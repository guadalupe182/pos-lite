package com.Gdev.pos_lite.product;

import com.Gdev.pos_lite.product.dto.ProductAdjustDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public enum Op { IN, OUT }

    @Transactional
    public Product adjustByBarcode(ProductAdjustDto req) {
        // Normaliza a op/qty si vino delta
        String opStr = req.op();
        Integer qty = req.qty();

        if (req.delta() != null) {
            int d = req.delta();
            opStr = d > 0 ? "IN" : "OUT";
            qty   = Math.abs(d);
        }

        final Op op;
        try {
            op = Op.valueOf(opStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "op debe ser IN u OUT");
        }

        var p = productRepo.findWithLockByBarcode(req.barcode()).orElse(null);

        // Alta rápida si no existe y se proporcionó info mínima
        if (p == null) {
            if (req.name() == null || req.name().isBlank()
                    || req.categoryId() == null || req.price() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no existe. Para alta rápida envía name, categoryId y price");
            }
            var cat = categoryRepo.findById(req.categoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId inválido"));



            p = Product.builder()
                    .name(req.name().trim())
                    .barcode(req.barcode())
                    .category(cat)
                    .price(req.price())
                    .stock(0)
                    .minStock(0)
                    .build();
        }

        int current = p.getStock() == null ? 0 : p.getStock();
        int newStock = (op == Op.IN) ? Math.addExact(current, qty) : current - qty;

        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente");
        }

        p.setStock(newStock);
        return productRepo.save(p);
    }
}
