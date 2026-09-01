package com.Gdev.pos_lite.cash.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class OpenCashRequestDto {

    @NotNull(message = "El monto inicial no puede ser nulo")
    @PositiveOrZero(message = "El monto inicial debe ser mayor o igual a cero")
    private BigDecimal initialAmount;

    @NotNull(message = "El usuario que abre la sesión no puede ser nulo")
    private String openedBy;

    public Double initialCash() {
        return initialAmount != null ? initialAmount.doubleValue() : null;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }
}
