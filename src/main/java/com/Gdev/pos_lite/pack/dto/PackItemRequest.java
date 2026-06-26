package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PackItemRequest(@NotNull Long productId, // piezas de ese producto por pack
@NotNull @Min(1) Integer quantity) {
}
