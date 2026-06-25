package com.wallet.repository;

import com.wallet.model.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FxRateRepository extends JpaRepository<FxRate, Long> {

    // Gets the most recently fetched rate for a currency pair
    // Used as DB fallback when Redis is unavailable
    @Query("SELECT f FROM FxRate f " +
           "WHERE f.sourceCurrency = :base AND f.targetCurrency = :target " +
           "ORDER BY f.lastUpdated DESC")
    Optional<FxRate> findLatestByPair(@Param("base") String base,
                                       @Param("target") String target);
}