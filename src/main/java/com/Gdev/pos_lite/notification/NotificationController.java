// src/main/java/com/Gdev/pos_lite/notification/NotificationController.java
package com.Gdev.pos_lite.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "Endpoints para la gestión de notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "getNotifications", description = "Endpoint para getnotifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(notificationService.getAllNotifications(email));
    }

    @GetMapping("/unread")
    @Operation(summary = "getUnreadNotifications", description = "Endpoint para getunreadnotifications")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(email));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "markAsRead", description = "Endpoint para markasread")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "deleteNotification", description = "Endpoint para deletenotification")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
