package com.wallet.controller;

import com.wallet.dto.WebhookRegistrationRequest;
import com.wallet.model.WebhookEndpoint;
import com.wallet.repository.WebhookEndpointRepository;
import com.wallet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookEndpointRepository endpointRepo;
    private final UserRepository userRepo;

    public WebhookController(WebhookEndpointRepository endpointRepo, UserRepository userRepo) {
        this.endpointRepo = endpointRepo;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<WebhookEndpoint> register(
            @RequestBody WebhookRegistrationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);

        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUserId(userId);
        endpoint.setUrl(req.getUrl());
        endpoint.setSecret(UUID.randomUUID().toString());
        endpoint.setActive(true);
        endpoint.setEventTypes(req.getEventTypes());

        return ResponseEntity.ok(endpointRepo.save(endpoint));
    }

    @GetMapping
    public ResponseEntity<List<WebhookEndpoint>> list(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(
            endpointRepo.findByUserIdAndActiveTrue(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        endpointRepo.findById(id).ifPresent(ep -> {
            ep.setActive(false);
            endpointRepo.save(ep);
        });
        return ResponseEntity.ok().build();
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepo.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }
}