package com.tekion.accounting.service;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.repository.BankTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankTransactionServiceTest {

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @InjectMocks
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
                .build();
    }

    @Test
    void testGetUnmatchedTransactions() {
        // Arrange
        List<BankTransaction> mockTransactions = Arrays.asList(testTransaction);
        when(bankTransactionRepository.findByDealershipIdAndStatusOrderByDateDesc("DEALER-001", "UNMATCHED"))
                .thenReturn(mockTransactions);

        // Act
        List<BankTransaction> result = bankTransactionService.getUnmatchedTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bankTransactionRepository, times(1))
                .findByDealershipIdAndStatusOrderByDateDesc("DEALER-001", "UNMATCHED");
    }

    @Test
    void testGetAllBankTransactions() {
        // Arrange
        List<BankTransaction> mockTransactions = Arrays.asList(testTransaction);
        when(bankTransactionRepository.findByDealershipId("DEALER-001")).thenReturn(mockTransactions);

        // Act
        List<BankTransaction> result = bankTransactionService.getAllBankTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bankTransactionRepository, times(1)).findByDealershipId("DEALER-001");
    }

    @Test
    void testGetBankTransactionById_Success() {
        // Arrange
        when(bankTransactionRepository.findById("txn-123")).thenReturn(Optional.of(testTransaction));

        // Act
        BankTransaction result = bankTransactionService.getBankTransactionById("txn-123");

        // Assert
        assertNotNull(result);
        assertEquals("txn-123", result.getId());
        verify(bankTransactionRepository, times(1)).findById("txn-123");
    }

    @Test
    void testGetBankTransactionById_NotFound() {
        // Arrange
        when(bankTransactionRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankTransactionService.getBankTransactionById("non-existent");
        });

        assertTrue(exception.getMessage().contains("Bank transaction not found"));
        verify(bankTransactionRepository, times(1)).findById("non-existent");
    }

    @Test
    void testMarkTransactionAsMatched() {
        // Arrange
        when(bankTransactionRepository.findById("txn-123")).thenReturn(Optional.of(testTransaction));
        when(bankTransactionRepository.save(any(BankTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        bankTransactionService.markTransactionAsMatched("txn-123");

        // Assert
        assertEquals("MATCHED", testTransaction.getStatus());
        verify(bankTransactionRepository, times(1)).findById("txn-123");
        verify(bankTransactionRepository, times(1)).save(testTransaction);
    }
}

