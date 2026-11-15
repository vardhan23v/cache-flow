package com.cacheflow.app;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class TrackedProduct {
    @Id @Column(length = 20) private String sku;
    @Column(nullable = false, length = 30) private String category;
    @Column(nullable = false) private String name;

    protected TrackedProduct() {}
    public TrackedProduct(String sku, String category, String name) { this.sku = sku; this.category = category; this.name = name; }
    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
}
