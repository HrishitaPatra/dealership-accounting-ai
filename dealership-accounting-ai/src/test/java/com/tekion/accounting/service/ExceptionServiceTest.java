package com.tekion.accounting.service;

import com.tekion.accounting.model.Exception;
import com.tekion.accounting.repository.ExceptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionServiceTest {

    @Mock
    private ExceptionRepository exceptionRepository;

    @InjectMocks
    private ExceptionService exceptionService;

    private Exception testException;

    @BeforeEach
    void setUp() {
        testException = new Exception();
        testException.setId("exc-123");
        testException.setExceptionNumber("EXC-001");
        testException.setType("UNMATCHED");
        testException.setDescription("Test exception");
        testException.setAmount(50.0);
        testException.setStatus("OPEN");
    }

    @Test
    void testCreateException_Success() {
        // Arrange
        when(exceptionRepository.count()).thenReturn(0L);
        when(exceptionRepository.save(any(Exception.class))).thenAnswer(invocation -> {
            Exception exc = invocation.getArgument(0);
            exc.setId("exc-123");
            return exc;
        });

        // Act
        Exception result = exceptionService.createException(
                "UNMATCHED",
                "Test exception",
                50.0,
                "txn-123",
                "batch-123",
                "AI generated memo",
                "GL-1000"
        );

        // Assert
        assertNotNull(result);
        assertEquals("EXC-001", result.getExceptionNumber());
        assertEquals("UNMATCHED", result.getType());
        assertEquals("Test exception", result.getDescription());
        assertEquals(50.0, result.getAmount());
        assertEquals("OPEN", result.getStatus());
        assertEquals("DEALER-001", result.getDealershipId());

        verify(exceptionRepository, times(1)).count();
        verify(exceptionRepository, times(1)).save(any(Exception.class));
    }

    @Test
    void testGetOpenExceptions() {
        // Arrange
        List<Exception> mockExceptions = Arrays.asList(testException);
        when(exceptionRepository.findByDealershipIdAndStatus("DEALER-001", "OPEN"))
                .thenReturn(mockExceptions);

        // Act
        List<Exception> result = exceptionService.getOpenExceptions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(exceptionRepository, times(1)).findByDealershipIdAndStatus("DEALER-001", "OPEN");
    }

    @Test
    void testGetResolvedExceptions() {
        // Arrange
        testException.setStatus("RESOLVED");
        List<Exception> mockExceptions = Arrays.asList(testException);
        when(exceptionRepository.findByDealershipIdAndStatus("DEALER-001", "RESOLVED"))
                .thenReturn(mockExceptions);

        // Act
        List<Exception> result = exceptionService.getResolvedExceptions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(exceptionRepository, times(1)).findByDealershipIdAndStatus("DEALER-001", "RESOLVED");
    }

    @Test
    void testGetExceptionById_Found() {
        // Arrange
        when(exceptionRepository.findById("exc-123")).thenReturn(Optional.of(testException));

        // Act
        Exception result = exceptionService.getExceptionById("exc-123");

        // Assert
        assertNotNull(result);
        assertEquals("exc-123", result.getId());
        verify(exceptionRepository, times(1)).findById("exc-123");
    }

    @Test
    void testGetExceptionById_NotFound() {
        // Arrange
        when(exceptionRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act
        Exception result = exceptionService.getExceptionById("non-existent");

        // Assert
        assertNull(result);
        verify(exceptionRepository, times(1)).findById("non-existent");
    }

    @Test
    void testResolveException_Success() {
        // Arrange
        when(exceptionRepository.findById("exc-123")).thenReturn(Optional.of(testException));
        when(exceptionRepository.save(any(Exception.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Exception result = exceptionService.resolveException("exc-123", "Resolved by manual adjustment");

        // Assert
        assertNotNull(result);
        assertEquals("RESOLVED", result.getStatus());
        assertEquals("Resolved by manual adjustment", result.getResolutionNotes());
        verify(exceptionRepository, times(1)).findById("exc-123");
        verify(exceptionRepository, times(1)).save(testException);
    }

    @Test
    void testResolveException_NotFound() {
        // Arrange
        when(exceptionRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            exceptionService.resolveException("non-existent", "Resolution notes");
        });

        assertTrue(exception.getMessage().contains("Exception not found"));
        verify(exceptionRepository, times(1)).findById("non-existent");
        verify(exceptionRepository, never()).save(any(Exception.class));
    }
}

