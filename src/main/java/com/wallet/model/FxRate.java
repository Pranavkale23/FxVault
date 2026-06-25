package com.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fx_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FxRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sourceCurrency;
    private String targetCurrency;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal rate;
    private LocalDateTime lastUpdated;
}