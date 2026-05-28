package com.transaction_service.state;

import java.time.Instant;

public record Refunded(
        String originalTxnId,
        Instant at
) implements TransactionState {}