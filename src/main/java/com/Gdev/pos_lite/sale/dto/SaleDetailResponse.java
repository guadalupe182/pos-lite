package com.Gdev.pos_lite.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para SaleDetailResponse")
public class SaleDetailResponse {

    @Schema(description = "Campo productId", example = "ejemplo")
    private Long productId;

    @Schema(description = "Campo productName", example = "ejemplo")
    private String productName;

    @Schema(description = "Campo quantity", example = "ejemplo")
    private Integer quantity;

    @Schema(description = "Campo unitPrice", example = "ejemplo")
    private Double unitPrice;

    @Schema(description = "Campo subtotal", example = "ejemplo")
    private Double subtotal;

    // Constructor, getters y setters
    public SaleDetailResponse(Long productId, String productName, Integer quantity, Double unitPrice, Double subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // ... getters y setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
