package com.Gdev.pos_lite.style;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boutique-items")
@Tag(name = "BoutiqueItem", description = "Endpoints para la gestión de artículos (POS Style)")
@PreAuthorize("hasAuthority('STYLE')")
public class BoutiqueItemController {

    private final BoutiqueItemService boutiqueItemService;

    public BoutiqueItemController(BoutiqueItemService boutiqueItemService) {
        this.boutiqueItemService = boutiqueItemService;
    }

    @GetMapping
    @Operation(summary = "list", description = "Lista todos los artículos")
    public List<BoutiqueItem> list() {
        return boutiqueItemService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get", description = "Obtiene un artículo por id")
    public BoutiqueItem get(@PathVariable Long id) {
        return boutiqueItemService.findById(id);
    }

    @PostMapping
    @Operation(summary = "create", description = "Crea un nuevo artículo")
    public BoutiqueItem create(@RequestBody BoutiqueItem item) {
        return boutiqueItemService.create(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update", description = "Actualiza un artículo")
    public BoutiqueItem update(@PathVariable Long id, @RequestBody BoutiqueItem item) {
        return boutiqueItemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete", description = "Elimina un artículo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boutiqueItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}