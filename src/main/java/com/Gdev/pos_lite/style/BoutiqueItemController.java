package com.Gdev.pos_lite.style;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public List<BoutiqueItem> list(@AuthenticationPrincipal Jwt jwt) {
        return boutiqueItemService.findAll(tenantId(jwt));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get", description = "Obtiene un artículo por id")
    public BoutiqueItem get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return boutiqueItemService.findById(id, tenantId(jwt));
    }

    @PostMapping
    @Operation(summary = "create", description = "Crea un nuevo artículo")
    public BoutiqueItem create(@RequestBody BoutiqueItem item, @AuthenticationPrincipal Jwt jwt) {
        return boutiqueItemService.create(item, tenantId(jwt));
    }

    @PutMapping("/{id}")
    @Operation(summary = "update", description = "Actualiza un artículo")
    public BoutiqueItem update(@PathVariable Long id, @RequestBody BoutiqueItem item,
                               @AuthenticationPrincipal Jwt jwt) {
        return boutiqueItemService.update(id, item, tenantId(jwt));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete", description = "Elimina un artículo")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        boutiqueItemService.delete(id, tenantId(jwt));
        return ResponseEntity.noContent().build();
    }

    private String tenantId(Jwt jwt) {
        String tenantId = jwt == null ? null : jwt.getClaimAsString("tenantId");
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El JWT no contiene tenantId");
        }
        return tenantId;
    }
}