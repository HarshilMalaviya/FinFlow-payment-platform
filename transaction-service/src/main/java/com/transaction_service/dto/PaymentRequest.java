package com.transaction_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record PaymentRequest(

        @NotNull(message = "Receiver ID is required")
        Long receiverId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum transfer is ₹1")
        BigDecimal amount,

        String note
) {}
