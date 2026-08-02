package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.email.EmailService;
import com.Gdev.pos_lite.sale.dto.InventoryReportDto;
import com.Gdev.pos_lite.sale.dto.SaleDetailResponse;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.sale.dto.SaleResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;
    private final EmailService emailService;

    public SaleController(SaleService saleService, EmailService emailService) {
        this.saleService = saleService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(
            @Valid @RequestBody SaleRequest request,
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "EFECTIVO") String paymentMethod) {

        String userEmail = authentication.getName();
        String customerEmail = request.getCustomerEmail();

        if (customerEmail == null || customerEmail.isBlank()) {
            customerEmail = userEmail;
        }

        String customerName = customerEmail.contains("@") ? customerEmail.split("@")[0] : customerEmail;

        // 1. Registrar venta en BD con cálculos precisos
        Sale sale = saleService.registerSale(request, userEmail);

        // 2. Enviar correo de confirmación
        try {
            emailService.sendSaleReceipt(sale, customerEmail, customerName, paymentMethod);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo enviar el recibo por correo: " + e.getMessage());
        }

        // 3. Mapear respuesta
        SaleResponse response = mapToResponse(sale);
        response.setCustomerEmail(customerEmail);
        response.setPaymentMethod(paymentMethod);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    public ResponseEntity<List<SaleResponse>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        List<Sale> sales = saleService.getSalesBetween(from, to);
        List<SaleResponse> responses = sales.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inventory-report")
    public ResponseEntity<List<InventoryReportDto>> getInventoryReport() {
        return ResponseEntity.ok(saleService.getInventoryReport());
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse resp = new SaleResponse();
        resp.setId(sale.getId());
        resp.setSaleDate(sale.getSaleDate());
        resp.setTotal(sale.getTotal());
        resp.setCashReceived(sale.getCashReceived());
        resp.setChange(sale.getChange());

        if (sale.getDetails() != null) {
            List<SaleDetailResponse> details = sale.getDetails().stream()
                    .map(d -> new SaleDetailResponse(
                            d.getProduct().getId(),
                            d.getProduct().getName(),
                            d.getQuantity(),
                            d.getUnitPrice(),
                            d.getSubtotal()
                    ))
                    .collect(Collectors.toList());
            resp.setDetails(details);
        }

        return resp;
    }
}