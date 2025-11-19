package com.Gdev.pos_lite.pack;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackRepository extends JpaRepository<Pack, Long> {
    Optional<Pack> findByBarcode(String barcode);
}
