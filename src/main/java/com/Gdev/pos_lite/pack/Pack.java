// src/main/java/com/Gdev/pos_lite/pack/Pack.java
package com.Gdev.pos_lite.pack;

import com.Gdev.pos_lite.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String // código del pack
    barcode;

    @Column(nullable = false)
    private String // nombre del pack (ej. "Refresco 3L x 6")
    name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal // precio total del pack
    price;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackItem> items;
}
