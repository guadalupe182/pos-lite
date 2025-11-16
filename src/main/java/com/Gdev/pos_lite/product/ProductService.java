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
        // --- Normaliza modo A (op/qty) vs B (delta) ---
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

        // --- Buscar producto con lock por barcode ---
        Product p = productRepo.findWithLockByBarcode(req.barcode()).orElse(null);

        // --- Alta rápida si no existe ---
        if (p == null) {
            if (req.name() == null || req.name().isBlank() || req.price() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no existe. Para alta rápida envía name, price y categoryId o categoryName");
            }

            // Resolver categoría por id o nombre (y crear si no existe)
            Category cat = null;
            if (req.categoryId() != null) {
                cat = categoryRepo.findById(req.categoryId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId inválido"));
            } else if (req.categoryName() != null && !req.categoryName().isBlank()) {
                final String cn = req.categoryName().trim();
                cat = categoryRepo.findByNameIgnoreCase(cn).orElseGet(() -> {
                    Category c = new Category();
                    c.setName(cn);
                    return categoryRepo.save(c);
                });
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta categoryId o categoryName");
            }

            p = Product.builder()
                    .name(req.name().trim())
                    .barcode(req.barcode())
                    .category(cat)
                    .price(req.price())        // BigDecimal ya en DTO
                    .stock(0)
                    .minStock(0)
                    .build();
        }

        // --- Ajuste de inventario ---
        int current = p.getStock() == null ? 0 : p.getStock();
        int newStock = (op == Op.IN) ? Math.addExact(current, qty) : current - qty;

        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente");
        }

        p.setStock(newStock);
        return productRepo.save(p);
    }
}
