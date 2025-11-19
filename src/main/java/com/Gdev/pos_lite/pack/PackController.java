package com.Gdev.pos_lite.pack;

import com.Gdev.pos_lite.pack.dto.PackSellRequest;
import com.Gdev.pos_lite.pack.dto.PackSellResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
public class PackController {

    private final PackService packService;

    /**
     * Vender uno o más packs.
     *
     * POST /api/packs/sell
     * Body:
     * {
     *   "barcode": "1234567890",
     *   "qty": 2,
     *   "reason": "SALE"
     * }
     */
    @PostMapping("/sell")
    public PackSellResponse sellPack(@Valid @RequestBody PackSellRequest request) {
        return packService.sellPack(request);
    }
}
