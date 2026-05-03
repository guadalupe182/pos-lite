package com.Gdev.pos_lite.sale.dto;

import java.util.List;

public class SaleRequest {
    private List<SaleItemRequest> items;
    private Double cashReceived;
    private Double change;
    private String customerEmail;  // ← Campo para email del comprador

    // Getters y Setters
    public List<SaleItemRequest> getItems() { return items; }
    public void setItems(List<SaleItemRequest> items) { this.items = items; }

    public Double getCashReceived() { return cashReceived; }
    public void setCashReceived(Double cashReceived) { this.cashReceived = cashReceived; }

    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}