package com.Gdev.pos_lite.cash;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "cash_session")
public class CashSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant openedAt = Instant.now();

    private Instant closedAt;

    private Instant closureDate;

    @Column(nullable = false)
    private Double initialCash;

    // Se calculará al cerrar
    private Double expectedCash;

    private Double actualCash;

    private Double finalCash;

    private Double difference;

    @Column(nullable = false)
    private String status; // "OPEN" o "CLOSED"

    @Column(nullable = false)
    private String openedBy; // Email del usuario

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

    public Instant getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(Instant closureDate) {
        this.closureDate = closureDate;
    }

    // Sobrecarga por si CashSessionService envía LocalDate
    public void setClosureDate(LocalDate closureDate) {
        this.closureDate = (closureDate != null)
                ? closureDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                : null;
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

    public Double getFinalCash() {
        return finalCash;
    }

    public void setFinalCash(Double finalCash) {
        this.finalCash = finalCash;
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