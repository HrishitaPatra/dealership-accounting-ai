package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Bank Transaction - From bank feed
 * 
 * Design: Simple document (no complex relationships)
 * Why: Mirrors bank feed structure, easy to import
 */
@Document(collection = "bank_transactions")
@CompoundIndex(name = "dealership_status_date_idx", 
               def = "{'dealershipId': 1, 'status': 1, 'date': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @Indexed(unique = true)
    @Field("transaction_id")
    private String transactionId;  // From bank feed
    
    @NotNull(message = "Transaction date is required")
    @Field("date")
    private LocalDate date;
    
    @NotBlank(message = "Description is required")
    @Field("description")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Field("amount")
    private Double amount;  // Positive = credit, negative = debit
    
    @Field("type")
    private String type;  // CREDIT, DEBIT
    
    @Indexed
    @Field("status")
    @Builder.Default
    private String status = "UNMATCHED";  // UNMATCHED, MATCHED
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}

