package com.Gdev.pos_lite.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CashSessionRepository extends JpaRepository<CashSession, Long> {
    Optional<CashSession> findByOpenedByAndStatus(String openedBy, String status);
}
