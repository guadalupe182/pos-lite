package com.Gdev.pos_lite.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(Instant from, Instant to);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    Double getTotalSalesBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    Long countSalesBetween(@Param("start") Instant start, @Param("end") Instant end);

    //suma solo ventas en efectivo
    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.paymentMethod = 'CASH'")
    Double getTotalCashSalesBetween(@Param("start") Instant start, @Param("end") Instant end);
}