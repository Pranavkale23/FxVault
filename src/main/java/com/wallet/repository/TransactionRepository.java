package com.wallet.repository;

import com.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Used by fraud engine — count how many transactions
    // this user made in the last N seconds
    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.createdAt > :since")
    long countRecentTransactions(@Param("userId") Long userId,
                                  @Param("since") LocalDateTime since);

    // Used by fraud engine — find user's average transaction amount
    // to detect anomalous large transfers
    @Query("SELECT AVG(t.amount) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.status = 'COMPLETED'")
    BigDecimal findAverageAmount(@Param("userId") Long userId);
}