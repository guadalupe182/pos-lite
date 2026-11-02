package com.Gdev.pos_lite.style;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "boutique_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoutiqueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String size;

    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    // Multi-tenancy
    @Column(name = "tenant_id")
    private String tenantId;
}