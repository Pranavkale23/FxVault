package com.wallet.dto;

import com.wallet.model.Transaction;
import lombok.*;
import java.math.BigDecimal;

@Getter
public class TransactionResponse {
    private Long id;
    private String idempotencyKey;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
    private BigDecimal fxRate;
    private String fxProvider;
    private BigDecimal savings;
    private String status;

    public TransactionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(String sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }
    public BigDecimal getFxRate() { return fxRate; }
    public void setFxRate(BigDecimal fxRate) { this.fxRate = fxRate; }
    public String getFxProvider() { return fxProvider; }
    public void setFxProvider(String fxProvider) { this.fxProvider = fxProvider; }
    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static TransactionResponse from(Transaction tx, BigDecimal savings) {
        TransactionResponse response = new TransactionResponse();
        response.setId(tx.getId());
        response.setIdempotencyKey(tx.getIdempotencyKey());
        response.setSourceCurrency(tx.getSourceCurrency());
        response.setTargetCurrency(tx.getTargetCurrency());
        response.setAmount(tx.getAmount());
        response.setConvertedAmount(tx.getConvertedAmount());
        response.setFxRate(tx.getFxRate());
        response.setFxProvider(tx.getFxProvider());
        response.setSavings(savings);
        response.setStatus(tx.getStatus());
        return response;
    }
}