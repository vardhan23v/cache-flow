package com.cacheflow.app;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Latest scraped price of a SKU at a store. */
@Entity
@Table(name = "store_prices", uniqueConstraints = @UniqueConstraint(columnNames = {"sku", "store"}))
public class StorePrice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 20) private String sku;
    @Column(nullable = false, length = 2) private String store;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    private LocalDateTime scrapedAt;

    protected StorePrice() {}
    public StorePrice(String sku, String store) { this.sku = sku; this.store = store; }
    public String getSku() { return sku; }
    public String getStore() { return store; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getScrapedAt() { return scrapedAt; }
    public void setPrice(BigDecimal price) { this.price = price; this.scrapedAt = LocalDateTime.now(); }
}
