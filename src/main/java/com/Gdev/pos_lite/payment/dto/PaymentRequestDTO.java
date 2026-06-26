package com.Gdev.pos_lite.payment.dto;

import java.math.BigDecimal;

public record PaymentRequestDTO(String description, Integer quantity, BigDecimal amount) {
}
