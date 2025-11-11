package com.Gdev.pos_lite.product;

import com.Gdev.pos_lite.product.dto.ProductCreateDto;
import com.Gdev.pos_lite.product.dto.ProductUpdateDto;
import com.Gdev.pos_lite.product.dto.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:5173}")
public class ProductController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductController(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    // === GET: lista (usa fetch join + DTO) ===
    @GetMapping
    public List<ProductDto> list() {
        return productRepo.findAllWithCategory()
                .stream()
                .map(ProductDto::from)
                .toList();
    }

    // === POST: crear (usa ProductCreateDto) ===
    @PostMapping
    public ProductDto create(@Valid @RequestBody ProductCreateDto in) {
        var category = categoryRepo.findById(in.categoryId()).orElseThrow();
        var entity = Product.builder()
                .name(in.name())
                .barcode(in.barcode())
                .category(category)
                .price(in.price())
                .stock(in.stock())
                .minStock(in.minStock())
                .build();
        return ProductDto.from(productRepo.save(entity));
    }

    // === PUT: actualizar (usa ProductUpdateDto) ===
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto in) {
        var db = productRepo.findById(id).orElseThrow();

        db.setName(in.name());
        db.setBarcode(in.barcode());
        db.setPrice(in.price());
        db.setStock(in.stock());
        db.setMinStock(in.minStock());

        if (in.categoryId() != null) {
            var category = categoryRepo.findById(in.categoryId()).orElseThrow();
            db.setCategory(category);
        }
        return ProductDto.from(productRepo.save(db));
    }

    // === DELETE ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // === GET por código de barras (único) usa ProductDto===
    @GetMapping("/barcode/{code}")
    public ResponseEntity<ProductDto> getByBarcode(@PathVariable String code) {
        return productRepo.findByBarcode(code)
                .map(ProductDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === Alert low stock (DTO) ===
    @GetMapping("/low-stock")
    public List<ProductDto> lowStock(@RequestParam(required = false) Integer threshold) {
        return productRepo.findLowStock(threshold).stream().map(ProductDto::from).toList();
    }

    /** Descuenta 'qty' unidades del stock. Retorna el producto actualizado. */
    @PatchMapping("/{id}/decrement")
    @Transactional
    public ProductDto decrement(@PathVariable Long id,
                                @RequestParam(defaultValue = "1") int qty) {
        if (qty <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty debe ser > 0");
        }

        Product p = productRepo.findById(id).orElseThrow();
        int current = p.getStock() == null ? 0 : p.getStock();

        if (current < qty) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente");
        }

        p.setStock(current - qty);
        return ProductDto.from(productRepo.save(p));
    }

}
