package com.Gdev.pos_lite.branch;

import com.Gdev.pos_lite.branch.dto.BranchRequest;
import com.Gdev.pos_lite.branch.dto.BranchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branch", description = "Endpoints para la gestión de sucursales")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "Obtener todas las sucursales activas")
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una sucursal por ID")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva sucursal")
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchRequest request) {
        return new ResponseEntity<>(branchService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sucursal existente")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable Long id, 
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Activar/Desactivar una sucursal")
    public ResponseEntity<BranchResponse> toggleBranchActive(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.toggleActive(id));
    }
}
