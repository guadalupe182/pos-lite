package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import com.Gdev.pos_lite.sale.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class CashService {

    private final CashClosureRepository cashClosureRepository;
    private final SaleRepository saleRepository;

    public CashService(CashClosureRepository cashClosureRepository, SaleRepository saleRepository) {
        this.cashClosureRepository = cashClosureRepository;
        this.saleRepository = saleRepository;
    }

    private Instant getStartOfDay(LocalDate date) {
        return date.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getEndOfDay(LocalDate date) {
        return date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    public DailySummaryDto getDailySummary() {
        LocalDate today = LocalDate.now();
        Instant start = getStartOfDay(today);
        Instant end = getEndOfDay(today);
        Double totalSales = saleRepository.getTotalSalesBetween(start, end);
        if (totalSales == null) totalSales = 0.0;
        Long totalTransactions = saleRepository.countSalesBetween(start, end);
        if (totalTransactions == null) totalTransactions = 0L;
        return new DailySummaryDto(totalSales, totalTransactions);
    }

    @Transactional
    public CashClosure closeCash(CloseCashRequestDto request, String closedByEmail) {
        LocalDate today = LocalDate.now();
        if (cashClosureRepository.existsByClosureDate(today)) {
            throw new RuntimeException("La caja ya fue cerrada hoy. No se puede cerrar nuevamente.");
        }
        Instant start = getStartOfDay(today);
        Instant end = getEndOfDay(today);
        Double cashSales = saleRepository.getTotalSalesBetween(start, end);
        if (cashSales == null) cashSales = 0.0;
        Double initialCash = request.initialCash() != null ? request.initialCash() : 0.0;
        Double finalCash = request.finalCash() != null ? request.finalCash() : 0.0;
        // Versión simplificada: esperado = 0 (se cambiará en issue #6)
        Double expectedCash = 0.0;
        Double difference = finalCash - expectedCash;
        CashClosure closure = new CashClosure(today, initialCash, finalCash, expectedCash, difference, closedByEmail);
        return cashClosureRepository.save(closure);
    }

    public boolean isCashClosedToday() {
        return cashClosureRepository.existsByClosureDate(LocalDate.now());
    }
}