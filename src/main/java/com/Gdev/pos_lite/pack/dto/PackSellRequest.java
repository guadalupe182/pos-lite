package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PackSellRequest(
        @NotBlank String barcode,   // código del pack
        @NotNull @Min(1) Integer qty, // cuántos packs vender
        String reason                // motivo (SALE, PROMO, etc) opcional
) {}
