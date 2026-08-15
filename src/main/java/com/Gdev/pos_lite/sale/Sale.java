package com.Gdev.pos_lite.sale;

import com.Gdev.pos_lite.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant saleDate = Instant.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> details = new ArrayList<>();

    @Column(nullable = false)
    private Double total;

    @Column(nullable = true)
    private String //"CASH", "CARD DEBIT". "CARD_CREDIT_MSI", "MERCADO_PAGO//"
    paymentMethod;

    //Only Cash
    private Double cashReceived;

    //Only Cash
    private Double cashChange;

    // Getters y Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(Double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public Double getCashChange() {
        return cashChange;
    }

    public void setCashChange(Double cashChange) {
        this.cashChange = cashChange;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
