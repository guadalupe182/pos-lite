package com.Gdev.pos_lite.cash;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para CloseCashRequest")
public class CloseCashRequestDto {

    @Schema(description = "Fecha de cierre", required = true)
    @NotNull(message = "La fecha de cierre no puede ser nula")
    private LocalDate closureDate;

    @Schema(description = "Usuario que cierra la sesión", required = true)
    @NotNull(message = "El usuario que cierra la sesión no puede ser nulo")
    private String closedBy;

    // Getters and Setters
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
