package com.Gdev.pos_lite.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductUpdateDto(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 64) String barcode,
        @NotNull @PositiveOrZero Integer stock,
        @NotNull @PositiveOrZero Integer minStock,
        @NotNull @DecimalMin(value = "0.00") BigDecimal price,
        @NotNull Long categoryId
) {}
