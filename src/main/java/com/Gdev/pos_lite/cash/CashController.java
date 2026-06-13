package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash")
public class CashController {

    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<DailySummaryDto> getDailySummary() {
        return ResponseEntity.ok(cashService.getDailySummary());
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeCash(@RequestBody CloseCashRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Esto devuelve el email/usuario logueado
        CashClosure closure = cashService.closeCash(request, email);
        return ResponseEntity.ok(closure);
    }

    @GetMapping("/is-closed")
    public ResponseEntity<Boolean> isCashClosed() {
        return ResponseEntity.ok(cashService.isCashClosedToday());
    }
}