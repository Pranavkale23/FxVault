package com.wallet.service;

import com.wallet.dto.FraudResult;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FraudScoringEngine {

    private final TransactionRepository txRepo;
    private final WalletRepository walletRepo;

    public FraudScoringEngine(TransactionRepository txRepo, WalletRepository walletRepo) {
        this.txRepo = txRepo;
        this.walletRepo = walletRepo;
    }

    public FraudResult evaluate(Long userId,
                                 String targetCurrency,
                                 BigDecimal amount) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // Rule 1: Velocity — 3+ transactions in last 60 seconds
        long recentCount = txRepo.countRecentTransactions(
            userId, LocalDateTime.now().minusSeconds(60)
        );
        if (recentCount >= 3) {
            score += 30;
            reasons.add("HIGH_VELOCITY");
        }

        // Rule 2: Amount anomaly — more than 5x user average
        BigDecimal avg = txRepo.findAverageAmount(userId);
        if (avg != null && avg.compareTo(BigDecimal.ZERO) > 0) {
            if (amount.compareTo(avg.multiply(new BigDecimal("5"))) > 0) {
                score += 25;
                reasons.add("ANOMALOUS_AMOUNT");
            }
        }

        // Rule 3: New currency never used before
        boolean newCurrency = !walletRepo
            .existsByUserIdAndCurrency(userId, targetCurrency);
        if (newCurrency) {
            score += 15;
            reasons.add("NEW_CURRENCY");
        }

        // Rule 4: Off-hours transaction (2am–4am)
        int hour = LocalDateTime.now().getHour();
        if (hour >= 2 && hour <= 4) {
            score += 10;
            reasons.add("OFF_HOURS");
        }

        // Rule 5: Large round number
        if (amount.remainder(new BigDecimal("1000"))
                .compareTo(BigDecimal.ZERO) == 0
                && amount.compareTo(new BigDecimal("10000")) >= 0) {
            score += 10;
            reasons.add("ROUND_LARGE_AMOUNT");
        }

        String decision = score >= 70 ? "BLOCK"
                        : score >= 40 ? "REVIEW"
                        : "APPROVE";

        log.debug("Fraud score for user {}: {} — {} — {}",
            userId, score, decision, reasons);

        return new FraudResult(score, decision, reasons);
    }
}