package com.Gdev.pos_lite.loyalty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "loyalty_account", uniqueConstraints = {
        @UniqueConstraint(name = "uk_loyalty_account_email", columnNames = "customer_email")
})
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String customerEmail;

    @Column(name = "points_balance", nullable = false)
    private long pointsBalance;

    @Version
    private long version;

    public Long getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public long getPointsBalance() {
        return pointsBalance;
    }

    public void earn(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("Los puntos acumulados no pueden ser negativos");
        }
        pointsBalance = Math.addExact(pointsBalance, points);
    }

    public void redeem(long points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Los puntos a canjear deben ser mayores que cero");
        }
        if (points > pointsBalance) {
            throw new IllegalArgumentException("Saldo de puntos insuficiente");
        }
        pointsBalance -= points;
    }
}
