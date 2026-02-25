package com.tekion.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.service.BankTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankTransactionController.class)
class BankTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BankTransactionService bankTransactionService;

    private BankTransaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = BankTransaction.builder()
                .id("txn-123")
                .transactionId("BANK-TXN-001")
                .date(LocalDate.now())
                .description("Test Deposit")
                .amount(100.0)
                .type("CREDIT")
                .status("UNMATCHED")
                .build();
    }

    @Test
    void testGetAllBankTransactions() throws Exception {
        // Arrange
        List<BankTransaction> transactions = Arrays.asList(testTransaction);
        when(bankTransactionService.getAllBankTransactions()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/bank-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("txn-123"))
                .andExpect(jsonPath("$[0].transactionId").value("BANK-TXN-001"));
    }

    @Test
    void testGetUnmatchedTransactions() throws Exception {
        // Arrange
        List<BankTransaction> transactions = Arrays.asList(testTransaction);
        when(bankTransactionService.getUnmatchedTransactions()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/bank-transactions/unmatched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("UNMATCHED"));
    }
}

