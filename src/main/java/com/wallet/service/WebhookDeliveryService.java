package com.wallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.event.TransactionEvent;
import com.wallet.model.WebhookDelivery;
import com.wallet.model.WebhookEndpoint;
import com.wallet.repository.WebhookDeliveryRepository;
import com.wallet.repository.WebhookEndpointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class WebhookDeliveryService {

    private final WebhookEndpointRepository endpointRepo;
    private final WebhookDeliveryRepository deliveryRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WebhookDeliveryService(WebhookEndpointRepository endpointRepo, WebhookDeliveryRepository deliveryRepo, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.endpointRepo = endpointRepo;
        this.deliveryRepo = deliveryRepo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transaction-events", groupId = "webhook-group")
    public void onTransactionEvent(TransactionEvent event) {
        log.info("Received transaction event for webhook processing: {}", event.getTransactionId());
        
        List<WebhookEndpoint> endpoints = endpointRepo.findByUserIdAndActiveTrue(event.getUserId());
        
        for (WebhookEndpoint endpoint : endpoints) {
            // In a real app we'd check if endpoint is subscribed to "transaction.completed" 
            deliverWebhook(endpoint, event);
        }
    }

    private void deliverWebhook(WebhookEndpoint endpoint, TransactionEvent event) {
        WebhookDelivery delivery = new WebhookDelivery();
        delivery.setTransactionId(event.getTransactionId());
        delivery.setUrl(endpoint.getUrl());
        delivery.setStatus("PENDING");
        delivery.setAttemptCount(0);
        delivery.setTimestamp(LocalDateTime.now());

        try {
            String payload = objectMapper.writeValueAsString(event);
            delivery.setPayload(payload);
            
            String signature = generateHmacSignature(payload, endpoint.getSecret());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Webhook-Signature", signature);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(endpoint.getUrl(), request, String.class);
            
            delivery.setResponseStatusCode(response.getStatusCode().value());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                delivery.setStatus("DELIVERED");
                delivery.setSuccess(true);
            } else {
                scheduleRetry(delivery);
            }
        } catch (Exception e) {
            log.error("Failed to deliver webhook to {}", endpoint.getUrl(), e);
            scheduleRetry(delivery);
        }
        
        deliveryRepo.save(delivery);
    }
    
    private void scheduleRetry(WebhookDelivery delivery) {
        delivery.setSuccess(false);
        delivery.setStatus("FAILED");
        delivery.setAttemptCount(delivery.getAttemptCount() + 1);
        if (delivery.getAttemptCount() < 5) {
            delivery.setNextRetryAt(LocalDateTime.now().plusMinutes((long) Math.pow(2, delivery.getAttemptCount())));
            delivery.setStatus("PENDING_RETRY");
        }
    }

    private String generateHmacSignature(String payload, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC signature", e);
        }
    }
}