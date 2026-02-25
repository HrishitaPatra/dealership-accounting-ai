package com.tekion.accounting.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for generating simulated historical data for analytics/forecasting
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsDataService {
    
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    
    /**
     * Generate simulated bank transaction data for the past 6 months
     */
    public List<BankTransactionData> generateHistoricalBankTransactions() {
        log.info("Generating historical bank transaction data for past 6 months");
        
        List<BankTransactionData> transactions = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        
        // Generate daily transactions
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // More transactions on weekdays, fewer on weekends
            int dailyCount = date.getDayOfWeek().getValue() <= 5 ? 
                    random.nextInt(15) + 10 : random.nextInt(5) + 2;
            
            for (int i = 0; i < dailyCount; i++) {
                // Generate realistic transaction amounts
                double baseAmount = 50 + random.nextDouble() * 500; // $50-$550
                
                // Add some seasonal variation (higher at month-end)
                if (date.getDayOfMonth() >= 25) {
                    baseAmount *= 1.3; // 30% increase at month-end
                }
                
                // Add weekly pattern (higher mid-week)
                int dayOfWeek = date.getDayOfWeek().getValue();
                if (dayOfWeek >= 2 && dayOfWeek <= 4) {
                    baseAmount *= 1.1; // 10% increase mid-week
                }
                
                transactions.add(BankTransactionData.builder()
                        .date(date.toString())
                        .amount(Math.round(baseAmount * 100.0) / 100.0)
                        .type("CREDIT")
                        .build());
            }
        }
        
        log.info("Generated {} historical bank transactions", transactions.size());
        return transactions;
    }
    
    /**
     * Generate simulated exception data for the past 6 months
     */
    public List<ExceptionData> generateHistoricalExceptions() {
        log.info("Generating historical exception data for past 6 months");
        
        List<ExceptionData> exceptions = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        
        // Generate monthly exceptions with improving resolution rate
        for (int month = 0; month < 6; month++) {
            LocalDate monthStart = startDate.plusMonths(month);
            
            // Generate 15-25 exceptions per month
            int monthlyCount = random.nextInt(11) + 15;
            
            // Resolution rate improves over time (60% -> 85%)
            double resolutionRate = 0.60 + (month * 0.05);
            
            for (int i = 0; i < monthlyCount; i++) {
                // Random day in the month
                int dayOffset = random.nextInt(28);
                LocalDate exceptionDate = monthStart.plusDays(dayOffset);
                
                // Determine if resolved based on resolution rate
                boolean isResolved = random.nextDouble() < resolutionRate;
                
                exceptions.add(ExceptionData.builder()
                        .date(exceptionDate.toString())
                        .status(isResolved ? "RESOLVED" : "OPEN")
                        .type("UNMATCHED")
                        .amount(Math.round((50 + random.nextDouble() * 200) * 100.0) / 100.0)
                        .build());
            }
        }
        
        log.info("Generated {} historical exceptions", exceptions.size());
        return exceptions;
    }
    
    /**
     * DTO for bank transaction data
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class BankTransactionData {
        private String date;
        private Double amount;
        private String type;
    }
    
    /**
     * DTO for exception data
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class ExceptionData {
        private String date;
        private String status;
        private String type;
        private Double amount;
    }
}

