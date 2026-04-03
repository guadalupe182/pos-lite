package com.Gdev.pos_lite.sale.dto;

public class SaleDetailResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
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