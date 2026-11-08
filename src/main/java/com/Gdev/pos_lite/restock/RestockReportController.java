package com.Gdev.pos_lite.restock;

import com.Gdev.pos_lite.restock.dto.RestockReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restock")
@Tag(name = "Restock", description = "Endpoints para la gestión de reportes de restock (Aguacates)")
public class RestockReportController {

    private final RestockReportService restockReportService;

    public RestockReportController(RestockReportService restockReportService) {
        this.restockReportService = restockReportService;
    }

    @GetMapping({"/report", "/reports"})
    @Operation(summary = "listReports", description = "Lista los reportes de restock registrados")
    public List<RestockReport> listReports() {
        return restockReportService.findAll();
    }

    @GetMapping("/report/{id}")
    @Operation(summary = "getReport", description = "Obtiene un reporte de restock por id")
    public RestockReport getReport(@PathVariable Long id) {
        return restockReportService.findById(id);
    }

    @PostMapping("/report")
    @Operation(summary = "createReport", description = "Recibe y registra un reporte de restock")
    public ResponseEntity<RestockReport> createReport(@Valid @RequestBody RestockReportRequest request) {
        RestockReport report = RestockReport.builder()
                .reportDate(request.reportDate())
                .productName(request.productName())
                .quantity(request.quantity())
                .unit(request.unit())
                .supplier(request.supplier())
                .notes(request.notes())
                .build();
        RestockReport saved = restockReportService.create(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
