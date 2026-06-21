package com.Gdev.pos_lite.notification;

import com.Gdev.pos_lite.cash.CashSession;
import com.Gdev.pos_lite.product.Product;
import com.Gdev.pos_lite.product.ProductRepository;
import com.Gdev.pos_lite.cash.CashSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ProductRepository productRepository;
    private final CashSessionService cashSessionService;

    public NotificationService(NotificationRepository notificationRepository,
                               ProductRepository productRepository,
                               CashSessionService cashSessionService) {
        this.notificationRepository = notificationRepository;
        this.productRepository = productRepository;
        this.cashSessionService = cashSessionService;
    }

    @Scheduled(fixedDelay = 60000) // cada minuto
    @Transactional
    public void checkLowStockAndCash() {
        // 1. Verificar stock bajo
        List<Product> lowStockProducts = productRepository.findLowStock(null);
        for (Product product : lowStockProducts) {
            String message = "Producto '" + product.getName() + "' tiene stock bajo: " + product.getStock() + " unidades.";
            createNotification("STOCK_LOW", message);
        }
        // 2. Verificar efectivo bajo en caja (si hay sesión abierta)
        if (cashSessionService.isOpen()) {
            CashSession session = cashSessionService.getCurrentOpenSession();
            double cash = session.getInitialCash();
            if (cash < 500) { // umbral de efectivo bajo (configurable)
                String message = "El efectivo en caja es bajo: $" + cash + ". Considera realizar un depósito.";
                createNotification("CASH_LOW", message);
            }
        }
    }

    public void createNotification(String type, String message) {
        // Obtener el usuario actual (podrías obtenerlo del contexto de seguridad)
        // Por simplicidad, usamos un usuario fijo o lo obtenemos del token
        String userId = "admin@demo.com"; // temporal
        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setUserId(userId);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    public List<Notification> getAllNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(n -> n.setRead(true));
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}