package com.FMS.dashboard.Adapter;

import com.FMS.dashboard.dto.dashboard.DashboardSummaryResponse;
import com.FMS.dashboard.port.in.DashboardUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardQueryAdapter {

    private final DashboardUseCase dashboardUseCase;

    public DashboardSummaryResponse getSummary(int months) {
        log.debug("Adapter → getSummary | months={}", months);
        int safeMo = Math.max(1, Math.min(months, 24));
        if (safeMo != months) {
            log.warn("Adapter clamped months from {} to {}", months, safeMo);
        }

        return dashboardUseCase.getSummary(safeMo);
    }
}