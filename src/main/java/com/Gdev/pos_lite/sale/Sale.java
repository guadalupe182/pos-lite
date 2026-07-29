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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getSaleDate() { return saleDate; }
    public void setSaleDate(Instant saleDate) { this.saleDate = saleDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<SaleDetail> getDetails() { return details; }
    public void setDetails(List<SaleDetail> details) { this.details = details; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}