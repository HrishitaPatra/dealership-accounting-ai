package com.tekion.accounting.service;

import com.tekion.accounting.model.Customer;
import com.tekion.accounting.model.LineItem;
import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.model.Vehicle;
import com.tekion.accounting.repository.RepairOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @InjectMocks
    private RepairOrderService repairOrderService;

    private RepairOrder testRepairOrder;
    private Customer testCustomer;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Set tax rate using reflection
        ReflectionTestUtils.setField(repairOrderService, "taxRate", 0.08);

        // Create test customer
        testCustomer = Customer.builder()
                .name("John Doe")
                .phone("555-123-4567")
                .build();

        // Create test vehicle
        testVehicle = Vehicle.builder()
                .vin("1HGBH41JXMN109186")
                .make("Toyota")
                .model("Camry")
                .year("2023")
                .build();

        // Create test line items
        LineItem lineItem = LineItem.builder()
                .description("Oil Change")
                .quantity(1)
                .rate(50.0)
                .amount(50.0)
                .build();

        // Create test repair order
        testRepairOrder = RepairOrder.builder()
                .customer(testCustomer)
                .vehicle(testVehicle)
                .lineItems(new ArrayList<>(Arrays.asList(lineItem)))
                .build();
    }

    @Test
    void testCreateRepairOrder_Success() {
        // Arrange
        when(repairOrderRepository.count()).thenReturn(0L);
        when(repairOrderRepository.save(any(RepairOrder.class))).thenAnswer(invocation -> {
            RepairOrder ro = invocation.getArgument(0);
            ro.setId("test-id-123");
            return ro;
        });

        // Act
        RepairOrder result = repairOrderService.createRepairOrder(testRepairOrder);

        // Assert
        assertNotNull(result);
        assertEquals("DEALER-001", result.getDealershipId());
        assertEquals("RO-001", result.getRoNumber());
        assertEquals("OPEN", result.getStatus());
        assertNotNull(result.getSubtotal());
        assertNotNull(result.getTax());
        assertNotNull(result.getTotal());

        verify(repairOrderRepository, times(1)).count();
        verify(repairOrderRepository, times(1)).save(any(RepairOrder.class));
    }

    @Test
    void testCloseRepairOrder_Success() {
        // Arrange
        String roId = "test-id-123";
        testRepairOrder.setId(roId);
        testRepairOrder.setStatus("OPEN");
        testRepairOrder.setRoNumber("RO-001");

        when(repairOrderRepository.findById(roId)).thenReturn(Optional.of(testRepairOrder));
        when(repairOrderRepository.save(any(RepairOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RepairOrder result = repairOrderService.closeRepairOrder(roId);

        // Assert
        assertNotNull(result);
        assertEquals("CLOSED", result.getStatus());

        verify(repairOrderRepository, times(1)).findById(roId);
        verify(repairOrderRepository, times(1)).save(any(RepairOrder.class));
    }

    @Test
    void testCloseRepairOrder_NotFound() {
        // Arrange
        String roId = "non-existent-id";
        when(repairOrderRepository.findById(roId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repairOrderService.closeRepairOrder(roId);
        });

        assertTrue(exception.getMessage().contains("Repair order not found"));
        verify(repairOrderRepository, times(1)).findById(roId);
        verify(repairOrderRepository, never()).save(any(RepairOrder.class));
    }

    @Test
    void testCloseRepairOrder_AlreadyClosed() {
        // Arrange
        String roId = "test-id-123";
        testRepairOrder.setId(roId);
        testRepairOrder.setStatus("CLOSED");
        testRepairOrder.setRoNumber("RO-001");

        when(repairOrderRepository.findById(roId)).thenReturn(Optional.of(testRepairOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repairOrderService.closeRepairOrder(roId);
        });

        assertTrue(exception.getMessage().contains("already closed"));
        verify(repairOrderRepository, times(1)).findById(roId);
        verify(repairOrderRepository, never()).save(any(RepairOrder.class));
    }

    @Test
    void testGetAllRepairOrders() {
        // Arrange
        List<RepairOrder> mockOrders = Arrays.asList(testRepairOrder);
        when(repairOrderRepository.findByDealershipIdOrderByCreatedAtDesc("DEALER-001")).thenReturn(mockOrders);

        // Act
        List<RepairOrder> result = repairOrderService.getAllRepairOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repairOrderRepository, times(1)).findByDealershipIdOrderByCreatedAtDesc("DEALER-001");
    }

    @Test
    void testGetRepairOrdersByStatus() {
        // Arrange
        String status = "OPEN";
        List<RepairOrder> mockOrders = Arrays.asList(testRepairOrder);
        when(repairOrderRepository.findByDealershipIdAndStatus("DEALER-001", status)).thenReturn(mockOrders);

        // Act
        List<RepairOrder> result = repairOrderService.getRepairOrdersByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repairOrderRepository, times(1)).findByDealershipIdAndStatus("DEALER-001", status);
    }

    @Test
    void testGetRepairOrderById_Success() {
        // Arrange
        String roId = "test-id-123";
        testRepairOrder.setId(roId);
        when(repairOrderRepository.findById(roId)).thenReturn(Optional.of(testRepairOrder));

        // Act
        RepairOrder result = repairOrderService.getRepairOrderById(roId);

        // Assert
        assertNotNull(result);
        assertEquals(roId, result.getId());
        verify(repairOrderRepository, times(1)).findById(roId);
    }

    @Test
    void testGetRepairOrderById_NotFound() {
        // Arrange
        String roId = "non-existent-id";
        when(repairOrderRepository.findById(roId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repairOrderService.getRepairOrderById(roId);
        });

        assertTrue(exception.getMessage().contains("Repair order not found"));
        verify(repairOrderRepository, times(1)).findById(roId);
    }
}

