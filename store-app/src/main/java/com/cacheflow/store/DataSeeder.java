package com.cacheflow.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/** Seeds the 100 canonical products, with a store-specific ±3–8% price variance. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final ProductRepository repo;
    private final long seed;
    private final int ending;

    public DataSeeder(ProductRepository repo, @Value("${store.price-seed}") long seed,
                      @Value("${store.price-ending}") int ending) {
        this.repo = repo; this.seed = seed; this.ending = ending;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        Random rnd = new Random(seed);
        for (ProductCatalog.Item item : ProductCatalog.ITEMS) {
            double pct = 0.03 + rnd.nextDouble() * 0.05;          // 3%..8%
            double factor = rnd.nextBoolean() ? 1 + pct : 1 - pct; // up or down
            long raw = Math.round(item.basePrice() * factor);
            long price = (raw / 100) * 100 - 100 + ending;          // A ends ...99, B ends ...49 -> never tie
            repo.save(new Product(item.sku(), item.category(), item.name(),
                    BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP)));
        }
    }
}
