package com.transaction_service.controller;

import com.transaction_service.dto.PaymentRequest;
import com.transaction_service.dto.PaymentResponse;
import com.transaction_service.dto.TransactionHistoryResponse;
import com.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(
            @Valid @RequestBody PaymentRequest paymentRequest,
            @RequestHeader("X-User-Id") Long senderId) {

        return ResponseEntity.ok(
                transactionService.processPayment(paymentRequest, senderId)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TransactionHistoryResponse>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(
                transactionService.getHistory(userId, page, size)
        );
    }
}
