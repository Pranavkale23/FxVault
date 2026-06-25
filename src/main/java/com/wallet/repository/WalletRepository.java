package com.wallet.repository;

import com.wallet.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Pessimistic lock — blocks other transactions from modifying
    // this wallet row until current transaction commits
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.currency = :currency")
    Optional<Wallet> findWithLock(@Param("userId") Long userId,
                                   @Param("currency") String currency);

    List<Wallet> findByUserId(Long userId);

    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);

    boolean existsByUserIdAndCurrency(Long userId, String currency);
}