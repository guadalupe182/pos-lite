package com.Gdev.pos_lite.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface CashSessionRepository extends JpaRepository<CashSession, Long> {

    Optional<CashSession> findTopByStatusOrderByOpenedAtDesc(String status);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM CashSession s WHERE s.status = 'OPEN' AND DATE(s.openedAt) = :date")
    boolean existsOpenSessionOnDate(@Param("date") LocalDate date);
}
