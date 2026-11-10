package com.Gdev.pos_lite.license;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);
}
