package com.tekion.accounting.service;

import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.repository.ReceiptRepository;
import com.tekion.accounting.repository.RepairOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private RepairOrder testRepairOrder;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        testRepairOrder = RepairOrder.builder()
                .id("ro-123")
                .roNumber("RO-001")
                .status("CLOSED")
                .total(108.0)
                .build();

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
    void testGenerateReceiptFromRO_Success() {
        // Arrange
        when(repairOrderRepository.findById("ro-123")).thenReturn(Optional.of(testRepairOrder));
        when(receiptRepository.findByRoId("ro-123")).thenReturn(new ArrayList<>());
        when(receiptRepository.count()).thenReturn(0L);
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> {
            Receipt r = invocation.getArgument(0);
            r.setId("receipt-123");
            return r;
        });

        // Act
        Receipt result = receiptService.generateReceiptFromRO("ro-123");

        // Assert
        assertNotNull(result);
        assertEquals("RCT-001", result.getReceiptNumber());
        assertEquals("ro-123", result.getRoId());
        assertEquals(108.0, result.getAmount());
        assertEquals("UNBATCHED", result.getStatus());

        verify(repairOrderRepository, times(1)).findById("ro-123");
        verify(receiptRepository, times(1)).findByRoId("ro-123");
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    @Test
    void testGenerateReceiptFromRO_RONotFound() {
        // Arrange
        when(repairOrderRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            receiptService.generateReceiptFromRO("non-existent");
        });

        assertTrue(exception.getMessage().contains("Repair order not found"));
        verify(repairOrderRepository, times(1)).findById("non-existent");
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void testGenerateReceiptFromRO_RONotClosed() {
        // Arrange
        testRepairOrder.setStatus("OPEN");
        when(repairOrderRepository.findById("ro-123")).thenReturn(Optional.of(testRepairOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            receiptService.generateReceiptFromRO("ro-123");
        });

        assertTrue(exception.getMessage().contains("RO not closed"));
        verify(repairOrderRepository, times(1)).findById("ro-123");
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void testGenerateReceiptFromRO_ReceiptAlreadyExists() {
        // Arrange
        when(repairOrderRepository.findById("ro-123")).thenReturn(Optional.of(testRepairOrder));
        when(receiptRepository.findByRoId("ro-123")).thenReturn(Arrays.asList(testReceipt));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            receiptService.generateReceiptFromRO("ro-123");
        });

        assertTrue(exception.getMessage().contains("Receipt already exists"));
        verify(repairOrderRepository, times(1)).findById("ro-123");
        verify(receiptRepository, times(1)).findByRoId("ro-123");
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void testGetUnbatchedReceipts() {
        // Arrange
        List<Receipt> mockReceipts = Arrays.asList(testReceipt);
        when(receiptRepository.findByDealershipIdAndStatusOrderByCreatedAtAsc("DEALER-001", "UNBATCHED"))
                .thenReturn(mockReceipts);

        // Act
        List<Receipt> result = receiptService.getUnbatchedReceipts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(receiptRepository, times(1))
                .findByDealershipIdAndStatusOrderByCreatedAtAsc("DEALER-001", "UNBATCHED");
    }

    @Test
    void testGetAllReceipts() {
        // Arrange
        List<Receipt> mockReceipts = Arrays.asList(testReceipt);
        when(receiptRepository.findByDealershipId("DEALER-001")).thenReturn(mockReceipts);

        // Act
        List<Receipt> result = receiptService.getAllReceipts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(receiptRepository, times(1)).findByDealershipId("DEALER-001");
    }

    @Test
    void testGetReceiptById_Success() {
        // Arrange
        when(receiptRepository.findById("receipt-123")).thenReturn(Optional.of(testReceipt));

        // Act
        Receipt result = receiptService.getReceiptById("receipt-123");

        // Assert
        assertNotNull(result);
        assertEquals("receipt-123", result.getId());
        verify(receiptRepository, times(1)).findById("receipt-123");
    }

    @Test
    void testGetReceiptById_NotFound() {
        // Arrange
        when(receiptRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            receiptService.getReceiptById("non-existent");
        });

        assertTrue(exception.getMessage().contains("Receipt not found"));
        verify(receiptRepository, times(1)).findById("non-existent");
    }

    @Test
    void testMarkReceiptAsBatched() {
        // Arrange
        when(receiptRepository.findById("receipt-123")).thenReturn(Optional.of(testReceipt));
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        receiptService.markReceiptAsBatched("receipt-123");

        // Assert
        assertEquals("BATCHED", testReceipt.getStatus());
        verify(receiptRepository, times(1)).findById("receipt-123");
        verify(receiptRepository, times(1)).save(testReceipt);
    }
}

