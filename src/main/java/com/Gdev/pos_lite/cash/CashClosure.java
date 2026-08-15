package com.Gdev.pos_lite.cash;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "cash_closure")
public class CashClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate closureDate;

    @Column(nullable = false)
    private Double initialCash;

    @Column(nullable = false)
    private Double finalCash;

    @Column(nullable = false)
    private Double expectedCash;

    @Column(nullable = false)
    private Double difference;

    @Column(nullable = false)
    private String closedBy;

    private Instant closedAt;

    public CashClosure() {
    }

    public CashClosure(LocalDate closureDate, Double initialCash, Double finalCash, Double expectedCash, Double difference, String closedBy) {
        this.closureDate = closureDate;
        this.initialCash = initialCash;
        this.finalCash = finalCash;
        this.expectedCash = expectedCash;
        this.difference = difference;
        this.closedBy = closedBy;
        this.closedAt = Instant.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(LocalDate closureDate) {
        this.closureDate = closureDate;
    }

    public Double getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(Double initialCash) {
        this.initialCash = initialCash;
    }

    public Double getFinalCash() {
        return finalCash;
    }

    public void setFinalCash(Double finalCash) {
        this.finalCash = finalCash;
    }

    public Double getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(Double expectedCash) {
        this.expectedCash = expectedCash;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }
}
