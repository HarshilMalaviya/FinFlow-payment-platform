package com.transaction_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String transactionId,
        String status,
        BigDecimal amount,
        Long senderId,
        Long receiverId,
        String note,
        Instant timestamp
) {}
