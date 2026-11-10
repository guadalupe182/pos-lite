package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.email.EmailService;
import com.Gdev.pos_lite.sale.dto.InventoryReportDto;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.sale.dto.SaleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sale", description = "Endpoints para la gestión de ventas")
public class SaleController {

    private static final Logger log = LoggerFactory.getLogger(SaleController.class);

    private final SaleService saleService;
    private final EmailService emailService;

    public SaleController(SaleService saleService, EmailService emailService) {
        this.saleService = saleService;
        this.emailService = emailService;
    }

    @PostMapping
    @Operation(summary = "createSale", description = "Registra una nueva venta en el sistema")
    public ResponseEntity<SaleResponse> createSale(
            @RequestBody SaleRequest request, 
            Authentication authentication, 
            @RequestParam(required = false, defaultValue = "MERCADOPAGO") String paymentMethod) {
        
        // 1. Obtener datos del usuario autenticado y del cliente
        String userEmail = authentication.getName();
        String customerEmail = request.getCustomerEmail();
        
        if (customerEmail == null || customerEmail.isBlank()) {
            customerEmail = userEmail;
        }
        String customerName = customerEmail.split("@")[0];

        // 2. Validar si el usuario tiene el módulo de lealtad habilitado
        boolean loyaltyEnabled = authentication.getAuthorities().stream()
                .anyMatch(authority -> "LOYALTY".equals(authority.getAuthority()));

        // 3. Registrar la venta (incluye la lógica transaccional de stock)
        Sale sale = saleService.registerSale(request, userEmail, loyaltyEnabled);
            
        // 4. RESILIENCIA: Aislamiento de fallos. 
        // Si el servicio de correo falla, la transacción de la venta no se revierte.
        try {
            emailService.sendSaleReceipt(sale, customerEmail, customerName, paymentMethod);
        } catch (Exception e) {
            log.error("Error enviando el ticket por correo al cliente {}: {}", customerEmail, e.getMessage());
        }

        // 5. Construir y devolver la respuesta
        SaleResponse response = mapToResponse(sale);
        response.setCustomerEmail(customerEmail);
        response.setPaymentMethod(paymentMethod);
        
        if (loyaltyEnabled && sale.getCustomerEmail() != null) {
            response.setPointsBalance(saleService.getPointsBalance(sale.getCustomerEmail()));
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    @Operation(summary = "getSalesReport", description = "Obtiene el reporte de ventas en un rango de fechas")
    public ResponseEntity<List<SaleResponse>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        List<Sale> sales = saleService.getSalesBetween(from, to);
        List<SaleResponse> responses = sales.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inventory-report")
    @Operation(summary = "getInventoryReport", description = "Obtiene el reporte actualizado del inventario")
    public ResponseEntity<List<InventoryReportDto>> getInventoryReport() {
        return ResponseEntity.ok(saleService.getInventoryReport());
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse resp = new SaleResponse();
        resp.setId(sale.getId());
        resp.setSaleDate(sale.getSaleDate());
        resp.setTotal(sale.getTotal());
        resp.setPointsEarned(sale.getPointsEarned());
        resp.setPointsRedeemed(sale.getPointsRedeemed());
        resp.setLoyaltyDiscount(sale.getLoyaltyDiscount());
        return resp;
    }
}
