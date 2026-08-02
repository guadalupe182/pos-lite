package com.Gdev.pos_lite.sale.dto;

import java.time.Instant;
import java.util.List;

public class SaleResponse {
    private Long id;
    private Instant saleDate;
    private Double total;
    private Double cashReceived;
    private Double change;
    private List<SaleDetailResponse> details;
    private String customerEmail;
    private String paymentMethod;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getSaleDate() { return saleDate; }
    public void setSaleDate(Instant saleDate) { this.saleDate = saleDate; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Double getCashReceived() { return cashReceived; }
    public void setCashReceived(Double cashReceived) { this.cashReceived = cashReceived; }

    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }

    public List<SaleDetailResponse> getDetails() { return details; }
    public void setDetails(List<SaleDetailResponse> details) { this.details = details; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}