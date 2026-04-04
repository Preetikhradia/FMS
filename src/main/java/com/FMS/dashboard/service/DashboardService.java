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
@Transactional(readOnly = true)
public class DashboardService implements DashboardUseCase {

    private final RecordRepository recordRepository;

    @Override
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public DashboardSummaryResponse getSummary(int months) {
        log.debug("DashboardService.getSummary | months={}", months);

        BigDecimal totalIncome   = resolveTotal(RecordType.INCOME);
        BigDecimal totalExpenses = resolveTotal(RecordType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryTotals = buildCategoryTotals();

        LocalDate trendsFrom = LocalDate.now().minusMonths(months);
        // ← pass typed enums instead of inline string literals
        List<MonthlyTrend> monthlyTrends = buildMonthlyTrends(
                trendsFrom, RecordType.INCOME, RecordType.EXPENSE);

        List<RecentActivity> recentActivity = buildRecentActivity();

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentActivity(recentActivity)
                .build();
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private BigDecimal resolveTotal(RecordType type) {
        BigDecimal result = recordRepository.sumByType(type);
        return result != null ? result : BigDecimal.ZERO;
    }

    private Map<String, BigDecimal> buildCategoryTotals() {
        List<Object[]> rows = recordRepository.getCategoryTotals();
        if (rows == null || rows.isEmpty()) return new LinkedHashMap<>();

        return rows.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> nullSafe((BigDecimal) row[1]),
                        BigDecimal::add,
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

    private List<MonthlyTrend> buildMonthlyTrends(
            LocalDate from, RecordType income, RecordType expense) {

        List<Object[]> rows = recordRepository.getMonthlyTrends(from, income, expense);
        if (rows == null || rows.isEmpty()) return List.of();

        return rows.stream()
                .map(row -> new MonthlyTrend(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        nullSafe((BigDecimal) row[2]),
                        nullSafe((BigDecimal) row[3])
                ))
                .toList();
    }

    private List<RecentActivity> buildRecentActivity() {
        return recordRepository
                .findByDeletedFalseOrderByDateDesc(PageRequest.of(0, 10))
                .stream()
                .map(r -> RecentActivity.builder()
                        .id(r.getId())
                        .amount(r.getAmount())
                        .type(r.getType())
                        .category(r.getCategory())
                        .date(r.getDate())
                        .build())
                .toList();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}