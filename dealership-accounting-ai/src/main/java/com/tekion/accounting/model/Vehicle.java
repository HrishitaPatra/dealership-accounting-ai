package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Vehicle information (embedded in RepairOrder)
 * Not a separate MongoDB collection - always part of RO document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    private String vin;  // Optional for demo
    
    @NotBlank(message = "Vehicle year is required")
    private String year;
    
    @NotBlank(message = "Vehicle make is required")
    private String make;
    
    @NotBlank(message = "Vehicle model is required")
    private String model;
}

