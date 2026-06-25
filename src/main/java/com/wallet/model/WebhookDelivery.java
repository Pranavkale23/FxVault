package com.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "webhook_deliveries")
public class WebhookDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long transactionId;
    private String eventId;
    private String url;
    private String payload;
    private int responseStatusCode;
    private boolean success;
    private String status;
    private int attemptCount;
    private LocalDateTime nextRetryAt;
    private LocalDateTime timestamp;

    public WebhookDelivery() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public int getResponseStatusCode() { return responseStatusCode; }
    public void setResponseStatusCode(int responseStatusCode) { this.responseStatusCode = responseStatusCode; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}