package com.Gdev.pos_lite.fit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@Tag(name = "Membership", description = "Endpoints para la gestión de membresías (POS Fit)")
@PreAuthorize("hasAuthority('FIT')")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping
    @Operation(summary = "list", description = "Lista todas las membresías")
    public List<Membership> list() {
        return membershipService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get", description = "Obtiene una membresía por id")
    public Membership get(@PathVariable Long id) {
        return membershipService.findById(id);
    }

    @PostMapping
    @Operation(summary = "create", description = "Crea una nueva membresía")
    public Membership create(@RequestBody Membership membership) {
        return membershipService.create(membership);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update", description = "Actualiza una membresía")
    public Membership update(@PathVariable Long id, @RequestBody Membership membership) {
        return membershipService.update(id, membership);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete", description = "Elimina una membresía")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membershipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}