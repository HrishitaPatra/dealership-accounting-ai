package com.tekion.accounting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for integrating with Ollama (Llama) for AI text generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    @Value("${ai.ollama.url}")
    private String ollamaBaseUrl;

    @Value("${ai.ollama.model}")
    private String ollamaModel;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Generate explanation for why a bank transaction matches a deposit batch
     */
    public String generateMatchExplanation(double bankAmount, double batchAmount, String batchNumber) {
        String prompt = String.format(
            "You are an accounting AI assistant. Explain in 2-3 sentences why bank transaction of $%.2f " +
            "matches deposit batch %s with amount $%.2f. Be professional and concise.",
            bankAmount, batchNumber, batchAmount
        );
        
        return generateText(prompt);
    }
    
    /**
     * Generate memo for merchant fee exception
     */
    public String generateMerchantFeeMemo(double expectedAmount, double actualAmount, double feeAmount, double feePercentage) {
        String prompt = String.format(
            "You are an accounting AI assistant. Write a professional accounting memo (2-3 sentences) explaining " +
            "a merchant fee discrepancy. Expected deposit: $%.2f, Actual bank credit: $%.2f, " +
            "Merchant fee: $%.2f (%.2f%%). Suggest the journal entry to record this.",
            expectedAmount, actualAmount, feeAmount, feePercentage
        );
        
        return generateText(prompt);
    }
    
    /**
     * Generate memo for timing difference exception
     */
    public String generateTimingDifferenceMemo(String batchNumber, double amount) {
        String prompt = String.format(
            "You are an accounting AI assistant. Write a professional accounting memo (2-3 sentences) explaining " +
            "a timing difference for deposit batch %s ($%.2f) that hasn't appeared in the bank feed yet. " +
            "Suggest how to handle this in month-end close.",
            batchNumber, amount
        );
        
        return generateText(prompt);
    }
    
    /**
     * Generate memo for unmatched bank transaction
     */
    public String generateUnmatchedMemo(double amount, String transactionType) {
        String prompt = String.format(
            "You are an accounting AI assistant. Write a professional accounting memo (2-3 sentences) explaining " +
            "an unmatched bank %s of $%.2f. Suggest possible causes and how to investigate.",
            transactionType.toLowerCase(), Math.abs(amount)
        );

        return generateText(prompt);
    }

    /**
     * Generate dispute resolution response for customer billing questions
     */
    public String generateDisputeResolution(String roDetails, String customerQuestion) {
        String prompt = String.format(
            "You are a professional, empathetic dealership customer service AI assistant. " +
            "A customer has a question about their repair bill.\n\n" +
            "Repair Order Details:\n%s\n\n" +
            "Customer Question: \"%s\"\n\n" +
            "Provide a professional, helpful response (3-5 sentences) that:\n" +
            "1. Addresses their concern directly and empathetically\n" +
            "2. Explains the charges clearly with specific numbers\n" +
            "3. Maintains a friendly, understanding tone\n" +
            "4. Offers further assistance if needed\n\n" +
            "Response:",
            roDetails, customerQuestion
        );

        return generateText(prompt);
    }
    
    /**
     * Core method to generate text using Ollama API
     */
    private String generateText(String prompt) {
        try {
            log.info("Calling Ollama API with model: {}", ollamaModel);
            
            Map<String, Object> request = new HashMap<>();
            request.put("model", ollamaModel);
            request.put("prompt", prompt);
            request.put("stream", false);
            
            String url = ollamaBaseUrl + "/api/generate";
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("response")) {
                String generatedText = (String) response.get("response");
                log.info("Ollama generated text: {}", generatedText.substring(0, Math.min(100, generatedText.length())));
                return generatedText.trim();
            }
            
            log.warn("Ollama response missing 'response' field");
            return getFallbackText(prompt);
            
        } catch (Exception e) {
            log.error("Error calling Ollama API: {}", e.getMessage());
            return getFallbackText(prompt);
        }
    }
    
    /**
     * Fallback text when Ollama is unavailable
     */
    private String getFallbackText(String prompt) {
        if (prompt.contains("matches deposit batch")) {
            return "The bank transaction amount matches the deposit batch total. This indicates a successful deposit with no discrepancies.";
        } else if (prompt.contains("merchant fee")) {
            return "A merchant fee has been deducted from the deposit. Debit: Merchant Fee Expense, Credit: Cash to reconcile the difference.";
        } else if (prompt.contains("timing difference")) {
            return "This deposit is in transit and will appear in the next bank statement. Monitor for clearance within 2-3 business days.";
        } else if (prompt.contains("customer service") || prompt.contains("Customer Question")) {
            return "Thank you for your question. I'd be happy to help clarify your bill. Please contact our service manager for a detailed explanation of the charges. We're committed to ensuring you understand every aspect of your service.";
        } else {
            return "This transaction requires manual review to determine the appropriate accounting treatment.";
        }
    }
}

