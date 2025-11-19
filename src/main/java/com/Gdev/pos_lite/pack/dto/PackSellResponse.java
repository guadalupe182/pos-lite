package com.Gdev.pos_lite.pack.dto;

import java.util.List;

public record PackSellResponse(
        Long packId,
        String packName,
        String barcode,
        Integer soldQty,
        List<ItemDetail> items
) {
    public record ItemDetail(
            Long productId,
            String productName,
            Integer quantityPerPack,       // cuántas piezas de ese producto trae 1 pack
            Integer totalQuantityDeducted, // qty * quantityPerPack
            Integer remainingStock         // stock final del producto
    ) {}
}
