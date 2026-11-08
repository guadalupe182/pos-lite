package com.Gdev.pos_lite.restock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestockReportServiceTest {

    @Mock
    private RestockReportRepository restockReportRepository;

    private RestockReportService restockReportService;

    @BeforeEach
    void setUp() {
        restockReportService = new RestockReportService(restockReportRepository);
    }

    @Test
    void guardaElReporteDeRestock() {
        RestockReport report = RestockReport.builder()
                .reportDate(LocalDate.of(2026, 11, 7))
                .productName("Aguacate Hass")
                .quantity(new BigDecimal("10"))
                .unit("caja")
                .build();

        RestockReport saved = RestockReport.builder()
                .id(1L)
                .reportDate(report.getReportDate())
                .productName(report.getProductName())
                .quantity(report.getQuantity())
                .unit(report.getUnit())
                .build();

        when(restockReportRepository.save(any(RestockReport.class))).thenReturn(saved);

        RestockReport result = restockReportService.create(report);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Aguacate Hass");
        verify(restockReportRepository).save(any(RestockReport.class));
    }
}
