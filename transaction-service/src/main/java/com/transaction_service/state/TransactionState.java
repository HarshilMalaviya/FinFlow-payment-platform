package com.transaction_service.state;

public sealed interface TransactionState
        permits Initiated, Processing, Completed, Failed, Refunded {}
