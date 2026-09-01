package com.Gdev.pos_lite.cash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashSessionServiceTest {

    @Mock
    private CashSessionRepository repository;

    @InjectMocks
    private CashSessionService service;

    private CashSession openSession;

    @BeforeEach
    void setUp() {
        openSession = new CashSession(1000.0, "admin@example.com");
    }

    @Test
    void openSession_ShouldCreateNewSession() {
        when(repository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(openSession);

        CashSession result = service.openSession(1000.0, "admin@example.com");

        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }

    @Test
    void openSession_ShouldThrowException_WhenSessionAlreadyOpen() {
        when(repository.findByOpenedByAndStatus(any(), any())).thenReturn(Optional.of(openSession));

        assertThrows(IllegalStateException.class, () -> service.openSession(1000.0, "admin@example.com"));
    }
}
