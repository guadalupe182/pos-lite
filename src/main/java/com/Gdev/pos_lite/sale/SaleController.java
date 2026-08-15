package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.email.EmailService;
import com.Gdev.pos_lite.sale.dto.InventoryReportDto;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.sale.dto.SaleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sale", description = "Endpoints para la gestión de sale")
public class SaleController {

    private final SaleService saleService;

    private final EmailService emailService;

    public SaleController(SaleService saleService, EmailService emailService) {
        this.saleService = saleService;
        this.emailService = emailService;
    }

    @PostMapping
    @Operation(summary = "createSale", description = "Endpoint para createsale")
    public ResponseEntity<SaleResponse> createSale(@RequestBody SaleRequest request, Authentication authentication, @RequestParam(required = false, defaultValue = "MERCADOPAGO") String paymentMethod) {
        // Email del vendedor (para registro)
        String userEmail = authentication.getName();
        // Email del comprador
        String customerEmail = request.getCustomerEmail();
        // Si no se proporciona email del comprador, usar el del vendedor (fallback)
        if (customerEmail == null || customerEmail.isBlank()) {
            customerEmail = userEmail;
        }
        String customerName = customerEmail.split("@")[0];
        Sale sale = saleService.registerSale(request, userEmail);
        // Enviar email de confirmación al COMPRADOR
        emailService.sendSaleReceipt(sale, customerEmail, customerName, paymentMethod);
        SaleResponse response = mapToResponse(sale);
        response.setCustomerEmail(customerEmail);
        response.setPaymentMethod(paymentMethod);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    @Operation(summary = "getSalesReport", description = "Endpoint para getsalesreport")
    public ResponseEntity<List<SaleResponse>> getSalesReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        List<Sale> sales = saleService.getSalesBetween(from, to);
        List<SaleResponse> responses = sales.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inventory-report")
    @Operation(summary = "getInventoryReport", description = "Endpoint para getinventoryreport")
    public ResponseEntity<List<InventoryReportDto>> getInventoryReport() {
        return ResponseEntity.ok(saleService.getInventoryReport());
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse resp = new SaleResponse();
        resp.setId(sale.getId());
        resp.setSaleDate(sale.getSaleDate());
        resp.setTotal(sale.getTotal());
        return resp;
    }
}
