package com.Gdev.pos_lite.product.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Modo A: op (IN|OUT) + qty (>0)
 * Modo B: delta (!=0) + reason opcional
 * Alta rápida si el barcode no existe: name (categoryId o categoryName), price, [minStock opcional]
 */
public record // Modo A
ProductAdjustDto(// Modo A
@NotBlank String barcode, // Modo B
String op, // Modo B
Integer qty, // Alta rápida opcional
Integer delta, // Alta rápida opcional
String reason, String name, Long categoryId, String categoryName, BigDecimal price, Integer minStock) {

    @AssertTrue(message = "Proveer (op y qty) o delta, pero no ambos")
    public boolean isModeValid() {
        boolean hasOpQty = op != null && !op.isBlank() && qty != null;
        boolean hasDelta = delta != null;
        // exactamente uno
        return hasOpQty ^ hasDelta;
    }

    @AssertTrue(message = "qty debe ser > 0")
    public boolean isQtyPositive() {
        return qty == null || qty > 0;
    }

    @AssertTrue(message = "delta no puede ser 0")
    public boolean isDeltaNonZero() {
        return delta == null || delta != 0;
    }

    @AssertTrue(message = "minStock no puede ser negativo")
    public boolean isMinStockValid() {
        return minStock == null || minStock >= 0;
    }
}
