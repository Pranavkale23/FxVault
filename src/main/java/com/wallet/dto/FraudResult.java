package com.wallet.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudResult {
    private int score;
    private String decision;
    private List<String> reasons;
}