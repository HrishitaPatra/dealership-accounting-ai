package com.tekion.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Spring Boot Application for Dealership Accounting AI System
 * 
 * Features:
 * - AI-powered reconciliation matching
 * - AI-powered exception resolution
 * - Multi-tenancy support (dealershipId-based)
 * - MongoDB document storage
 * 
 * @author Tekion Intern
 */
@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
public class DealershipAccountingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealershipAccountingApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("🚀 Dealership Accounting AI System Started!");
        System.out.println("==============================================");
        System.out.println("📍 Server: http://localhost:8080");
        System.out.println("💚 Health: http://localhost:8080/actuator/health");
        System.out.println("🤖 AI Strategy: Rule-based (Ollama-enhanced)");
        System.out.println("🏢 Demo Dealership: DEALER-001 (ABC Toyota)");
        System.out.println("==============================================\n");
    }
}

