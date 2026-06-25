package com.wallet.controller;

import com.wallet.model.Transaction;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository txRepo;
    private final TransferService transferService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = transferService.getUserIdByEmail(
                userDetails.getUsername());
        return ResponseEntity.ok(
                txRepo.findByUserIdOrderByCreatedAtDesc(userId));
    }
}