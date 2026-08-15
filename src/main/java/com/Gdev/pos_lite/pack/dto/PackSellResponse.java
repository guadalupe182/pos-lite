package com.Gdev.pos_lite.pack.dto;

import java.util.List;

public record PackSellResponse(Long packId, String packName, String barcode, Integer soldQty, List<ItemDetail> items) {

    public record // cuántas piezas de ese producto trae 1 pack
    ItemDetail(// cuántas piezas de ese producto trae 1 pack
    Long productId, // cuántas piezas de ese producto trae 1 pack
    String productName, // qty * quantityPerPack
    Integer quantityPerPack, // stock final del producto
    Integer totalQuantityDeducted, Integer remainingStock) {
    }
}
