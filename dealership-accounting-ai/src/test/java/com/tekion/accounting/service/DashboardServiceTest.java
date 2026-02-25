package com.tekion.accounting.service;

import com.tekion.accounting.repository.BankTransactionRepository;
import com.tekion.accounting.repository.DepositBatchRepository;
import com.tekion.accounting.repository.ExceptionRepository;
import com.tekion.accounting.repository.ReceiptRepository;
import com.tekion.accounting.repository.RepairOrderRepository;
import com.tekion.accounting.service.DashboardService.DashboardMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private DepositBatchRepository depositBatchRepository;

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @Mock
    private ExceptionRepository exceptionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetDashboardMetrics_AllZero() {
        // Arrange
        when(repairOrderRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(0L);
        when(receiptRepository.countByDealershipIdAndStatus("DEALER-001", "UNBATCHED")).thenReturn(0L);
        when(depositBatchRepository.countByDealershipIdAndReconciled("DEALER-001", false)).thenReturn(0L);
        when(bankTransactionRepository.countByDealershipIdAndStatus("DEALER-001", "UNMATCHED")).thenReturn(0L);
        when(exceptionRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(0L);

        // Act
        DashboardMetrics result = dashboardService.getDashboardMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getOpenRepairOrders());
        assertEquals(0, result.getUnbatchedReceipts());
        assertEquals(0, result.getUnreconciledBatches());
        assertEquals(0, result.getUnmatchedBankTransactions());
        assertEquals(0, result.getOpenExceptions());
        assertEquals(100, result.getCloseReadinessPercentage()); // 100% ready when all zero

        verify(repairOrderRepository, times(1)).countByDealershipIdAndStatus("DEALER-001", "OPEN");
        verify(receiptRepository, times(1)).countByDealershipIdAndStatus("DEALER-001", "UNBATCHED");
        verify(depositBatchRepository, times(1)).countByDealershipIdAndReconciled("DEALER-001", false);
        verify(bankTransactionRepository, times(1)).countByDealershipIdAndStatus("DEALER-001", "UNMATCHED");
        verify(exceptionRepository, times(1)).countByDealershipIdAndStatus("DEALER-001", "OPEN");
    }

    @Test
    void testGetDashboardMetrics_WithUnbatchedReceipts() {
        // Arrange
        when(repairOrderRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(5L);
        when(receiptRepository.countByDealershipIdAndStatus("DEALER-001", "UNBATCHED")).thenReturn(3L);
        when(depositBatchRepository.countByDealershipIdAndReconciled("DEALER-001", false)).thenReturn(0L);
        when(bankTransactionRepository.countByDealershipIdAndStatus("DEALER-001", "UNMATCHED")).thenReturn(2L);
        when(exceptionRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(0L);

        // Act
        DashboardMetrics result = dashboardService.getDashboardMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getOpenRepairOrders());
        assertEquals(3, result.getUnbatchedReceipts());
        assertEquals(0, result.getUnreconciledBatches());
        assertEquals(2, result.getUnmatchedBankTransactions());
        assertEquals(0, result.getOpenExceptions());
        assertEquals(70, result.getCloseReadinessPercentage()); // 100 - 30 (unbatched receipts)
    }

    @Test
    void testGetDashboardMetrics_WithUnreconciledBatches() {
        // Arrange
        when(repairOrderRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(2L);
        when(receiptRepository.countByDealershipIdAndStatus("DEALER-001", "UNBATCHED")).thenReturn(0L);
        when(depositBatchRepository.countByDealershipIdAndReconciled("DEALER-001", false)).thenReturn(4L);
        when(bankTransactionRepository.countByDealershipIdAndStatus("DEALER-001", "UNMATCHED")).thenReturn(1L);
        when(exceptionRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(0L);

        // Act
        DashboardMetrics result = dashboardService.getDashboardMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getOpenRepairOrders());
        assertEquals(0, result.getUnbatchedReceipts());
        assertEquals(4, result.getUnreconciledBatches());
        assertEquals(1, result.getUnmatchedBankTransactions());
        assertEquals(0, result.getOpenExceptions());
        assertEquals(60, result.getCloseReadinessPercentage()); // 100 - 40 (unreconciled batches)
    }

    @Test
    void testGetDashboardMetrics_WithOpenExceptions() {
        // Arrange
        when(repairOrderRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(1L);
        when(receiptRepository.countByDealershipIdAndStatus("DEALER-001", "UNBATCHED")).thenReturn(0L);
        when(depositBatchRepository.countByDealershipIdAndReconciled("DEALER-001", false)).thenReturn(0L);
        when(bankTransactionRepository.countByDealershipIdAndStatus("DEALER-001", "UNMATCHED")).thenReturn(0L);
        when(exceptionRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(2L);

        // Act
        DashboardMetrics result = dashboardService.getDashboardMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOpenRepairOrders());
        assertEquals(0, result.getUnbatchedReceipts());
        assertEquals(0, result.getUnreconciledBatches());
        assertEquals(0, result.getUnmatchedBankTransactions());
        assertEquals(2, result.getOpenExceptions());
        assertEquals(70, result.getCloseReadinessPercentage()); // 100 - 30 (open exceptions)
    }

    @Test
    void testGetDashboardMetrics_AllPending() {
        // Arrange
        when(repairOrderRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(10L);
        when(receiptRepository.countByDealershipIdAndStatus("DEALER-001", "UNBATCHED")).thenReturn(5L);
        when(depositBatchRepository.countByDealershipIdAndReconciled("DEALER-001", false)).thenReturn(3L);
        when(bankTransactionRepository.countByDealershipIdAndStatus("DEALER-001", "UNMATCHED")).thenReturn(7L);
        when(exceptionRepository.countByDealershipIdAndStatus("DEALER-001", "OPEN")).thenReturn(2L);

        // Act
        DashboardMetrics result = dashboardService.getDashboardMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getOpenRepairOrders());
        assertEquals(5, result.getUnbatchedReceipts());
        assertEquals(3, result.getUnreconciledBatches());
        assertEquals(7, result.getUnmatchedBankTransactions());
        assertEquals(2, result.getOpenExceptions());
        assertEquals(0, result.getCloseReadinessPercentage()); // 100 - 30 - 40 - 30 = 0
    }
}

