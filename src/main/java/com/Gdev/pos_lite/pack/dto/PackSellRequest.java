package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PackSellRequest(// código del pack
@NotBlank String barcode, // cuántos packs vender
@NotNull @Min(1) Integer qty, // motivo (SALE, PROMO, etc) opcional
String reason) {
}
