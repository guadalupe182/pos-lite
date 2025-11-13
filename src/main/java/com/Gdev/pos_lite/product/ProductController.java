package com.Gdev.pos_lite.product;

import com.Gdev.pos_lite.product.dto.ProductCreateDto;
import com.Gdev.pos_lite.product.dto.ProductUpdateDto;
import com.Gdev.pos_lite.product.dto.ProductDto;
import com.Gdev.pos_lite.product.dto.ProductAdjustDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "${app.cors.allowed-origins:*}",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS },
        allowedHeaders = "*",
        allowCredentials = "true" // opcional
)
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductService productService;

    public ProductController(ProductRepository productRepo,
                             CategoryRepository categoryRepo,
                             ProductService productService) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        // OJO: inyectamos el service, NO instanciamos con 'new'
        this.productService = productService;
    }

    // === GET: lista (DTO + fetch join) ===
    @GetMapping
    public List<ProductDto> list() {
        return productRepo.findAllWithCategory()
                .stream()
                .map(ProductDto::from)
                .toList();
    }

    // === POST: crear ===
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

    // === PUT: actualizar ===
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

    // === GET por código de barras (DTO) ===
    @GetMapping("/barcode/{code}")
    public ResponseEntity<ProductDto> getByBarcode(@PathVariable String code) {
        return productRepo.findByBarcode(code)
                .map(ProductDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === Low stock ===
    @GetMapping("/low-stock")
    public List<ProductDto> lowStock(@RequestParam(required = false) Integer threshold) {
        return productRepo.findLowStock(threshold).stream().map(ProductDto::from).toList();
    }

    // === Descuento directo por ID (legacy helper) ===
    @PatchMapping("/{id}/decrement")
    @Transactional
    public ProductDto decrement(@PathVariable Long id,
                                @RequestParam(defaultValue = "1") int qty) {
        if (qty <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty debe ser > 0");

        var p = productRepo.findById(id).orElseThrow();
        var current = p.getStock() == null ? 0 : p.getStock();
        if (current < qty) throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente");

        p.setStock(current - qty);
        return ProductDto.from(productRepo.save(p));
    }

    // === Ajuste por barcode (IN/OUT) ===
    @PostMapping("/adjust-by-barcode")
    public ProductDto adjustOne(@Valid @RequestBody ProductAdjustDto req) {
        var saved = productService.adjustByBarcode(req);
        return ProductDto.from(saved);
    }

    // === Ajuste batch por barcode ===
    @PostMapping("/adjust-batch")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> adjustBatch(@Valid @RequestBody List<ProductAdjustDto> items) {
        var out = new java.util.ArrayList<ProductDto>();
        for (var it : items) {
            var saved = productService.adjustByBarcode(it);
            out.add(ProductDto.from(saved));
        }
        return out;
    }
}
