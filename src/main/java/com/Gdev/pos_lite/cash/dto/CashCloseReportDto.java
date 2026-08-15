package com.Gdev.pos_lite.cash.dto;

import com.Gdev.pos_lite.cash.CashClosure;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record CashCloseReportDto(Long id, LocalDate closureDate, Double initialCash, Double finalCash, Double expectedCash, Double difference, String closedBy, Instant closedAt, Double totalCashSales, Double totalCardSales, Map<String, Double> cardBreakdown, Double totalMercadoPagoSales, Double totalSales) {

    public CashCloseReportDto(CashClosure closure, Double totalCashSales, Double totalCardSales, Map<String, Double> cardBreakdown, Double totalMercadoPagoSales) {
        this(closure.getId(), closure.getClosureDate(), closure.getInitialCash(), closure.getFinalCash(), closure.getExpectedCash(), closure.getDifference(), closure.getClosedBy(), closure.getClosedAt(), totalCashSales, totalCardSales, cardBreakdown, totalMercadoPagoSales, totalCashSales + totalCardSales + totalMercadoPagoSales);
    }
}
