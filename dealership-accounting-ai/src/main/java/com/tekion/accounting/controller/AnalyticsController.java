package com.tekion.accounting.controller;

import com.tekion.accounting.service.AnalyticsService;
import com.tekion.accounting.service.AnalyticsService.ForecastResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Analytics and Forecasting
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Get bank transaction forecast for next 30 days
     * GET /api/analytics/forecast/bank-transactions
     */
    @GetMapping("/forecast/bank-transactions")
    public ResponseEntity<ForecastResult> forecastBankTransactions() {
        log.info("REST: Getting bank transaction forecast");
        ForecastResult result = analyticsService.forecastBankTransactions();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get exception resolution rate forecast for next 6 months
     * GET /api/analytics/forecast/exception-resolution
     */
    @GetMapping("/forecast/exception-resolution")
    public ResponseEntity<ForecastResult> forecastExceptionResolution() {
        log.info("REST: Getting exception resolution forecast");
        ForecastResult result = analyticsService.forecastExceptionResolution();
        return ResponseEntity.ok(result);
    }
}

