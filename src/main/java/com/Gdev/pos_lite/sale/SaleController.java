package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.sale.dto.InventoryReportDto;
import com.Gdev.pos_lite.sale.dto.SaleRequest;
import com.Gdev.pos_lite.sale.dto.SaleResponse;
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

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@RequestBody SaleRequest request,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        Sale sale = saleService.registerSale(request, userEmail);
        return ResponseEntity.ok(mapToResponse(sale));
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
        // Aquí podrías mapear los detalles si los necesitas
        return resp;
    }
}