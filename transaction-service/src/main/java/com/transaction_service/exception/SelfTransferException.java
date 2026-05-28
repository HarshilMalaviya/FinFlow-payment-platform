package com.transaction_service.exception;

public class SelfTransferException extends RuntimeException {
    public SelfTransferException() {
        super("Cannot transfer money to yourself");
    }
}

