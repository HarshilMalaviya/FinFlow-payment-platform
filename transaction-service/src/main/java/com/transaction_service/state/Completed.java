package com.transaction_service.state;

import java.math.BigDecimal;
import java.time.Instant;

public record Completed(
        String txnId,
        BigDecimal amount,
        Instant at
) implements TransactionState {}