package com.wallet.service;

import com.wallet.dto.RateQuote;
import com.wallet.repository.FxRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class SmartFxRouter {

    private final RedisTemplate<String, String> redisTemplate;
    private final FxRateRepository fxRateRepo;

    public SmartFxRouter(RedisTemplate<String, String> redisTemplate, FxRateRepository fxRateRepo) {
        this.redisTemplate = redisTemplate;
        this.fxRateRepo = fxRateRepo;
    }

    public RateQuote getBestRate(String from, String to) {

        // Same currency — no conversion needed
        if (from.equals(to)) {
            return new RateQuote("SAME_CURRENCY",
                BigDecimal.ONE, BigDecimal.ZERO);
        }

        List<RateQuote> quotes = fetchAllQuotes(from, to);

        if (quotes.isEmpty()) {
            throw new RuntimeException(
                "No FX rates available for " + from + " → " + to
            );
        }

        BigDecimal worstRate = quotes.stream()
            .map(RateQuote::getRate)
            .min(Comparator.naturalOrder())
            .orElseThrow();

        return quotes.stream()
            .max(Comparator.comparing(RateQuote::getRate))
            .map(best -> {
                BigDecimal savings = best.getRate().subtract(worstRate);
                return new RateQuote(
                    best.getProvider(), best.getRate(), savings);
            })
            .orElseThrow();
    }

    private List<RateQuote> fetchAllQuotes(String from, String to) {
        List<RateQuote> quotes = new ArrayList<>();

        // Source 1: Redis cache (populated by FxRateScheduler)
        String cached = redisTemplate.opsForValue()
            .get("fx:" + from + ":" + to);
        if (cached != null) {
            quotes.add(new RateQuote(
                "OpenExchangeRates",
                new BigDecimal(cached),
                BigDecimal.ZERO));
        }

        // Source 2: Database fallback
        fxRateRepo.findLatestByPair(from, to).ifPresent(r ->
            quotes.add(new RateQuote(
                "InternalDB", r.getRate(), BigDecimal.ZERO))
        );

        // Source 3: Simulated bank rate (always slightly worse)
        if (!quotes.isEmpty()) {
            BigDecimal bankRate = quotes.get(0).getRate()
                .multiply(new BigDecimal("0.98"));
            quotes.add(new RateQuote(
                "BankRate", bankRate, BigDecimal.ZERO));
        }

        log.debug("FX quotes for {}→{}: {}", from, to, quotes);
        return quotes;
    }
}