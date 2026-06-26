package com.Gdev.pos_lite.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para SaleItemRequest")
public class SaleItemRequest {

    @Schema(description = "Campo productId", example = "ejemplo")
    private Long productId;

    @Schema(description = "Campo quantity", example = "ejemplo")
    private Integer quantity;

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
