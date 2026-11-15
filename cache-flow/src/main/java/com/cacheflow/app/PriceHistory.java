package com.cacheflow.app;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history", indexes = @Index(columnList = "sku,store,recordedAt"))
public class PriceHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 20) private String sku;
    @Column(nullable = false, length = 2) private String store;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private LocalDateTime recordedAt;

    protected PriceHistory() {}
    public PriceHistory(String sku, String store, BigDecimal price, LocalDateTime recordedAt) {
        this.sku = sku; this.store = store; this.price = price; this.recordedAt = recordedAt;
    }
    public String getSku() { return sku; }
    public String getStore() { return store; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
