package com.Gdev.pos_lite.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record PaymentRequestDTO(String description, Integer quantity, BigDecimal amount) {
}
