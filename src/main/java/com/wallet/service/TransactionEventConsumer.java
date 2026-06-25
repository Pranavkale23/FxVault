package com.wallet.service;

import com.wallet.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionEventConsumer {

    private final EmailService emailService;

    public TransactionEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "transaction-events", groupId = "email-receipt-group")
    public void consumeTransactionEvent(TransactionEvent event) {
        log.info("Consumed transaction event for email receipt: TX ID {}", event.getTransactionId());
        
        try {
            emailService.sendReceipt(event);
        } catch (Exception e) {
            log.error("Failed to send email receipt for TX ID {}", event.getTransactionId(), e);
        }
    }
}
