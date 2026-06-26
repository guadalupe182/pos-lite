package com.Gdev.pos_lite.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para InventoryReportDto")
public class InventoryReportDto {

    @Schema(description = "Campo productId", example = "ejemplo")
    private Long productId;

    // ← campo agregado
    @Schema(description = "Campo barcode", example = "ejemplo")
    private String barcode;

    @Schema(description = "Campo name", example = "ejemplo")
    private String name;

    @Schema(description = "Campo stock", example = "ejemplo")
    private Integer stock;

    @Schema(description = "Campo minStock", example = "ejemplo")
    private Integer minStock;

    @Schema(description = "Campo lowStock", example = "ejemplo")
    private boolean lowStock;

    public InventoryReportDto(Long productId, String barcode, String name, Integer stock, Integer minStock, boolean lowStock) {
        this.productId = productId;
        this.barcode = barcode;
        this.name = name;
        this.stock = stock;
        this.minStock = minStock;
        this.lowStock = lowStock;
    }

    // Getters
    public Long getProductId() {
        return productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public boolean isLowStock() {
        return lowStock;
    }
}
