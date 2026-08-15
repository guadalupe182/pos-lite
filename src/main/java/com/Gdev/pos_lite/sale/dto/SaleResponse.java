package com.Gdev.pos_lite.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "DTO para SaleResponse")
public class SaleResponse {

    @Schema(description = "Campo id", example = "ejemplo")
    private Long id;

    @Schema(description = "Campo saleDate", example = "ejemplo")
    private Instant saleDate;

    @Schema(description = "Campo total", example = "ejemplo")
    private Double total;

    @Schema(description = "Campo details", example = "ejemplo")
    private List<SaleDetailResponse> details;

    // ← NUEVO
    @Schema(description = "Campo customerEmail", example = "ejemplo")
    private String customerEmail;

    // ← NUEVO
    @Schema(description = "Campo paymentMethod", example = "ejemplo")
    private String paymentMethod;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Instant saleDate) {
        this.saleDate = saleDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<SaleDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetailResponse> details) {
        this.details = details;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
