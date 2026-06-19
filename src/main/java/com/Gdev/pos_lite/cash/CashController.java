package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CashCloseReportDto;
import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import com.Gdev.pos_lite.cash.dto.CurrentSessionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash")
public class CashController {

    private final CashService cashService;
    private final CashSessionService cashSessionService;

    public CashController(CashService cashService, CashSessionService cashSessionService) {
        this.cashService = cashService;
        this.cashSessionService = cashSessionService;
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<DailySummaryDto> getDailySummary() {
        return ResponseEntity.ok(cashService.getDailySummary());
    }

    @PostMapping("/close")
    public ResponseEntity<CashCloseReportDto> closeCash(@RequestBody CloseCashRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        CashCloseReportDto report = cashService.closeCash(request, email);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/is-closed")
    public ResponseEntity<Boolean> isCashClosed() {
        return ResponseEntity.ok(cashService.isCashClosedToday());
    }

    @PostMapping("/open")
    public ResponseEntity<CashSession> openCash(@RequestBody OpenCashRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        CashSession session = cashSessionService.openSession(request, email);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/current-session")
    public ResponseEntity<CurrentSessionDto> getCurrentSession() {
        CurrentSessionDto session = cashSessionService.getCurrentSessionDto();
        return ResponseEntity.ok(session);
    }

    @GetMapping("/is-open")
    public ResponseEntity<Boolean> isCashOpen() {
        return ResponseEntity.ok(cashSessionService.isOpen());
    }
}