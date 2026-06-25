package com.wallet.controller;

import com.wallet.model.Transaction;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;

    public TransactionController(TransactionRepository transactionRepo, UserRepository userRepo) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepo.findByEmail(userDetails.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(
            transactionRepo.findByUserIdOrderByCreatedAtDesc(userId));
    }
}