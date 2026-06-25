package com.wallet.scheduler;

import com.wallet.model.FxRate;
import com.wallet.repository.FxRateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class FxRateScheduler {

    private final FxRateRepository fxRateRepository;
    private final Random random = new Random();

    public FxRateScheduler(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void updateRates() {
        List<FxRate> rates = fxRateRepository.findAll();
        for (FxRate rate : rates) {
            // Random fluctuation between -0.2% and +0.2%
            double fluctuation = 1.0 + ((random.nextDouble() - 0.5) * 0.004);
            BigDecimal newRate = rate.getRate().multiply(BigDecimal.valueOf(fluctuation))
                                     .setScale(4, RoundingMode.HALF_UP);
            
            rate.setRate(newRate);
            rate.setLastUpdated(LocalDateTime.now());
            fxRateRepository.save(rate);
        }
    }
}