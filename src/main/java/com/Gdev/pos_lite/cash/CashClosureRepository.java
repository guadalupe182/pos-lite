package com.Gdev.pos_lite.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface CashClosureRepository extends JpaRepository<CashClosure, Long> {
    Optional<CashClosure> findTopByClosureDateOrderByClosedAtDesc(LocalDate date);
    boolean existsByClosureDate(LocalDate date);
}