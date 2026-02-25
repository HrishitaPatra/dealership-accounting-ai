package com.tekion.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.service.DepositBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepositBatchController.class)
class DepositBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositBatchService depositBatchService;

    private DepositBatch testBatch;

    @BeforeEach
    void setUp() {
        testBatch = DepositBatch.builder()
                .id("batch-123")
                .batchNumber("BATCH-001")
                .receiptIds(Arrays.asList("receipt-1", "receipt-2"))
                .total(216.0)
                .status("OPEN")
                .reconciled(false)
                .build();
    }

    @Test
    void testCreateDepositBatch() throws Exception {
        // Arrange
        when(depositBatchService.createDepositBatch(any(List.class))).thenReturn(testBatch);

        String requestBody = "{\"receiptIds\":[\"receipt-1\",\"receipt-2\"]}";

        // Act & Assert
        mockMvc.perform(post("/api/deposit-batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("batch-123"))
                .andExpect(jsonPath("$.batchNumber").value("BATCH-001"))
                .andExpect(jsonPath("$.total").value(216.0));
    }

    @Test
    void testGetAllDepositBatches() throws Exception {
        // Arrange
        List<DepositBatch> batches = Arrays.asList(testBatch);
        when(depositBatchService.getAllDepositBatches()).thenReturn(batches);

        // Act & Assert
        mockMvc.perform(get("/api/deposit-batches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("batch-123"))
                .andExpect(jsonPath("$[0].batchNumber").value("BATCH-001"));
    }

    @Test
    void testGetUnreconciledBatches() throws Exception {
        // Arrange
        List<DepositBatch> batches = Arrays.asList(testBatch);
        when(depositBatchService.getUnreconciledBatches()).thenReturn(batches);

        // Act & Assert
        mockMvc.perform(get("/api/deposit-batches/unreconciled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reconciled").value(false));
    }

    @Test
    void testMarkBatchAsDeposited() throws Exception {
        // Arrange
        testBatch.setStatus("DEPOSITED");
        when(depositBatchService.markBatchAsDeposited("batch-123")).thenReturn(testBatch);

        // Act & Assert
        mockMvc.perform(put("/api/deposit-batches/batch-123/deposit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEPOSITED"));
    }
}

