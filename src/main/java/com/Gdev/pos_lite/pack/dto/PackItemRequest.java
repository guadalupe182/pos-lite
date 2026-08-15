package com.Gdev.pos_lite.pack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record // piezas de ese producto por pack
PackItemRequest(// piezas de ese producto por pack
@NotNull Long productId, @NotNull @Min(1) Integer quantity) {
}
