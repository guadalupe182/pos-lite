package com.Gdev.pos_lite.cash.dto;

import java.time.Instant;

public record CurrentSessionDto(Long id, Double initialCash, String status, Instant openedAt, String openedBy) {}