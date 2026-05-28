package com.transaction_service.repository;

import com.transaction_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, String> {


    @Query("SELECT t FROM Transaction t WHERE t.senderId = :userId OR t.receiverId = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);


    Page<Transaction> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);


    Page<Transaction> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
}