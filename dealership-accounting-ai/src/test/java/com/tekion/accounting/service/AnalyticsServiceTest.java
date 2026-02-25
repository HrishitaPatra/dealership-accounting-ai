package com.tekion.accounting.service;

import com.tekion.accounting.service.AnalyticsService.ForecastResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsService
 */
@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private AnalyticsDataService analyticsDataService;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        // Setup is done by Mockito
    }

    @Test
    void testForecastBankTransactions_CallsDataService() {
        // Given
        List<AnalyticsDataService.BankTransactionData> mockData = createMockBankTransactionData();
        when(analyticsDataService.generateHistoricalBankTransactions()).thenReturn(mockData);

        // When
        ForecastResult result = analyticsService.forecastBankTransactions();

        // Then
        assertNotNull(result);
        verify(analyticsDataService, times(1)).generateHistoricalBankTransactions();
    }

    @Test
    void testForecastExceptionResolution_CallsDataService() {
        // Given
        List<AnalyticsDataService.ExceptionData> mockData = createMockExceptionData();
        when(analyticsDataService.generateHistoricalExceptions()).thenReturn(mockData);

        // When
        ForecastResult result = analyticsService.forecastExceptionResolution();

        // Then
        assertNotNull(result);
        verify(analyticsDataService, times(1)).generateHistoricalExceptions();
    }

    @Test
    void testForecastBankTransactions_ReturnsNonNullResult() {
        // Given
        List<AnalyticsDataService.BankTransactionData> mockData = createMockBankTransactionData();
        when(analyticsDataService.generateHistoricalBankTransactions()).thenReturn(mockData);

        // When
        ForecastResult result = analyticsService.forecastBankTransactions();

        // Then
        assertNotNull(result);
        assertNotNull(result.getSuccess());
        assertNotNull(result.getForecastType());
    }

    @Test
    void testForecastExceptionResolution_ReturnsNonNullResult() {
        // Given
        List<AnalyticsDataService.ExceptionData> mockData = createMockExceptionData();
        when(analyticsDataService.generateHistoricalExceptions()).thenReturn(mockData);

        // When
        ForecastResult result = analyticsService.forecastExceptionResolution();

        // Then
        assertNotNull(result);
        assertNotNull(result.getSuccess());
        assertNotNull(result.getForecastType());
    }

    @Test
    void testForecastBankTransactions_HandlesDataServiceCall() {
        // Given
        List<AnalyticsDataService.BankTransactionData> mockData = createMockBankTransactionData();
        when(analyticsDataService.generateHistoricalBankTransactions()).thenReturn(mockData);

        // When
        analyticsService.forecastBankTransactions();

        // Then
        verify(analyticsDataService, times(1)).generateHistoricalBankTransactions();
        verifyNoMoreInteractions(analyticsDataService);
    }

    @Test
    void testForecastExceptionResolution_HandlesDataServiceCall() {
        // Given
        List<AnalyticsDataService.ExceptionData> mockData = createMockExceptionData();
        when(analyticsDataService.generateHistoricalExceptions()).thenReturn(mockData);

        // When
        analyticsService.forecastExceptionResolution();

        // Then
        verify(analyticsDataService, times(1)).generateHistoricalExceptions();
        verifyNoMoreInteractions(analyticsDataService);
    }

    // Helper methods to create mock data

    private List<AnalyticsDataService.BankTransactionData> createMockBankTransactionData() {
        List<AnalyticsDataService.BankTransactionData> mockData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String date = LocalDate.now().minusDays(i).toString();
            Double amount = 100.0 + i;
            String type = "CREDIT";
            mockData.add(new AnalyticsDataService.BankTransactionData(date, amount, type));
        }
        return mockData;
    }

    private List<AnalyticsDataService.ExceptionData> createMockExceptionData() {
        List<AnalyticsDataService.ExceptionData> mockData = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String date = LocalDate.now().minusMonths(i).toString();
            String status = i % 2 == 0 ? "RESOLVED" : "PENDING";
            String type = "MERCHANT_FEE";
            Double amount = 50.0 + i * 10;
            mockData.add(new AnalyticsDataService.ExceptionData(date, status, type, amount));
        }
        return mockData;
    }
}

