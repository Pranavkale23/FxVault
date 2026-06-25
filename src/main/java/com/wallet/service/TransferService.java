package com.wallet.service;

import com.wallet.dto.FraudResult;
import com.wallet.dto.RateQuote;
import com.wallet.dto.TransactionResponse;
import com.wallet.dto.TransferRequest;
import com.wallet.event.TransactionEvent;
import com.wallet.exception.FraudBlockedException;
import com.wallet.exception.InsufficientFundsException;
import com.wallet.exception.WalletNotFoundException;
import com.wallet.model.Transaction;
import com.wallet.model.Wallet;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.UserRepository;
import com.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class TransferService {

    private final WalletRepository walletRepo;
    private final TransactionRepository txRepo;
    private final UserRepository userRepo;
    private final SmartFxRouter fxRouter;
    private final FraudScoringEngine fraudEngine;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransferService(WalletRepository walletRepo, TransactionRepository txRepo, UserRepository userRepo, SmartFxRouter fxRouter, FraudScoringEngine fraudEngine, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.walletRepo = walletRepo;
        this.txRepo = txRepo;
        this.userRepo = userRepo;
        this.fxRouter = fxRouter;
        this.fraudEngine = fraudEngine;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public TransactionResponse transfer(Long userId, String email, TransferRequest req) {

        // 1. Idempotency Check
        if (req.getIdempotencyKey() != null && txRepo.existsByIdempotencyKey(req.getIdempotencyKey())) {
            log.info("Idempotent request received for key: {}", req.getIdempotencyKey());
            Transaction existingTx = txRepo.findByIdempotencyKey(req.getIdempotencyKey()).get();
            return TransactionResponse.from(existingTx, BigDecimal.ZERO);
        }

        // 2. Fetch Wallets
        Wallet sourceWallet = walletRepo.findById(req.getSourceWalletId())
            .orElseThrow(() -> new WalletNotFoundException("Source wallet not found"));
            
        if (!sourceWallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to use this source wallet");
        }
        
        Wallet targetWallet = walletRepo.findById(req.getTargetWalletId())
            .orElseThrow(() -> new WalletNotFoundException("Target wallet not found"));

        // 3. Fraud Check
        FraudResult fraud = fraudEngine.evaluate(userId, targetWallet.getCurrency(), req.getAmount());
        if ("BLOCK".equals(fraud.getDecision())) {
            throw new FraudBlockedException("Transaction blocked by fraud engine");
        }

        // 4. FX Rate
        RateQuote quote = fxRouter.getBestRate(sourceWallet.getCurrency(), targetWallet.getCurrency());
        BigDecimal convertedAmount = req.getAmount().multiply(quote.getRate());

        // 5. Wallet balances
        if (sourceWallet.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Not enough funds in source wallet");
        }
        
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(req.getAmount()));
        walletRepo.save(sourceWallet);

        targetWallet.setBalance(targetWallet.getBalance().add(convertedAmount));
        walletRepo.save(targetWallet);

        // 6. Transaction
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setIdempotencyKey(req.getIdempotencyKey());
        tx.setSourceCurrency(sourceWallet.getCurrency());
        tx.setTargetCurrency(targetWallet.getCurrency());
        tx.setAmount(req.getAmount());
        tx.setConvertedAmount(convertedAmount);
        tx.setFxRate(quote.getRate());
        tx.setFxProvider(quote.getProvider());
        tx.setFraudScore(fraud.getScore());
        tx.setFraudDecision(fraud.getDecision());
        tx.setStatus("COMPLETED");
        tx.setCreatedAt(LocalDateTime.now());
        
        tx = txRepo.save(tx);

        // 7. Events
        try {
            TransactionEvent event = TransactionEvent.from(tx, userId, email, quote.getSavings());
            kafkaTemplate.send("transaction-events", event);
        } catch (Exception e) {
            log.error("Failed to send transaction event to Kafka. Transfer succeeded but email receipt may be delayed.", e);
        }

        return TransactionResponse.from(tx, quote.getSavings());
    }

    @Transactional
    public TransactionResponse splitTransfer(com.wallet.dto.SplitTransferRequest req, Long userId) {
        throw new UnsupportedOperationException("Split transfer is not implemented yet");
    }

    public Long getUserIdByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}