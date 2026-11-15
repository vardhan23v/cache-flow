package com.cacheflow.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/** Tells Cache Flow that a price changed so it re-scrapes immediately. */
@Component
public class WebhookNotifier {
    private static final Logger log = LoggerFactory.getLogger(WebhookNotifier.class);
    private final RestClient client = RestClient.create();
    private final String webhookUrl;
    private final String storeCode;

    public WebhookNotifier(@Value("${cacheflow.webhook-url}") String webhookUrl,
                           @Value("${store.code}") String storeCode) {
        this.webhookUrl = webhookUrl; this.storeCode = storeCode;
    }

    @Async
    public void priceChanged(String sku) {
        try {
            client.post().uri(webhookUrl).body(Map.of("store", storeCode, "sku", sku)).retrieve().toBodilessEntity();
            log.info("Notified Cache Flow of price change: {} {}", storeCode, sku);
        } catch (Exception e) {
            log.warn("Cache Flow webhook failed ({}); scheduled scrape will catch up.", e.getMessage());
        }
    }
}
