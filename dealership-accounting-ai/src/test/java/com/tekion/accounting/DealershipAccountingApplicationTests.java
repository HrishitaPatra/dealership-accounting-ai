package com.tekion.accounting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test to verify Spring Boot application context loads successfully
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017"
})
class DealershipAccountingApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads without errors
        // If this passes, it means all beans are properly configured
    }
}

