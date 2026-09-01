package com.Gdev.pos_lite.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO para OpenCashRequest")
public class OpenCashRequestDto {

    @Schema(description = "Cantidad inicial de efectivo", required = true)
    @NotNull(message = "La cantidad inicial de efectivo no puede ser nula")
    @Positive(message = "La cantidad inicial de efectivo debe ser un número positivo")
    private Double initialCash;

    @Schema(description = "Usuario que abre la sesión", required = true)
    @NotNull(message = "El usuario que abre la sesión no puede ser nulo")
    private String openedBy;

    // Getters and Setters
    public Double getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(Double initialCash) {
        this.initialCash = initialCash;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }
}
