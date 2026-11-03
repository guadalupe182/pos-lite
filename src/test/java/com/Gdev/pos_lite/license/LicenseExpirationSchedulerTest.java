package com.Gdev.pos_lite.license;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenseExpirationSchedulerTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Test
    void consultaLasLicenciasQueExpiranEnLosProximosSieteDias() {
        LicenseExpirationScheduler scheduler = new LicenseExpirationScheduler(licenseRepository);
        LocalDate today = LocalDate.now();
        when(licenseRepository.findByExpirationDateBetween(today, today.plusDays(7)))
                .thenReturn(List.of());

        scheduler.checkExpiringLicenses();

        verify(licenseRepository).findByExpirationDateBetween(today, today.plusDays(7));
    }
}
