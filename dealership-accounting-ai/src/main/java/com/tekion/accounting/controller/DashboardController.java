package com.tekion.accounting.controller;

import com.tekion.accounting.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Get dashboard metrics
     * GET /api/dashboard/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<DashboardService.DashboardMetrics> getDashboardMetrics() {
        log.info("REST: Getting dashboard metrics");
        DashboardService.DashboardMetrics metrics = dashboardService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }
}

