package com.cacheflow.app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

interface TrackedProductRepository extends JpaRepository<TrackedProduct, String> {
    List<TrackedProduct> findAllByOrderBySkuAsc();
}

interface StorePriceRepository extends JpaRepository<StorePrice, Long> {
    Optional<StorePrice> findBySkuAndStore(String sku, String store);
}

interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findBySkuOrderByRecordedAtAsc(String sku);
}
