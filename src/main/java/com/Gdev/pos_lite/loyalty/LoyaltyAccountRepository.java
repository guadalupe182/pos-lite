package com.Gdev.pos_lite.loyalty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {

    Optional<LoyaltyAccount> findByCustomerEmailIgnoreCase(String customerEmail);
}
