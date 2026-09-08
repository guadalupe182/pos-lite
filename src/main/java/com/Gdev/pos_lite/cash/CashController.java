package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CashCloseReportDto;
import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.CurrentSessionDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash")
@Tag(name = "Cash", description = "Endpoints para la gestión de caja por turno y cajero")
public class CashController {

    private final CashService cashService;
    private final CashSessionService cashSessionService;

    public CashController(CashService cashService, CashSessionService cashSessionService) {
        this.cashService = cashService;
        this.cashSessionService = cashSessionService;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAuthority('MULTI_CASH')")
    @Operation(summary = "openCash", description = "Abre una nueva sesión de caja para el cajero autenticado")
    public ResponseEntity<CashSession> openCash(@Valid @RequestBody OpenCashRequestDto request, Authentication auth) {
        CashSession session = cashSessionService.openSession(request, auth.getName());
        return ResponseEntity.ok(session);
    }

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('MULTI_CASH')")
    @Operation(summary = "closeCash", description = "Cierra la sesión de caja del cajero autenticado")
    public ResponseEntity<CashCloseReportDto> closeCash(@Valid @RequestBody CloseCashRequestDto request, Authentication auth) {
        CashCloseReportDto report = cashService.closeCash(request, auth.getName());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/current-session")
    @Operation(summary = "getCurrentSession", description = "Obtiene los detalles DTO de la sesión actual")
    public ResponseEntity<CurrentSessionDto> getCurrentSession() {
        CurrentSessionDto session = cashSessionService.getCurrentSessionDto();
        return ResponseEntity.ok(session);
    }

    @GetMapping("/is-open")
    @Operation(summary = "isCashOpen", description = "Verifica si el cajero actual tiene una caja abierta")
    public ResponseEntity<Boolean> isCashOpen(Authentication auth) {
        return ResponseEntity.ok(cashSessionService.isSessionOpenForUser(auth.getName()));
    }

    @GetMapping("/daily-summary")
    @Operation(summary = "getDailySummary", description = "Obtiene el resumen diario global de ventas")
    public ResponseEntity<DailySummaryDto> getDailySummary() {
        return ResponseEntity.ok(cashService.getDailySummary());
    }

    @GetMapping("/is-closed")
    @Operation(summary = "isCashClosed", description = "Verifica si las operaciones globales del día están cerradas")
    public ResponseEntity<Boolean> isCashClosed() {
        return ResponseEntity.ok(cashService.isCashClosedToday());
    }
}