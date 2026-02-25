package com.tekion.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReceiptController.class)
class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReceiptService receiptService;

    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        testReceipt = Receipt.builder()
                .id("receipt-123")
                .receiptNumber("RCT-001")
                .roId("ro-123")
                .roNumber("RO-001")
                .amount(108.0)
                .status("UNBATCHED")
                .build();
    }

    @Test
    void testGenerateReceipt() throws Exception {
        // Arrange
        when(receiptService.generateReceiptFromRO("ro-123")).thenReturn(testReceipt);

        String requestBody = "{\"roId\":\"ro-123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/receipts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("receipt-123"))
                .andExpect(jsonPath("$.receiptNumber").value("RCT-001"))
                .andExpect(jsonPath("$.roId").value("ro-123"));
    }

    @Test
    void testGetAllReceipts() throws Exception {
        // Arrange
        List<Receipt> receipts = Arrays.asList(testReceipt);
        when(receiptService.getAllReceipts()).thenReturn(receipts);

        // Act & Assert
        mockMvc.perform(get("/api/receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("receipt-123"))
                .andExpect(jsonPath("$[0].receiptNumber").value("RCT-001"));
    }

    @Test
    void testGetUnbatchedReceipts() throws Exception {
        // Arrange
        List<Receipt> receipts = Arrays.asList(testReceipt);
        when(receiptService.getUnbatchedReceipts()).thenReturn(receipts);

        // Act & Assert
        mockMvc.perform(get("/api/receipts/unbatched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("UNBATCHED"));
    }
}

