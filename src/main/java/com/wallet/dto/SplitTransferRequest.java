package com.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SplitTransferRequest {
    private String targetCurrency;
    private BigDecimal targetAmount;
    private String idempotencyKey;
}