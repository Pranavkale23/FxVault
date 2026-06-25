package com.wallet.repository;

import com.wallet.model.WebhookDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, Long> {

    // Scheduler uses this every 30 seconds to pick up
    // deliveries that are due for retry
    List<WebhookDelivery> findByStatusAndNextRetryAtBefore(
            String status, LocalDateTime time);

    List<WebhookDelivery> findByTransactionId(Long transactionId);
}