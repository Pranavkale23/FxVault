package com.wallet.dto;

import lombok.*;
import java.util.List;

public class WebhookRegistrationRequest {
    private String url;
    private List<String> eventTypes;

    public WebhookRegistrationRequest() {}

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public List<String> getEventTypes() { return eventTypes; }
    public void setEventTypes(List<String> eventTypes) { this.eventTypes = eventTypes; }
}