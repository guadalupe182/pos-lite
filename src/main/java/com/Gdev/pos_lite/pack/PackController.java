// src/main/java/com/Gdev/pos_lite/pack/PackController.java
package com.Gdev.pos_lite.pack;

import com.Gdev.pos_lite.pack.dto.PackCreateRequest;
import com.Gdev.pos_lite.pack.dto.PackDetailResponse;
import com.Gdev.pos_lite.pack.dto.PackSellRequest;
import com.Gdev.pos_lite.pack.dto.PackSellResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
@Tag(name = "Pack", description = "Endpoints para la gestión de pack")
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
    @Operation(summary = "sellPack", description = "Endpoint para sellpack")
    public PackSellResponse sellPack(@Valid @RequestBody PackSellRequest request) {
        return packService.sellPack(request);
    }

    @PostMapping
    @Operation(summary = "createPack", description = "Endpoint para createpack")
    public PackDetailResponse createPack(@Valid @RequestBody PackCreateRequest request) {
        return packService.createPack(request);
    }

    @GetMapping("/{barcode}")
    @Operation(summary = "getByBarcode", description = "Endpoint para getbybarcode")
    public PackDetailResponse getByBarcode(@PathVariable String barcode) {
        return packService.getByBarcode(barcode);
    }
}
