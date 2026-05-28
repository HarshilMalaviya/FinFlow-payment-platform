package com.transaction_service.state;

import java.time.Instant;

public record Initiated(Instant at) implements TransactionState {}
