package com.Gdev.pos_lite.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(Instant from, Instant to);
}