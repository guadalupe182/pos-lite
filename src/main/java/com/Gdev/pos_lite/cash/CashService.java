package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CashCloseReportDto;
import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import com.Gdev.pos_lite.sale.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

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
    public CashCloseReportDto closeCash(CloseCashRequestDto request, String closedByEmail) {
        LocalDate today = LocalDate.now();

        // 1. Validar que no haya un cierre ya registrado hoy
        if (cashClosureRepository.existsByClosureDate(today)) {
            throw new RuntimeException("La caja ya fue cerrada hoy. No se puede cerrar nuevamente.");
        }

        // 2. Obtener la sesión de caja abierta
        CashSession openSession = cashSessionService.getCurrentOpenSession();
        Double initialCash = openSession.getInitialCash();

        Instant start = getStartOfDay(today);
        Instant end = getEndOfDay(today);

        // 3. Obtener ventas agrupadas por método de pago
        List<Object[]> grouped = saleRepository.sumTotalGroupedByPaymentMethod(start, end);
        Map<String, Double> totalsByMethod = new HashMap<>();
        for (Object[] row : grouped) {
            String method = (String) row[0];
            Double total = (Double) row[1];
            totalsByMethod.put(method, total);
        }

        // 4. Extraer totales específicos
        Double cashSales = totalsByMethod.getOrDefault("CASH", 0.0);
        Double mercadopagoSales = totalsByMethod.getOrDefault("MP", 0.0);

        // 5. Totales de tarjetas (DEBIT, CREDIT_*)
        Map<String, Double> cardBreakdown = new LinkedHashMap<>();
        Double totalCardSales = 0.0;
        for (String method : totalsByMethod.keySet()) {
            if (method.startsWith("DEBIT") || method.startsWith("CREDIT")) {
                cardBreakdown.put(method, totalsByMethod.get(method));
                totalCardSales += totalsByMethod.get(method);
            }
        }

        // 6. Calcular esperado y diferencia
        Double expectedCash = initialCash + cashSales;
        Double finalCash = request.finalCash() != null ? request.finalCash() : 0.0;
        Double difference = finalCash - expectedCash;

        // 7. Guardar el cierre
        CashClosure closure = new CashClosure(today, initialCash, finalCash, expectedCash, difference, closedByEmail);
        CashClosure saved = cashClosureRepository.save(closure);

        // 8. Cerrar la sesión de caja
        openSession.setStatus("CLOSED");
        openSession.setClosedAt(Instant.now());
        openSession.setExpectedCash(expectedCash);
        openSession.setActualCash(finalCash);
        openSession.setDifference(difference);
        cashSessionService.save(openSession);

        // 9. Devolver el reporte completo
        return new CashCloseReportDto(saved, cashSales, totalCardSales, cardBreakdown, mercadopagoSales);
    }

    public boolean isCashClosedToday() {
        return cashClosureRepository.existsByClosureDate(LocalDate.now());
    }
}