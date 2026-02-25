package com.tekion.accounting.service;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.model.ReconciliationMatch;
import com.tekion.accounting.repository.ReconciliationMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    private ReconciliationMatchRepository reconciliationMatchRepository;

    @Mock
    private BankTransactionService bankTransactionService;

    @Mock
    private DepositBatchService depositBatchService;

    @InjectMocks
    private ReconciliationService reconciliationService;

    private BankTransaction testBankTransaction;
    private DepositBatch testDepositBatch;
    private ReconciliationMatch testMatch;

    @BeforeEach
    void setUp() {
        testBankTransaction = BankTransaction.builder()
                .id("txn-123")
                .transactionId("BANK-TXN-001")
                .amount(100.0)
                .status("UNMATCHED")
                .build();

        testDepositBatch = DepositBatch.builder()
                .id("batch-123")
                .batchNumber("BATCH-001")
                .total(100.0)
                .reconciled(false)
                .build();

        testMatch = ReconciliationMatch.builder()
                .id("match-123")
                .bankTransactionId("txn-123")
                .depositBatchId("batch-123")
                .aiSuggested(true)
                .aiConfidence(95)
                .userConfirmed(true)
                .build();
    }

    @Test
    void testConfirmMatch_Success() {
        // Arrange
        when(bankTransactionService.getBankTransactionById("txn-123")).thenReturn(testBankTransaction);
        when(depositBatchService.getDepositBatchById("batch-123")).thenReturn(testDepositBatch);
        when(reconciliationMatchRepository.save(any(ReconciliationMatch.class))).thenAnswer(invocation -> {
            ReconciliationMatch match = invocation.getArgument(0);
            match.setId("match-123");
            return match;
        });
        doNothing().when(bankTransactionService).markTransactionAsMatched("txn-123");
        doNothing().when(depositBatchService).markBatchAsReconciled("batch-123");

        // Act
        ReconciliationMatch result = reconciliationService.confirmMatch(
                "txn-123", "batch-123", true, 95, Arrays.asList("Exact amount match")
        );

        // Assert
        assertNotNull(result);
        assertEquals("txn-123", result.getBankTransactionId());
        assertEquals("batch-123", result.getDepositBatchId());
        assertTrue(result.getAiSuggested());
        assertEquals(95, result.getAiConfidence());
        assertTrue(result.getUserConfirmed());

        verify(bankTransactionService, times(1)).getBankTransactionById("txn-123");
        verify(depositBatchService, times(1)).getDepositBatchById("batch-123");
        verify(reconciliationMatchRepository, times(1)).save(any(ReconciliationMatch.class));
        verify(bankTransactionService, times(1)).markTransactionAsMatched("txn-123");
        verify(depositBatchService, times(1)).markBatchAsReconciled("batch-123");
    }

    @Test
    void testConfirmMatch_BankTransactionAlreadyMatched() {
        // Arrange
        testBankTransaction.setStatus("MATCHED");
        when(bankTransactionService.getBankTransactionById("txn-123")).thenReturn(testBankTransaction);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reconciliationService.confirmMatch("txn-123", "batch-123", false, null, null);
        });

        assertTrue(exception.getMessage().contains("already matched"));
        verify(reconciliationMatchRepository, never()).save(any(ReconciliationMatch.class));
    }

    @Test
    void testConfirmMatch_DepositBatchAlreadyReconciled() {
        // Arrange
        testDepositBatch.setReconciled(true);
        when(bankTransactionService.getBankTransactionById("txn-123")).thenReturn(testBankTransaction);
        when(depositBatchService.getDepositBatchById("batch-123")).thenReturn(testDepositBatch);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reconciliationService.confirmMatch("txn-123", "batch-123", false, null, null);
        });

        assertTrue(exception.getMessage().contains("already reconciled"));
        verify(reconciliationMatchRepository, never()).save(any(ReconciliationMatch.class));
    }

    @Test
    void testGetAllMatches() {
        // Arrange
        List<ReconciliationMatch> mockMatches = Arrays.asList(testMatch);
        when(reconciliationMatchRepository.findByDealershipIdOrderByMatchedAtDesc("DEALER-001"))
                .thenReturn(mockMatches);

        // Act
        List<ReconciliationMatch> result = reconciliationService.getAllMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reconciliationMatchRepository, times(1))
                .findByDealershipIdOrderByMatchedAtDesc("DEALER-001");
    }

    @Test
    void testGetAISuggestedMatches() {
        // Arrange
        List<ReconciliationMatch> mockMatches = Arrays.asList(testMatch);
        when(reconciliationMatchRepository.findByDealershipIdAndAiSuggested("DEALER-001", true))
                .thenReturn(mockMatches);

        // Act
        List<ReconciliationMatch> result = reconciliationService.getAISuggestedMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reconciliationMatchRepository, times(1))
                .findByDealershipIdAndAiSuggested("DEALER-001", true);
    }

    @Test
    void testGetMatchByBankTransactionId_Found() {
        // Arrange
        when(reconciliationMatchRepository.findByBankTransactionId("txn-123"))
                .thenReturn(Optional.of(testMatch));

        // Act
        ReconciliationMatch result = reconciliationService.getMatchByBankTransactionId("txn-123");

        // Assert
        assertNotNull(result);
        assertEquals("txn-123", result.getBankTransactionId());
        verify(reconciliationMatchRepository, times(1)).findByBankTransactionId("txn-123");
    }

    @Test
    void testGetMatchByBankTransactionId_NotFound() {
        // Arrange
        when(reconciliationMatchRepository.findByBankTransactionId("non-existent"))
                .thenReturn(Optional.empty());

        // Act
        ReconciliationMatch result = reconciliationService.getMatchByBankTransactionId("non-existent");

        // Assert
        assertNull(result);
        verify(reconciliationMatchRepository, times(1)).findByBankTransactionId("non-existent");
    }

    @Test
    void testGetMatchByDepositBatchId_Found() {
        // Arrange
        when(reconciliationMatchRepository.findByDepositBatchId("batch-123"))
                .thenReturn(Optional.of(testMatch));

        // Act
        ReconciliationMatch result = reconciliationService.getMatchByDepositBatchId("batch-123");

        // Assert
        assertNotNull(result);
        assertEquals("batch-123", result.getDepositBatchId());
        verify(reconciliationMatchRepository, times(1)).findByDepositBatchId("batch-123");
    }

    @Test
    void testGetMatchByDepositBatchId_NotFound() {
        // Arrange
        when(reconciliationMatchRepository.findByDepositBatchId("non-existent"))
                .thenReturn(Optional.empty());

        // Act
        ReconciliationMatch result = reconciliationService.getMatchByDepositBatchId("non-existent");

        // Assert
        assertNull(result);
        verify(reconciliationMatchRepository, times(1)).findByDepositBatchId("non-existent");
    }
}

