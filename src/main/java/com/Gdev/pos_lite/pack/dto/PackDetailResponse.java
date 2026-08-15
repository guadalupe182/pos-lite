package com.Gdev.pos_lite.pack.dto;

import java.math.BigDecimal;
import java.util.List;

public record PackDetailResponse(Long id, String name, String barcode, BigDecimal price, List<ItemDetail> items) {

    public record ItemDetail(Long productId, String productName, Integer quantityPerPack) {
    }
}
