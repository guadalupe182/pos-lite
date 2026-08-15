package com.Gdev.pos_lite.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Product", description = "Endpoints para la gestión de product")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @Operation(summary = "list", description = "Endpoint para list")
    public List<Category> list() {
        return repo.findAll();
    }

    @PostMapping
    @Operation(summary = "create", description = "Endpoint para create")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        Category saved = repo.save(category);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "get", description = "Endpoint para get")
    public ResponseEntity<Category> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete", description = "Endpoint para delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
