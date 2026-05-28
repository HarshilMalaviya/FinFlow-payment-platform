package com.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.transaction-url}")
    private String transactionServiceUrl;

    @PostMapping("/pay")
    public ResponseEntity<?> forwardPay(
            @RequestBody Object body,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        HttpHeaders headers = buildInternalHeaders(userId, email, role);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                transactionServiceUrl + "/api/v1/transactions/pay",
                HttpMethod.POST,
                entity,
                Object.class
        );
    }

    @GetMapping("/history")
    public ResponseEntity<?> forwardHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        HttpHeaders headers = buildInternalHeaders(userId, email, role);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                transactionServiceUrl + "/api/v1/transactions/history?page=" + page + "&size=" + size,
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    private HttpHeaders buildInternalHeaders(
            String userId, String email, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id",    userId);
        headers.set("X-User-Email", email);
        headers.set("X-User-Role",  role);
        return headers;
    }
}
