package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CashCloseReportDto;
import com.Gdev.pos_lite.cash.dto.CloseCashRequestDto;
import com.Gdev.pos_lite.cash.dto.DailySummaryDto;
import com.Gdev.pos_lite.notification.NotificationService;
import com.Gdev.pos_lite.sale.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class CashService {

    private final CashClosureRepository cashClosureRepository;

    private final SaleRepository saleRepository;

    private final CashSessionService cashSessionService;

    private final NotificationService notificationService;

    public CashService(CashClosureRepository cashClosureRepository, SaleRepository saleRepository, CashSessionService cashSessionService, NotificationService notificationService) {
        this.cashClosureRepository = cashClosureRepository;
        this.saleRepository = saleRepository;
        this.cashSessionService = cashSessionService;
        this.notificationService = notificationService;
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
        if (totalSales == null)
            totalSales = 0.0;
        Long totalTransactions = saleRepository.countSalesBetween(start, end);
        if (totalTransactions == null)
            totalTransactions = 0L;
        return new DailySummaryDto(totalSales, totalTransactions);
    }

    @Transactional
    public CashCloseReportDto closeCash(CloseCashRequestDto request, String closedByEmail) {
        LocalDate today = LocalDate.now();
        // 1. Validar que no haya un cierre ya registrado hoy (CONFLICT)
        if (cashClosureRepository.existsByClosureDate(today)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La caja ya fue cerrada hoy.");
        }
        // 2. Validar que exista una sesión de caja abierta (BAD_REQUEST)
        if (!cashSessionService.isOpen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay sesión de caja abierta.");
        }
        // 3. Obtener la sesión de caja abierta
        CashSession openSession = cashSessionService.getCurrentOpenSession();
        Double initialCash = openSession.getInitialCash();
        Instant start = getStartOfDay(today);
        Instant end = getEndOfDay(today);
        // 4. Obtener ventas agrupadas por método de pago
        List<Object[]> grouped = saleRepository.sumTotalGroupedByPaymentMethod(start, end);
        Map<String, Double> totalsByMethod = new HashMap<>();
        for (Object[] row : grouped) {
            String method = (String) row[0];
            Double total = (Double) row[1];
            totalsByMethod.put(method, total);
        }
        // 5. Extraer totales específicos
        Double cashSales = totalsByMethod.getOrDefault("CASH", 0.0);
        Double mercadopagoSales = totalsByMethod.getOrDefault("MP", 0.0);
        // 6. Totales de tarjetas (DEBIT, CREDIT_*)
        Map<String, Double> cardBreakdown = new LinkedHashMap<>();
        Double totalCardSales = 0.0;
        for (String method : totalsByMethod.keySet()) {
            if (method.startsWith("DEBIT") || method.startsWith("CREDIT")) {
                cardBreakdown.put(method, totalsByMethod.get(method));
                totalCardSales += totalsByMethod.get(method);
            }
        }
        // 7. Calcular esperado y diferencia
        Double expectedCash = initialCash + cashSales;
        Double finalCash = request.finalCash() != null ? request.finalCash() : 0.0;
        Double difference = finalCash - expectedCash;
        // Generar notificación si hay faltante
        if (difference < 0) {
            String message = "Faltante de efectivo en cierre de caja: $" + Math.abs(difference);
            notificationService.createNotification("CASH_LOW", message);
        }
        // 8. Guardar el cierre
        CashClosure closure = new CashClosure(today, initialCash, finalCash, expectedCash, difference, closedByEmail);
        CashClosure saved = cashClosureRepository.save(closure);
        // 9. Cerrar la sesión de caja
        openSession.setStatus("CLOSED");
        openSession.setClosedAt(Instant.now());
        openSession.setExpectedCash(expectedCash);
        openSession.setActualCash(finalCash);
        openSession.setDifference(difference);
        cashSessionService.save(openSession);
        // 10. Devolver el reporte completo
        return new CashCloseReportDto(saved, cashSales, totalCardSales, cardBreakdown, mercadopagoSales);
    }

    public boolean isCashClosedToday() {
        return cashClosureRepository.existsByClosureDate(LocalDate.now());
    }
}
