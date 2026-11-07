package com.Gdev.pos_lite.restock.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RestockReportRequest(
        @NotNull
        @JsonAlias({"date", "fecha", "report_date", "reportDate"})
        LocalDate reportDate,

        @NotBlank
        @Size(max = 120)
        @JsonAlias({"product", "item", "name", "avocadoType", "tipoAguacate", "producto"})
        String productName,

        @NotNull
        @Positive
        @JsonAlias({"qty", "amount", "cantidad"})
        BigDecimal quantity,

        @NotBlank
        @Size(max = 30)
        @JsonAlias({"unidad"})
        String unit,

        @Size(max = 120)
        @JsonAlias({"proveedor"})
        String supplier,

        @Size(max = 1000)
        @JsonAlias({"note", "observaciones", "comments"})
        String notes
) {
}
