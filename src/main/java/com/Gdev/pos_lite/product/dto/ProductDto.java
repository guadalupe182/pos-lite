// src/main/java/com/Gdev/pos_lite/product/dto/ProductDto.java
package com.Gdev.pos_lite.product.dto;

import com.Gdev.pos_lite.product.Product;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String barcode,
        BigDecimal price,
        Integer stock,
        Integer minStock,
        Long categoryId,
        String categoryName
) {
    public static ProductDto from(Product p) {
        var cat = p.getCategory();
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getBarcode(),
                p.getPrice(),
                p.getStock(),
                p.getMinStock(),
                (cat != null ? cat.getId() : null),
                (cat != null ? cat.getName() : null)
        );
    }
}
