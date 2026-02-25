package com.tekion.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekion.accounting.model.Customer;
import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.model.Vehicle;
import com.tekion.accounting.service.RepairOrderService;
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

@WebMvcTest(RepairOrderController.class)
class RepairOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairOrderService repairOrderService;

    private RepairOrder testRepairOrder;

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder()
                .name("John Doe")
                .phone("555-123-4567")
                .build();

        Vehicle vehicle = Vehicle.builder()
                .vin("1HGBH41JXMN109186")
                .make("Toyota")
                .model("Camry")
                .year("2023")
                .build();

        testRepairOrder = RepairOrder.builder()
                .id("ro-123")
                .roNumber("RO-001")
                .customer(customer)
                .vehicle(vehicle)
                .status("OPEN")
                .subtotal(100.0)
                .tax(8.0)
                .total(108.0)
                .build();
    }

    @Test
    void testCreateRepairOrder() throws Exception {
        // Arrange
        when(repairOrderService.createRepairOrder(any(RepairOrder.class))).thenReturn(testRepairOrder);

        // Act & Assert
        mockMvc.perform(post("/api/repair-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRepairOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ro-123"))
                .andExpect(jsonPath("$.roNumber").value("RO-001"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void testGetAllRepairOrders() throws Exception {
        // Arrange
        List<RepairOrder> repairOrders = Arrays.asList(testRepairOrder);
        when(repairOrderService.getAllRepairOrders()).thenReturn(repairOrders);

        // Act & Assert
        mockMvc.perform(get("/api/repair-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ro-123"))
                .andExpect(jsonPath("$[0].roNumber").value("RO-001"));
    }

    @Test
    void testGetRepairOrdersByStatus() throws Exception {
        // Arrange
        List<RepairOrder> repairOrders = Arrays.asList(testRepairOrder);
        when(repairOrderService.getRepairOrdersByStatus("OPEN")).thenReturn(repairOrders);

        // Act & Assert
        mockMvc.perform(get("/api/repair-orders")
                .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void testGetRepairOrderById() throws Exception {
        // Arrange
        when(repairOrderService.getRepairOrderById("ro-123")).thenReturn(testRepairOrder);

        // Act & Assert
        mockMvc.perform(get("/api/repair-orders/ro-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ro-123"))
                .andExpect(jsonPath("$.roNumber").value("RO-001"));
    }

    @Test
    void testCloseRepairOrder() throws Exception {
        // Arrange
        testRepairOrder.setStatus("CLOSED");
        when(repairOrderService.closeRepairOrder("ro-123")).thenReturn(testRepairOrder);

        // Act & Assert
        mockMvc.perform(put("/api/repair-orders/ro-123/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ro-123"))
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}

