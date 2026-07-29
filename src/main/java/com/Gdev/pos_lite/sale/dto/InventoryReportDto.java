package com.Gdev.pos_lite.sale.dto;

public class InventoryReportDto {
    private Long productId;
    private String barcode;
    private String name;
    private Integer stock;
    private Integer minStock;
    private boolean lowStock;

    public InventoryReportDto(Long productId, String barcode, String name, Integer stock, Integer minStock, boolean lowStock) {
        this.productId = productId;
        this.barcode = barcode;
        this.name = name;
        this.stock = stock;
        this.minStock = minStock;
        this.lowStock = lowStock;
    }

    public Long getProductId() { return productId; }
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public Integer getStock() { return stock; }
    public Integer getMinStock() { return minStock; }
    public boolean isLowStock() { return lowStock; }
}