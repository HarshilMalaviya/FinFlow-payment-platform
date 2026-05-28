package com.transaction_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionHistoryResponse(
        String transactionId,
        String type,
        BigDecimal amount,
        String counterpartyId,
        String status,
        String note,
        Instant timestamp
) {}
