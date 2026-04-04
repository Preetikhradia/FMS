package com.FMS.dashboard.Controller;

import com.FMS.dashboard.Adapter.DashboardQueryAdapter;
import com.FMS.dashboard.dto.dashboard.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardQueryAdapter dashboardAdapter;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(dashboardAdapter.getSummary(months));
    }
}