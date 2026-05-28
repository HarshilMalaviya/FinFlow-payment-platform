package com.transaction_service.service;

import com.transaction_service.dto.PaymentRequest;
import com.transaction_service.dto.PaymentResponse;
import com.transaction_service.dto.TransactionHistoryResponse;
import com.transaction_service.entity.Status;
import com.transaction_service.entity.Transaction;
import com.transaction_service.exception.InsufficientBalanceException;
import com.transaction_service.exception.SelfTransferException;
import com.transaction_service.exception.UserNotFoundException;
import com.transaction_service.repository.TransactionRepository;
import com.transaction_service.state.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;


    private final UserBalanceSimulator userBalanceSimulator;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, Long senderId) {

        log.info("Processing payment: sender={}, receiver={}, amount={}",
                senderId, request.receiverId(), request.amount());


        if (senderId.equals(request.receiverId())) {
            throw new SelfTransferException();
        }


        if (!userBalanceSimulator.userExists(request.receiverId())) {
            throw new UserNotFoundException(request.receiverId());
        }


        BigDecimal senderBalance = userBalanceSimulator.getBalance(senderId);
        if (senderBalance.compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException();
        }


        TransactionState state = new Initiated(Instant.now());
        String txnId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .id(txnId)
                .senderId(senderId)
                .receiverId(request.receiverId())
                .amount(request.amount())
                .status(Status.INITIATED)
                .note(request.note())
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction {} saved with status INITIATED", txnId);


        state = new Processing(Instant.now());
        transaction.setStatus(Status.PROCESSING);
        transactionRepository.save(transaction);

        try {

            userBalanceSimulator.deduct(senderId, request.amount());


            userBalanceSimulator.credit(request.receiverId(), request.amount());


            state = new Completed(txnId, request.amount(), Instant.now());
            transaction.setStatus(Status.COMPLETED);
            transactionRepository.save(transaction);
            log.info("Transaction {} COMPLETED", txnId);

        } catch (Exception e) {
            state = new Failed(e.getMessage(), Instant.now());
            transaction.setStatus(Status.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            log.error("Transaction {} FAILED: {}", txnId, e.getMessage());

            throw new RuntimeException("Payment processing failed");
        }

        String message;

        if (state instanceof Initiated) {
            Initiated i = (Initiated) state;
            message = "Payment initiated at " + i.at();

        } else if (state instanceof Processing) {
            Processing p = (Processing) state;
            message = "Payment processing since " + p.at();

        } else if (state instanceof Completed) {
            Completed c = (Completed) state;
            message = "₹" + c.amount() + " transferred successfully";

        } else if (state instanceof Failed) {
            Failed f = (Failed) state;
            message = "Payment failed: " + f.reason();

        } else if (state instanceof Refunded) {
            Refunded r = (Refunded) state;
            message = "Refunded from txn " + r.originalTxnId();

        } else {
            throw new IllegalStateException("Unexpected state: " + state);
        }
        log.info("Transaction result: {}", message);

        return new PaymentResponse(
                txnId,
                "COMPLETED",
                request.amount(),
                senderId,
                request.receiverId(),
                request.note(),
                Instant.now()
        );
    }

    public Page<TransactionHistoryResponse> getHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository
                .findByUserId(userId, pageable)
                .map(txn -> new TransactionHistoryResponse(
                        txn.getId(),
                        txn.getSenderId().equals(userId) ? "SENT" : "RECEIVED",
                        txn.getAmount(),
                        txn.getSenderId().equals(userId)
                                ? String.valueOf(txn.getReceiverId())
                                : String.valueOf(txn.getSenderId()),
                        txn.getStatus().toString(),
                        txn.getNote(),
                        txn.getCreatedAt()
                ));
    }
}