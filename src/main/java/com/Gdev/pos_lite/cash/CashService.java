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
    private final CashSessionService cashSessionService;

    public CashService(CashClosureRepository cashClosureRepository,
                       SaleRepository saleRepository,
                       CashSessionService cashSessionService) {
        this.cashClosureRepository = cashClosureRepository;
        this.saleRepository = saleRepository;
        this.cashSessionService = cashSessionService;
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

        // 1. Validar que no haya un cierre ya registrado hoy
        if (cashClosureRepository.existsByClosureDate(today)) {
            throw new RuntimeException("La caja ya fue cerrada hoy. No se puede cerrar nuevamente.");
        }

        // 2. Obtener la sesión de caja abierta
        CashSession openSession = cashSessionService.getCurrentOpenSession();
        Double initialCash = openSession.getInitialCash();

        // 3. Calcular ventas en efectivo del día
        Instant start = getStartOfDay(today);
        Instant end = getEndOfDay(today);
        Double cashSales = saleRepository.getTotalCashSalesBetween(start, end);
        if (cashSales == null) cashSales = 0.0;

        // 4. Calcular efectivo esperado y diferencia
        Double expectedCash = initialCash + cashSales;
        Double finalCash = request.finalCash() != null ? request.finalCash() : 0.0;
        Double difference = finalCash - expectedCash;

        // 5. Guardar el cierre
        CashClosure closure = new CashClosure(today, initialCash, finalCash, expectedCash, difference, closedByEmail);
        CashClosure saved = cashClosureRepository.save(closure);

        // 6. Cerrar la sesión de caja (actualizar estado y datos)
        openSession.setStatus("CLOSED");
        openSession.setClosedAt(Instant.now());
        openSession.setExpectedCash(expectedCash);
        openSession.setActualCash(finalCash);
        openSession.setDifference(difference);
        cashSessionService.save(openSession);

        return saved;
    }

    public boolean isCashClosedToday() {
        return cashClosureRepository.existsByClosureDate(LocalDate.now());
    }
}