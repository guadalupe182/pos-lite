package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record // código del pack
PackSellRequest(// cuántos packs vender
@NotBlank String barcode, // motivo (SALE, PROMO, etc) opcional
@NotNull @Min(1) Integer qty, String reason) {
}
