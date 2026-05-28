package com.transaction_service.state;

import java.time.Instant;

public record Failed(
        String reason,
        Instant at
) implements TransactionState {}