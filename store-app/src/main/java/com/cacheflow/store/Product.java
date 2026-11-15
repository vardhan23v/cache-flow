package com.cacheflow.store;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 20)
    private String sku;
    @Column(nullable = false, length = 30)
    private String category;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    private LocalDateTime updatedAt;

    protected Product() {}
    public Product(String sku, String category, String name, BigDecimal price) {
        this.sku = sku; this.category = category; this.name = name; this.price = price;
        this.updatedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setPrice(BigDecimal price) { this.price = price; this.updatedAt = LocalDateTime.now(); }
}
