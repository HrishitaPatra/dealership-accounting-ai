package com.tekion.accounting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekion.accounting.service.AnalyticsDataService.BankTransactionData;
import com.tekion.accounting.service.AnalyticsDataService.ExceptionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for analytics and forecasting using Python ARIMA/SARIMAX models
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final AnalyticsDataService analyticsDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Forecast bank transaction amounts for next 30 days using SARIMAX
     */
    public ForecastResult forecastBankTransactions() {
        log.info("Generating bank transaction forecast");
        
        try {
            // Generate historical data
            List<BankTransactionData> historicalData = analyticsDataService.generateHistoricalBankTransactions();
            
            // Prepare input for Python script
            Map<String, Object> input = new HashMap<>();
            input.put("type", "bank_transactions");
            input.put("data", historicalData);
            
            // Call Python script
            String pythonOutput = callPythonScript(input);
            
            // Parse result
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(pythonOutput, Map.class);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                log.info("Bank transaction forecast generated successfully");
                return ForecastResult.builder()
                        .success(true)
                        .forecastType("Bank Transactions")
                        .modelType((String) result.get("model_type"))
                        .historical(result.get("historical"))
                        .forecast(result.get("forecast"))
                        .build();
            } else {
                log.error("Python forecast failed: {}", result.get("error"));
                return ForecastResult.builder()
                        .success(false)
                        .error((String) result.get("error"))
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error generating bank transaction forecast", e);
            return ForecastResult.builder()
                    .success(false)
                    .error("Failed to generate forecast: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Forecast exception resolution rate for next 6 months using ARIMA
     */
    public ForecastResult forecastExceptionResolution() {
        log.info("Generating exception resolution forecast");
        
        try {
            // Generate historical data
            List<ExceptionData> historicalData = analyticsDataService.generateHistoricalExceptions();
            
            // Prepare input for Python script
            Map<String, Object> input = new HashMap<>();
            input.put("type", "exception_resolution");
            input.put("data", historicalData);
            
            // Call Python script
            String pythonOutput = callPythonScript(input);
            
            // Parse result
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(pythonOutput, Map.class);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                log.info("Exception resolution forecast generated successfully");
                return ForecastResult.builder()
                        .success(true)
                        .forecastType("Exception Resolution Rate")
                        .modelType((String) result.get("model_type"))
                        .historical(result.get("historical"))
                        .forecast(result.get("forecast"))
                        .build();
            } else {
                log.error("Python forecast failed: {}", result.get("error"));
                return ForecastResult.builder()
                        .success(false)
                        .error((String) result.get("error"))
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error generating exception resolution forecast", e);
            return ForecastResult.builder()
                    .success(false)
                    .error("Failed to generate forecast: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Call Python forecast script via ProcessBuilder
     */
    private String callPythonScript(Map<String, Object> input) throws Exception {
        // Get project root directory
        String projectRoot = Paths.get("").toAbsolutePath().toString();
        String pythonPath = projectRoot + "/venv/bin/python3";
        String scriptPath = projectRoot + "/forecast.py";
        
        log.info("Calling Python script: {} {}", pythonPath, scriptPath);
        
        // Build process
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Write input JSON to stdin
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            String inputJson = objectMapper.writeValueAsString(input);
            writer.write(inputJson);
            writer.flush();
        }
        
        // Read output from stdout
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        
        // Wait for process to complete
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("Python script exited with code: " + exitCode);
        }
        
        return output.toString();
    }
    
    /**
     * DTO for forecast results
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class ForecastResult {
        private Boolean success;
        private String forecastType;
        private String modelType;
        private Object historical;
        private Object forecast;
        private String error;
    }
}

