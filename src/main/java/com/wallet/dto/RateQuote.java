package com.wallet.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateQuote {
    private String provider;
    private BigDecimal rate;
    private BigDecimal savings;
}