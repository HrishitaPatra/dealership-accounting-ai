package com.tekion.accounting.service;

import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.repository.DepositBatchRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositBatchServiceTest {

    @Mock
    private DepositBatchRepository depositBatchRepository;

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private DepositBatchService depositBatchService;

    private DepositBatch testBatch;
    private Receipt testReceipt1;
    private Receipt testReceipt2;

    @BeforeEach
    void setUp() {
        testReceipt1 = Receipt.builder()
                .id("receipt-1")
                .receiptNumber("RCT-001")
                .amount(54.0)
                .status("UNBATCHED")
                .build();

        testReceipt2 = Receipt.builder()
                .id("receipt-2")
                .receiptNumber("RCT-002")
                .amount(108.0)
                .status("UNBATCHED")
                .build();

        testBatch = DepositBatch.builder()
                .id("batch-123")
                .batchNumber("BATCH-001")
                .receiptIds(Arrays.asList("receipt-1", "receipt-2"))
                .total(162.0)
                .status("OPEN")
                .reconciled(false)
                .build();
    }

    @Test
    void testCreateDepositBatch_Success() {
        // Arrange
        List<String> receiptIds = Arrays.asList("receipt-1", "receipt-2");
        when(receiptService.getReceiptById("receipt-1")).thenReturn(testReceipt1);
        when(receiptService.getReceiptById("receipt-2")).thenReturn(testReceipt2);
        when(depositBatchRepository.count()).thenReturn(0L);
        when(depositBatchRepository.save(any(DepositBatch.class))).thenAnswer(invocation -> {
            DepositBatch batch = invocation.getArgument(0);
            batch.setId("batch-123");
            return batch;
        });
        doNothing().when(receiptService).markReceiptAsBatched(anyString());

        // Act
        DepositBatch result = depositBatchService.createDepositBatch(receiptIds);

        // Assert
        assertNotNull(result);
        assertEquals("BATCH-001", result.getBatchNumber());
        assertEquals(162.0, result.getTotal());
        assertEquals("OPEN", result.getStatus());
        assertFalse(result.getReconciled());

        verify(receiptService, times(1)).getReceiptById("receipt-1");
        verify(receiptService, times(1)).getReceiptById("receipt-2");
        verify(depositBatchRepository, times(1)).save(any(DepositBatch.class));
        verify(receiptService, times(2)).markReceiptAsBatched(anyString());
    }

    @Test
    void testCreateDepositBatch_EmptyReceiptList() {
        // Arrange
        List<String> receiptIds = Arrays.asList();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            depositBatchService.createDepositBatch(receiptIds);
        });

        assertTrue(exception.getMessage().contains("no receipts provided"));
        verify(depositBatchRepository, never()).save(any(DepositBatch.class));
    }

    @Test
    void testCreateDepositBatch_ReceiptAlreadyBatched() {
        // Arrange
        testReceipt1.setStatus("BATCHED");
        List<String> receiptIds = Arrays.asList("receipt-1");
        when(receiptService.getReceiptById("receipt-1")).thenReturn(testReceipt1);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            depositBatchService.createDepositBatch(receiptIds);
        });

        assertTrue(exception.getMessage().contains("Receipt already batched"));
        verify(depositBatchRepository, never()).save(any(DepositBatch.class));
    }

    @Test
    void testMarkBatchAsDeposited_Success() {
        // Arrange
        when(depositBatchRepository.findById("batch-123")).thenReturn(Optional.of(testBatch));
        when(depositBatchRepository.save(any(DepositBatch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        DepositBatch result = depositBatchService.markBatchAsDeposited("batch-123");

        // Assert
        assertNotNull(result);
        assertEquals("DEPOSITED", result.getStatus());
        assertNotNull(result.getDepositedDate());

        verify(depositBatchRepository, times(1)).findById("batch-123");
        verify(depositBatchRepository, times(1)).save(any(DepositBatch.class));
    }

    @Test
    void testMarkBatchAsDeposited_NotFound() {
        // Arrange
        when(depositBatchRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            depositBatchService.markBatchAsDeposited("non-existent");
        });

        assertTrue(exception.getMessage().contains("Deposit batch not found"));
        verify(depositBatchRepository, times(1)).findById("non-existent");
        verify(depositBatchRepository, never()).save(any(DepositBatch.class));
    }

    @Test
    void testMarkBatchAsDeposited_AlreadyDeposited() {
        // Arrange
        testBatch.setStatus("DEPOSITED");
        when(depositBatchRepository.findById("batch-123")).thenReturn(Optional.of(testBatch));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            depositBatchService.markBatchAsDeposited("batch-123");
        });

        assertTrue(exception.getMessage().contains("already deposited"));
        verify(depositBatchRepository, times(1)).findById("batch-123");
        verify(depositBatchRepository, never()).save(any(DepositBatch.class));
    }

    @Test
    void testGetUnreconciledBatches() {
        // Arrange
        List<DepositBatch> mockBatches = Arrays.asList(testBatch);
        when(depositBatchRepository.findByDealershipIdAndStatusAndReconciledOrderByDepositedDateDesc(
                "DEALER-001", "DEPOSITED", false)).thenReturn(mockBatches);

        // Act
        List<DepositBatch> result = depositBatchService.getUnreconciledBatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(depositBatchRepository, times(1))
                .findByDealershipIdAndStatusAndReconciledOrderByDepositedDateDesc("DEALER-001", "DEPOSITED", false);
    }

    @Test
    void testGetAllDepositBatches() {
        // Arrange
        List<DepositBatch> mockBatches = Arrays.asList(testBatch);
        when(depositBatchRepository.findByDealershipId("DEALER-001")).thenReturn(mockBatches);

        // Act
        List<DepositBatch> result = depositBatchService.getAllDepositBatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(depositBatchRepository, times(1)).findByDealershipId("DEALER-001");
    }

    @Test
    void testGetDepositBatchById_Success() {
        // Arrange
        when(depositBatchRepository.findById("batch-123")).thenReturn(Optional.of(testBatch));

        // Act
        DepositBatch result = depositBatchService.getDepositBatchById("batch-123");

        // Assert
        assertNotNull(result);
        assertEquals("batch-123", result.getId());
        verify(depositBatchRepository, times(1)).findById("batch-123");
    }

    @Test
    void testMarkBatchAsReconciled() {
        // Arrange
        when(depositBatchRepository.findById("batch-123")).thenReturn(Optional.of(testBatch));
        when(depositBatchRepository.save(any(DepositBatch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        depositBatchService.markBatchAsReconciled("batch-123");

        // Assert
        assertTrue(testBatch.getReconciled());
        verify(depositBatchRepository, times(1)).findById("batch-123");
        verify(depositBatchRepository, times(1)).save(testBatch);
    }
}

