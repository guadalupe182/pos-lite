package com.Gdev.pos_lite.restock;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "restock_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestockReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "product_name", nullable = false, length = 120)
    private String productName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(length = 120)
    private String supplier;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (reportDate == null) {
            reportDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
