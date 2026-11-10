package com.Gdev.pos_lite.loyalty;

import com.Gdev.pos_lite.loyalty.dto.RedeemRequest;
import com.Gdev.pos_lite.loyalty.dto.RedeemResponse;
import com.Gdev.pos_lite.sale.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loyalty")
@PreAuthorize("hasAuthority('LOYALTY')")
public class LoyaltyController {

    private final SaleService saleService;

    public LoyaltyController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/redeem")
    @Operation(summary = "Valida y calcula un canje de puntos para el carrito")
    public RedeemResponse redeem(@Valid @RequestBody RedeemRequest request) {
        return saleService.previewRedemption(request);
    }
}
