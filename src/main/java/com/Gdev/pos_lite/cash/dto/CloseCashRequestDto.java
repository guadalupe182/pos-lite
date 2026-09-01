package com.Gdev.pos_lite.cash.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public class CloseCashRequestDto {

    @NotNull(message = "La fecha de cierre no puede ser nula")
    private LocalDate closureDate;

    private BigDecimal finalAmount;

    @NotNull(message = "El usuario que cierra la sesión no puede ser nulo")
    private String closedBy;

    public Double finalCash() {
        return finalAmount != null ? finalAmount.doubleValue() : null;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDate getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(LocalDate closureDate) {
        this.closureDate = closureDate;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }
}
