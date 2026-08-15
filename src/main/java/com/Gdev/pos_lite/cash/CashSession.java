package com.Gdev.pos_lite.cash;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cash_session")
public class CashSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant openedAt = Instant.now();

    private Instant closedAt;

    @Column(nullable = false)
    private Double initialCash;

    // se calculará al cerrar
    private Double expectedCash;

    private Double actualCash;

    private Double difference;

    @Column(nullable = false)
    private String // "OPEN" o "CLOSED"
    status;

    @Column(nullable = false)
    private String // email del usuario
    openedBy;

    // Constructor vacío
    public CashSession() {
    }

    // Constructor para apertura
    public CashSession(Double initialCash, String openedBy) {
        this.initialCash = initialCash;
        this.openedBy = openedBy;
        this.status = "OPEN";
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public Double getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(Double initialCash) {
        this.initialCash = initialCash;
    }

    public Double getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(Double expectedCash) {
        this.expectedCash = expectedCash;
    }

    public Double getActualCash() {
        return actualCash;
    }

    public void setActualCash(Double actualCash) {
        this.actualCash = actualCash;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }
}
