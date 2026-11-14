package com.Gdev.pos_lite.notification;

import com.Gdev.pos_lite.product.ProductRepository;
import com.Gdev.pos_lite.cash.CashSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final String OWNER_EMAIL = "owner@example.com";
    private static final String OTHER_EMAIL = "other@example.com";

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CashSessionService cashSessionService;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, productRepository, cashSessionService);
    }

    @Test
    void markAsReadMarcaLaNotificacionDelPropietario() {
        Notification notification = notification(1L, OWNER_EMAIL, false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L, OWNER_EMAIL);

        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void markAsReadLanzaNotFoundSiNoEsElPropietario() {
        Notification notification = notification(1L, OWNER_EMAIL, false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, OTHER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(notification.isRead()).isFalse();
    }

    @Test
    void markAsReadLanzaNotFoundSiNoExiste() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99L, OWNER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteNotificationEliminaSiEsElPropietario() {
        Notification notification = notification(2L, OWNER_EMAIL, false);
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(2L, OWNER_EMAIL);

        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotificationNoEliminaSiNoEsElPropietario() {
        Notification notification = notification(2L, OWNER_EMAIL, false);
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.deleteNotification(2L, OTHER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(notificationRepository, never()).delete(notification);
    }

    @Test
    void deleteNotificationLanzaNotFoundSiNoExiste() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.deleteNotification(99L, OWNER_EMAIL))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(notificationRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    private Notification notification(Long id, String userId, boolean read) {
        Notification n = new Notification();
        n.setId(id);
        n.setUserId(userId);
        n.setRead(read);
        n.setType("STOCK_LOW");
        n.setMessage("msg");
        return n;
    }
}