package com.transaction_service.state;

import java.time.Instant;

public record Processing(Instant at) implements TransactionState {}
