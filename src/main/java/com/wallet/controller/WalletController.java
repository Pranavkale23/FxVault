package com.wallet.controller;

import com.wallet.model.Wallet;
import com.wallet.repository.UserRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.service.TransferService;
import com.wallet.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletRepository walletRepo;
    private final UserRepository userRepo;
    private final TransferService transferService;
    private final com.wallet.service.SmartFxRouter fxRouter;
    private final com.wallet.repository.FxRateRepository fxRateRepo;

    public WalletController(WalletRepository walletRepo, UserRepository userRepo, TransferService transferService, com.wallet.service.SmartFxRouter fxRouter, com.wallet.repository.FxRateRepository fxRateRepo) {
        this.walletRepo = walletRepo;
        this.userRepo = userRepo;
        this.transferService = transferService;
        this.fxRouter = fxRouter;
        this.fxRateRepo = fxRateRepo;
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getWallets(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(walletRepo.findByUserId(userId));
    }

    @GetMapping("/rates")
    public ResponseEntity<List<com.wallet.model.FxRate>> getLiveRates() {
        return ResponseEntity.ok(fxRateRepo.findAll());
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<Wallet> wallets = walletRepo.findByUserId(userId);
        
        BigDecimal totalUsd = BigDecimal.ZERO;
        for (Wallet w : wallets) {
            if ("USD".equals(w.getCurrency())) {
                totalUsd = totalUsd.add(w.getBalance());
            } else {
                try {
                    RateQuote quote = fxRouter.getBestRate(w.getCurrency(), "USD");
                    totalUsd = totalUsd.add(w.getBalance().multiply(quote.getRate()));
                } catch (Exception e) {
                    // Ignore wallets without exchange rates for the total balance calculation
                }
            }
        }
        return ResponseEntity.ok(Map.of("totalBalanceUSD", totalUsd));
    }

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        String currency = body.get("currency").toUpperCase();

        if (walletRepo.existsByUserIdAndCurrency(userId, currency)) {
            return ResponseEntity.badRequest().build();
        }

        var user = userRepo.findById(userId).orElseThrow();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.ZERO);

        return ResponseEntity.ok(walletRepo.save(wallet));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Wallet> deposit(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Long walletId = Long.valueOf(body.get("walletId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        
        Wallet wallet = walletRepo.findById(walletId).orElseThrow();
        if (!wallet.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        
        wallet.setBalance(wallet.getBalance().add(amount));
        return ResponseEntity.ok(walletRepo.save(wallet));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @RequestBody TransferRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(transferService.transfer(userId, userDetails.getUsername(), req));
    }

    @PostMapping("/split-transfer")
    public ResponseEntity<TransactionResponse> splitTransfer(
            @RequestBody SplitTransferRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(transferService.splitTransfer(req, userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepo.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }
}