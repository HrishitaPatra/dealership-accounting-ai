package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Customer information (embedded in RepairOrder)
 * Not a separate MongoDB collection - always part of RO document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @NotBlank(message = "Customer name is required")
    private String name;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Phone must be in format: 555-123-4567")
    private String phone;
}

