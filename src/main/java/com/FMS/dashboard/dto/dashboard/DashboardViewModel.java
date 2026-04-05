package com.FMS.dashboard.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardViewModel {
    private String totalIncome;
    private String totalExpenses;
    private String netBalance;
    private boolean netPositive;
    private int categoryCount;
    private Map<String, String> categoryTotals;
    private List<MonthlyTrend> monthlyTrends;
    private List<RecentActivity> recentActivity;
}