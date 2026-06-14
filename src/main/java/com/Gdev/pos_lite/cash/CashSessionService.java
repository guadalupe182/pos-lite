package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CurrentSessionDto;
import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CashSessionService {

    private final CashSessionRepository cashSessionRepository;

    public CashSessionService(CashSessionRepository cashSessionRepository) {
        this.cashSessionRepository = cashSessionRepository;
    }

    @Transactional
    public CashSession openSession(OpenCashRequestDto request, String userEmail) {
        LocalDate today = LocalDate.now();

        // Verificar si ya hay una sesión abierta hoy
        if (cashSessionRepository.existsOpenSessionOnDate(today)) {
            throw new IllegalStateException("Ya existe una sesión de caja abierta para hoy. Solo se permite una apertura por día.");
        }

        Double initialCash = request.initialCash();
        if (initialCash == null || initialCash < 0) {
            throw new IllegalArgumentException("El monto inicial debe ser mayor o igual a cero.");
        }

        CashSession session = new CashSession(initialCash, userEmail);
        return cashSessionRepository.save(session);
    }

    public CashSession getCurrentOpenSession() {
        return cashSessionRepository.findTopByStatusOrderByOpenedAtDesc("OPEN")
                .orElseThrow(() -> new IllegalStateException("No hay una sesión de caja abierta. Debe abrir caja antes de vender."));
    }

    public boolean isOpen() {
        return cashSessionRepository.findTopByStatusOrderByOpenedAtDesc("OPEN").isPresent();
    }

    public CurrentSessionDto getCurrentSessionDto() {
        return cashSessionRepository.findTopByStatusOrderByOpenedAtDesc("OPEN")
                .map(s -> new CurrentSessionDto(s.getId(), s.getInitialCash(), s.getStatus(), s.getOpenedAt(), s.getOpenedBy()))
                .orElse(null);
    }
}