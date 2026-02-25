package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Exception - Unresolved reconciliation issues
 * 
 * Design: Separate collection with AI suggestions
 * Why: Queried independently (exceptions queue), AI metadata tracking
 */
@Document(collection = "exceptions")
@CompoundIndex(name = "dealership_status_created_idx", 
               def = "{'dealershipId': 1, 'status': 1, 'createdAt': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exception {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @Indexed(unique = true)
    @Field("exception_number")
    private String exceptionNumber;  // Auto-generated: EXC-001, EXC-002, etc.
    
    @NotBlank(message = "Exception type is required")
    @Indexed
    @Field("type")
    private String type;  // MERCHANT_FEE, TIMING_DIFFERENCE, UNMATCHED, etc.

    @Field("description")
    private String description;  // Human-readable description

    @NotNull(message = "Amount is required")
    @Field("amount")
    private Double amount;
    
    // Optional references (depending on exception type)
    @Field("bank_transaction_id")
    private String bankTransactionId;
    
    @Field("deposit_batch_id")
    private String depositBatchId;
    
    // AI suggestions (Agent 2: Exception Resolution)
    @Field("ai_detected")
    @Builder.Default
    private Boolean aiDetected = false;

    @Field("ai_suggested_gl_account")
    private String aiSuggestedGLAccount;  // "5200"

    @Field("ai_suggested_gl_name")
    private String aiSuggestedGLName;  // "Bank Fees"

    @Field("suggested_gl_account")
    private String suggestedGlAccount;  // Simplified field name

    @Field("ai_generated_memo")
    private String aiGeneratedMemo;  // Audit memo text

    @Field("resolution_notes")
    private String resolutionNotes;  // User's resolution notes
    
    @Indexed
    @Field("status")
    @Builder.Default
    private String status = "OPEN";  // OPEN, RESOLVED
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("resolved_at")
    private LocalDateTime resolvedAt;
    
    @Field("resolved_by")
    private String resolvedBy;  // "DEMO-USER"
}

