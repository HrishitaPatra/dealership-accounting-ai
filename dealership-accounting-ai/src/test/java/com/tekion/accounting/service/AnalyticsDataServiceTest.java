package com.tekion.accounting.service;

import com.tekion.accounting.service.AnalyticsDataService.BankTransactionData;
import com.tekion.accounting.service.AnalyticsDataService.ExceptionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnalyticsDataService
 */
@ExtendWith(MockitoExtension.class)
public class AnalyticsDataServiceTest {

    @InjectMocks
    private AnalyticsDataService analyticsDataService;

    @BeforeEach
    void setUp() {
        // No setup needed - service has no dependencies
    }

    @Test
    void testGenerateHistoricalBankTransactions_ReturnsCorrectNumberOfMonths() {
        // When
        List<BankTransactionData> result = analyticsDataService.generateHistoricalBankTransactions();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should have data for approximately 6 months (180 days)
        // Weekdays: ~130 days × 10-25 transactions = ~1,300-3,250 transactions
        // Weekends: ~50 days × 2-7 transactions = ~100-350 transactions
        // Total: ~1,400-3,600 transactions
        assertTrue(result.size() >= 1000, "Should have at least 1000 transactions");
        assertTrue(result.size() <= 5000, "Should have at most 5000 transactions");
    }

    @Test
    void testGenerateHistoricalBankTransactions_HasValidDates() {
        // When
        List<BankTransactionData> result = analyticsDataService.generateHistoricalBankTransactions();

        // Then
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (BankTransactionData data : result) {
            assertNotNull(data.getDate());
            LocalDate date = LocalDate.parse(data.getDate(), formatter);
            assertTrue(date.isAfter(sixMonthsAgo.minusDays(1)),
                    "Date should be within last 6 months");
            assertTrue(date.isBefore(today.plusDays(1)),
                    "Date should not be in the future");
        }
    }

    @Test
    void testGenerateHistoricalBankTransactions_HasValidAmounts() {
        // When
        List<BankTransactionData> result = analyticsDataService.generateHistoricalBankTransactions();

        // Then
        for (BankTransactionData data : result) {
            assertTrue(data.getAmount() >= 50.0, "Amount should be at least $50");
            // Max amount can be $550 * 1.3 (month-end) * 1.1 (mid-week) = ~786
            assertTrue(data.getAmount() <= 800.0, "Amount should be at most $800");
        }
    }

    @Test
    void testGenerateHistoricalBankTransactions_DatesAreOrdered() {
        // When
        List<BankTransactionData> result = analyticsDataService.generateHistoricalBankTransactions();

        // Then
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        for (int i = 1; i < result.size(); i++) {
            LocalDate prevDate = LocalDate.parse(result.get(i - 1).getDate(), formatter);
            LocalDate currDate = LocalDate.parse(result.get(i).getDate(), formatter);
            assertTrue(prevDate.isBefore(currDate) || prevDate.isEqual(currDate),
                    "Dates should be in chronological order");
        }
    }

    @Test
    void testGenerateHistoricalExceptions_ReturnsCorrectNumberOfMonths() {
        // When
        List<ExceptionData> result = analyticsDataService.generateHistoricalExceptions();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Should have 6 months of data, 15-25 exceptions per month
        // Total: 90-150 exceptions
        assertTrue(result.size() >= 80, "Should have at least 80 exceptions");
        assertTrue(result.size() <= 160, "Should have at most 160 exceptions");
    }

    @Test
    void testGenerateHistoricalExceptions_HasValidDates() {
        // When
        List<ExceptionData> result = analyticsDataService.generateHistoricalExceptions();

        // Then
        for (ExceptionData data : result) {
            assertNotNull(data.getDate());
            // Date should be in YYYY-MM-DD format
            assertTrue(data.getDate().matches("\\d{4}-\\d{2}-\\d{2}"),
                    "Date should be in YYYY-MM-DD format");
        }
    }

    @Test
    void testGenerateHistoricalExceptions_HasValidStatus() {
        // When
        List<ExceptionData> result = analyticsDataService.generateHistoricalExceptions();

        // Then
        for (ExceptionData data : result) {
            assertNotNull(data.getStatus());
            assertTrue(data.getStatus().equals("RESOLVED") || data.getStatus().equals("OPEN"),
                    "Status should be either RESOLVED or OPEN");
        }
    }

    @Test
    void testGenerateHistoricalExceptions_HasValidType() {
        // When
        List<ExceptionData> result = analyticsDataService.generateHistoricalExceptions();

        // Then
        for (ExceptionData data : result) {
            assertNotNull(data.getType());
            assertFalse(data.getType().isEmpty(), "Type should not be empty");
        }
    }

    @Test
    void testGenerateHistoricalExceptions_HasValidAmounts() {
        // When
        List<ExceptionData> result = analyticsDataService.generateHistoricalExceptions();

        // Then
        for (ExceptionData data : result) {
            assertNotNull(data.getAmount());
            assertTrue(data.getAmount() > 0, "Amount should be positive");
        }
    }
}

