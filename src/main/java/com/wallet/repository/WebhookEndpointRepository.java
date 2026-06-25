package com.wallet.repository;

import com.wallet.model.WebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {

    List<WebhookEndpoint> findByUserIdAndActiveTrue(Long userId);

    List<WebhookEndpoint> findByUserId(Long userId);
}