package com.Gdev.pos_lite.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LicenseExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicenseExpirationScheduler.class);

    private final LicenseRepository licenseRepository;

    public LicenseExpirationScheduler(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiringLicenses() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(7);

        List<License> expiringLicenses = licenseRepository.findByExpirationDateBetween(today, limit);
        for (License license : expiringLicenses) {
            log.warn("Licencia próxima a expirar: key={}, tenant={}, expirationDate={}",
                    license.getLicenseKey(), license.getTenantId(), license.getExpirationDate());
        }
    }
}
