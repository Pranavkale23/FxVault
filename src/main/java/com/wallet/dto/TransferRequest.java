package com.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long sourceWalletId;
    private Long targetWalletId;
    private BigDecimal amount;
    private String idempotencyKey;
}