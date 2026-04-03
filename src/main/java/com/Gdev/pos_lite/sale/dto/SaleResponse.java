package com.Gdev.pos_lite.sale.dto;

import java.time.Instant;
import java.util.List;

public class SaleResponse {
    private Long id;
    private Instant saleDate;
    private Double total;
    private List<SaleDetailResponse> details;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getSaleDate() { return saleDate; }
    public void setSaleDate(Instant saleDate) { this.saleDate = saleDate; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public List<SaleDetailResponse> getDetails() { return details; }
    public void setDetails(List<SaleDetailResponse> details) { this.details = details; }
}