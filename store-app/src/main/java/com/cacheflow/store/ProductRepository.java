package com.cacheflow.store;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByCategoryOrderBySkuAsc(String category);
    List<Product> findAllByOrderBySkuAsc();
}
