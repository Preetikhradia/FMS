package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.dashboard.DashboardSummaryResponse;
import com.FMS.dashboard.dto.dashboard.MonthlyTrend;
import com.FMS.dashboard.dto.dashboard.RecentActivity;
import com.FMS.dashboard.model.RecordType;
import com.FMS.dashboard.port.in.DashboardUseCase;
import com.FMS.dashboard.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)   // all queries — never opens a write transaction
public class DashboardService implements DashboardUseCase {

    private final RecordRepository recordRepository;

    @Override
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public DashboardSummaryResponse getSummary(int months) {
        log.debug("DashboardService.getSummary called | months={}", months);

        BigDecimal totalIncome   = resolveTotal(RecordType.INCOME);
        BigDecimal totalExpenses = resolveTotal(RecordType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryTotals = buildCategoryTotals();

        LocalDate trendsFrom = LocalDate.now().minusMonths(months);
        List<MonthlyTrend> monthlyTrends = buildMonthlyTrends(trendsFrom);

        List<RecentActivity> recentActivity = buildRecentActivity();

        log.debug("Summary built | income={} expenses={} net={} categories={} trendMonths={}",
                totalIncome, totalExpenses, netBalance,
                categoryTotals.size(), monthlyTrends.size());

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentActivity(recentActivity)
                .build();
    }

    // ── private builders ──────────────────────────────────────────────────────

    /**
     * Returns COALESCE(SUM, 0) — never null, safe to do arithmetic on directly.
     */
    private BigDecimal resolveTotal(RecordType type) {
        BigDecimal result = recordRepository.sumByType(type);
        return result != null ? result : BigDecimal.ZERO;
    }

    /**
     * Builds a category → total map.
     * Entries are sorted descending by value so the dashboard can render a
     * ranked breakdown without doing any extra work on the frontend.
     */
    private Map<String, BigDecimal> buildCategoryTotals() {
        return recordRepository.getCategoryTotals()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String)     row[0],   // category name
                        row -> (BigDecimal) row[1],   // summed amount
                        BigDecimal::add,              // merge (shouldn't occur — just safe)
                        LinkedHashMap::new
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /**
     * Maps raw Object[] rows from the JPQL aggregate into typed MonthlyTrend DTOs.
     * Row layout (must match repository @Query projection):
     *   [0] = YEAR(r.date)   → Integer
     *   [1] = MONTH(r.date)  → Integer
     *   [2] = SUM income     → BigDecimal
     *   [3] = SUM expense    → BigDecimal
     */
    private List<MonthlyTrend> buildMonthlyTrends(LocalDate from) {
        return recordRepository.getMonthlyTrends(from)
                .stream()
                .map(row -> new MonthlyTrend(
                        ((Number) row[0]).intValue(),              // year
                        ((Number) row[1]).intValue(),              // month
                        nullSafe((BigDecimal) row[2]),             // income
                        nullSafe((BigDecimal) row[3])              // expenses
                ))
                .toList();
    }

    /**
     * Fetches the 10 most recent non-deleted records for the activity feed.
     */
    private List<RecentActivity> buildRecentActivity() {
        return recordRepository
                .findByDeletedFalseOrderByDateDesc(PageRequest.of(0, 10))
                .stream()
                .map(record -> RecentActivity.builder()
                        .id(record.getId())
                        .amount(record.getAmount())
                        .type(record.getType())
                        .category(record.getCategory())
                        .date(record.getDate())
                        .build())
                .toList();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}