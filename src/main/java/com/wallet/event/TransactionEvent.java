package com.wallet.event;

import com.wallet.model.Transaction;
import lombok.*;
import java.math.BigDecimal;

public class TransactionEvent {
    private Long transactionId;
    private Long userId;
    private String userEmail;
    private BigDecimal amount;
    private String sourceCurrency;
    private BigDecimal convertedAmount;
    private String targetCurrency;
    private BigDecimal fxRate;
    private String fxProvider;
    private BigDecimal savings;
    private String status;

    public TransactionEvent() {}

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(String sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }
    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }
    public BigDecimal getFxRate() { return fxRate; }
    public void setFxRate(BigDecimal fxRate) { this.fxRate = fxRate; }
    public String getFxProvider() { return fxProvider; }
    public void setFxProvider(String fxProvider) { this.fxProvider = fxProvider; }
    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static TransactionEvent from(Transaction tx, Long userId, String email, BigDecimal savings) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(tx.getId());
        event.setUserId(userId);
        event.setUserEmail(email);
        event.setAmount(tx.getAmount());
        event.setSourceCurrency(tx.getSourceCurrency());
        event.setConvertedAmount(tx.getConvertedAmount());
        event.setTargetCurrency(tx.getTargetCurrency());
        event.setFxRate(tx.getFxRate());
        event.setFxProvider(tx.getFxProvider());
        event.setSavings(savings);
        event.setStatus(tx.getStatus());
        return event;
    }
}