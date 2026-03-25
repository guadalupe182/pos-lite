package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PackCreateRequest(
        String barcode,                 // puede venir null / ""
        @NotBlank String name,
        @NotNull BigDecimal price,
        @NotEmpty List<PackItemRequest> items
) {}
