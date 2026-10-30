package com.Gdev.pos_lite.loyalty.dto;

public class RedeemResponse {

    private final String customerEmail;
    private final long pointsToRedeem;
    private final double discount;
    private final double finalTotal;
    private final long remainingPoints;

    public RedeemResponse(String customerEmail, long pointsToRedeem, double discount,
                          double finalTotal, long remainingPoints) {
        this.customerEmail = customerEmail;
        this.pointsToRedeem = pointsToRedeem;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.remainingPoints = remainingPoints;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public long getPointsToRedeem() {
        return pointsToRedeem;
    }

    public double getDiscount() {
        return discount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public long getRemainingPoints() {
        return remainingPoints;
    }
}
