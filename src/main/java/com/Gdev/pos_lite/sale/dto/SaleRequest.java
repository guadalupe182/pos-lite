package com.Gdev.pos_lite.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO para SaleRequest")
public class SaleRequest {

    @Schema(description = "Campo items", example = "ejemplo")
    private List<SaleItemRequest> items;

    @Schema(description = "Campo cashReceived", example = "ejemplo")
    private Double cashReceived;

    @Schema(description = "Campo change", example = "ejemplo")
    private Double change;

    // ← Campo para email del comprador
    @Schema(description = "Campo customerEmail", example = "ejemplo")
    private String customerEmail;

    @Schema(description = "Campo paymentMethod", example = "ejemplo")
    private String paymentMethod;

    // Getters y Setters
    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public Double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(Double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
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
