package com.Gdev.pos_lite.license;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<License> findByLicenseKey(String licenseKey);
}
