package com.FMS.dashboard.port.in;

import com.FMS.dashboard.dto.dashboard.DashboardSummaryResponse;

public interface DashboardUseCase {
    DashboardSummaryResponse getSummary(int months);
}