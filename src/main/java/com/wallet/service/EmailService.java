package com.wallet.service;

import org.springframework.stereotype.Service;

import com.wallet.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    
    public void sendReceipt(TransactionEvent event) {
        log.info("Sending receipt to user {} for transaction {}", event.getUserId(), event.getTransactionId());
        // TODO: Implement actual email sending logic
    }
}