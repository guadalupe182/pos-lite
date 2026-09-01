package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-sessions")
public class CashSessionController {

    private final CashSessionService cashSessionService;

    public CashSessionController(CashSessionService cashSessionService) {
        this.cashSessionService = cashSessionService;
    }

    @PostMapping("/open")
    public ResponseEntity<?> openSession(@Valid @RequestBody OpenCashRequestDto request) {
        return ResponseEntity.ok(cashSessionService.openSession(request, request.getOpenedBy()));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeSession(@PathVariable Long id, @Valid @RequestBody CloseCashRequestDto request) {
        Double finalCash = request.getFinalAmount() != null ? request.getFinalAmount().doubleValue() : null;
        return ResponseEntity.ok(cashSessionService.closeSession(id, finalCash, request.getClosedBy()));
    }
}
