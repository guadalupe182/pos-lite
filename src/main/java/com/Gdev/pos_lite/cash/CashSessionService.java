package com.Gdev.pos_lite.cash;

import com.Gdev.pos_lite.cash.dto.CurrentSessionDto;
import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class CashSessionService {

    private final CashSessionRepository repository;

    public CashSessionService(CashSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CashSession openSession(Double initialCash, String openedBy) {
        if (isSessionOpenForUser(openedBy)) {
            throw new IllegalStateException("El usuario ya tiene una sesión de caja abierta");
        }
        return repository.save(new CashSession(initialCash, openedBy));
    }

    @Transactional
    public CashSession openSession(OpenCashRequestDto dto, String openedBy) {
        Double initialCash = (dto != null && dto.initialCash() != null) ? dto.initialCash() : 0.0;
        return openSession(initialCash, openedBy);
    }

    @Transactional
    public CashSession closeSession(Long sessionId, Double actualCash, String closedBy) {
        CashSession session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        if (!"OPEN".equals(session.getStatus())) {
            throw new IllegalStateException("La sesión ya está cerrada");
        }

        session.setStatus("CLOSED");
        session.setClosedAt(Instant.now());
        session.setClosureDate(LocalDate.now());
        session.setActualCash(actualCash);
        session.setFinalCash(actualCash);
        session.setDifference(actualCash - session.getExpectedCash());

        return repository.save(session);
    }

    public boolean isSessionOpenForUser(String openedBy) {
        return repository.findByOpenedByAndStatus(openedBy, "OPEN").isPresent();
    }

    public boolean isOpen() {
        return repository.count() > 0;
    }

    public CashSession getCurrentOpenSession() {
        return repository.findAll().stream()
                .filter(s -> "OPEN".equals(s.getStatus()))
                .findFirst()
                .orElse(null);
    }

    public CurrentSessionDto getCurrentSessionDto() {
        return null;
    }

    public CashSession save(CashSession session) {
        return repository.save(session);
    }
}
