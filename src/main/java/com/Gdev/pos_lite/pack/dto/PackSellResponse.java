package com.Gdev.pos_lite.pack.dto;

import java.util.List;

public record PackSellResponse(Long packId, String packName, String barcode, Integer soldQty, List<ItemDetail> items) {

    public record ItemDetail(Long productId, String productName, // cuántas piezas de ese producto trae 1 pack
    Integer quantityPerPack, // qty * quantityPerPack
    Integer totalQuantityDeducted, // stock final del producto
    Integer remainingStock) {
    }
}
