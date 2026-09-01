package com.Gdev.pos_lite.cash;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CashSessionServiceTest {

    @Mock
    private CashSessionRepository cashSessionRepository;

    @InjectMocks
    private CashSessionService cashSessionService;

    private CashSession openSession;

    @BeforeEach
    void setUp() {
        openSession = new CashSession();
        openSession.setId(1L);
        openSession.setOpenedBy("user@example.com");
        openSession.setStatus("OPEN");
        openSession.setInitialCash(1000.0);
        openSession.setOpenedAt(Instant.now());
    }

    @Test
    void openSession_ShouldSucceedWhenNoOpenSession() {
        when(cashSessionRepository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.empty());
        when(cashSessionRepository.save(any())).thenReturn(openSession);

        CashSession result = cashSessionService.openSession(1000.0, "user@example.com");

        assertNotNull(result);
        assertEquals("OPEN", result.getStatus());
    }

    @Test
    void openSession_ShouldFailWhenOpenSessionExists() {
        when(cashSessionRepository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.of(openSession));

        assertThrows(IllegalStateException.class, () -> {
            cashSessionService.openSession(1000.0, "user@example.com");
        });
    }

    @Test
    void closeSession_ShouldSucceedWhenSessionOpen() {
        when(cashSessionRepository.findById(any())).thenReturn(Optional.of(openSession));

        CashSession result = cashSessionService.closeSession(1L, 1500.0, "admin@example.com");

        assertEquals("CLOSED", result.getStatus());
        assertNotNull(result.getClosedAt());
        assertEquals(LocalDate.now(), result.getClosureDate().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        assertEquals(1500.0, result.getActualCash());
    }

    @Test
    void closeSession_ShouldFailWhenSessionNotFound() {
        when(cashSessionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            cashSessionService.closeSession(99L, 1500.0, "admin@example.com");
        });
    }

    @Test
    void closeSession_ShouldFailWhenSessionAlreadyClosed() {
        openSession.setStatus("CLOSED");
        when(cashSessionRepository.findById(any())).thenReturn(Optional.of(openSession));

        assertThrows(IllegalStateException.class, () -> {
            cashSessionService.closeSession(1L, 1500.0, "admin@example.com");
        });
    }

    @Test
    void isSessionOpenForUser_ShouldReturnTrueWhenOpenSessionExists() {
        when(cashSessionRepository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.of(openSession));

        assertTrue(cashSessionService.isSessionOpenForUser("user@example.com"));
    }

    @Test
    void isSessionOpenForUser_ShouldReturnFalseWhenNoOpenSession() {
        when(cashSessionRepository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.empty());

        assertFalse(cashSessionService.isSessionOpenForUser("user@example.com"));
    }
}
