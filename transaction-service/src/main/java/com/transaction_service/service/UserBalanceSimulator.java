package com.transaction_service.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserBalanceSimulator {

    private final ConcurrentHashMap<Long, BigDecimal> balances =
            new ConcurrentHashMap<>();

    public UserBalanceSimulator() {

        balances.put(1L, new BigDecimal("5000.00"));
        balances.put(2L, new BigDecimal("3000.00"));
        balances.put(3L, new BigDecimal("1500.00"));
    }

    public boolean userExists(Long userId) {
        return balances.containsKey(userId);
    }

    public BigDecimal getBalance(Long userId) {
        return balances.getOrDefault(userId, BigDecimal.ZERO);
    }

    public void deduct(Long userId, BigDecimal amount) {
        balances.compute(userId, (k, v) -> v.subtract(amount));
    }

    public void credit(Long userId, BigDecimal amount) {
        balances.compute(userId, (k, v) -> v.add(amount));
    }
}