package com.Gdev.pos_lite.restock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestockReportService {

    private final RestockReportRepository restockReportRepository;

    public RestockReportService(RestockReportRepository restockReportRepository) {
        this.restockReportRepository = restockReportRepository;
    }

    @Transactional
    public RestockReport create(RestockReport report) {
        if (report.getReportDate() == null) {
            report.setReportDate(java.time.LocalDate.now());
        }
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(java.time.Instant.now());
        }
        return restockReportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public java.util.List<RestockReport> findAll() {
        return restockReportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RestockReport findById(Long id) {
        return restockReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte de restock no encontrado: " + id));
    }
}
