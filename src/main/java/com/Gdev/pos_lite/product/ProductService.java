package com.Gdev.pos_lite.product;

import com.Gdev.pos_lite.product.dto.ProductAdjustDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        // --- Validación base ---
        if (req.barcode() == null || req.barcode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "barcode requerido");
        }
        final String barcode = req.barcode().trim();

        // --- Normaliza modo A (op/qty) vs B (delta) ---
        String  opStr = req.op();
        Integer qty   = req.qty();

        if (req.delta() != null) {
            int d = req.delta();
            opStr = d > 0 ? "IN" : "OUT";
            qty   = Math.abs(d);
        }
        if (opStr == null || opStr.isBlank() || qty == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveer (op y qty) o delta");
        }

        final Op op;
        try {
            op = Op.valueOf(opStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "op debe ser IN u OUT");
        }

        if (qty <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty/delta debe ser > 0");
        }

        // --- Buscar producto con lock por barcode ---
        Product p = productRepo.findWithLockByBarcode(barcode).orElse(null);

        // --- Alta rápida si no existe ---
        if (p == null) {
            if (req.name() == null || req.name().isBlank() || req.price() == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no existe. Para alta rápida envía name, price y categoryId o categoryName"
                );
            }
            if (req.price().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price debe ser >= 0");
            }

            // Resolver categoría por id o por nombre (crea si no existe).
            Category cat;
            if (req.categoryId() != null) {
                cat = categoryRepo.findById(req.categoryId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId inválido"));
            } else if (req.categoryName() != null && !req.categoryName().isBlank()) {
                final String cn = req.categoryName().trim();
                cat = categoryRepo.findByNameIgnoreCase(cn).orElse(null);
                if (cat == null) {
                    // Crear categoría por nombre con manejo de choque (único).
                    try {
                        Category c = new Category();
                        c.setName(cn);
                        cat = categoryRepo.save(c);
                    } catch (DataIntegrityViolationException ex) {
                        // Otra petición creó la misma categoría en paralelo → re-consulta
                        cat = categoryRepo.findByNameIgnoreCase(cn).orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto creando categoría")
                        );
                    }
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta categoryId o categoryName");
            }

            // minStock: usa el enviado o default 10 (no negativo)
            Integer minStockReq = req.minStock();
            final int minStock = (minStockReq == null) ? 10 : Math.max(0, minStockReq);

            p = Product.builder()
                    .name(req.name().trim())
                    .barcode(barcode)
                    .category(cat)
                    .price(req.price())        // BigDecimal ya en DTO
                    .stock(0)
                    .minStock(minStock)
                    .build();

            // Guardar producto con manejo de choque por barcode único
            try {
                p = productRepo.save(p);
            } catch (DataIntegrityViolationException ex) {
                // Otro request insertó el mismo barcode → re-consulta con lock
                p = productRepo.findWithLockByBarcode(barcode)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto de barcode"));
            }
        }

        // --- Ajuste de inventario con protección de overflow ---
        final int current = (p.getStock() == null) ? 0 : p.getStock();
        final int newStock;
        try {
            newStock = (op == Op.IN)
                    ? Math.addExact(current, qty)      // lanza ArithmeticException si overflow int
                    : Math.subtractExact(current, qty);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad fuera de rango entero");
        }

        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente");
        }

        p.setStock(newStock);
        return productRepo.save(p);
    }
}
