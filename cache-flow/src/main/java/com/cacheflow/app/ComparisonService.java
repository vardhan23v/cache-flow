package com.cacheflow.app;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ComparisonService {
    public record Row(String sku, String category, String name, BigDecimal storeA, BigDecimal storeB,
                      BigDecimal difference, BigDecimal differencePct, String best) {}

    private final TrackedProductRepository products;
    private final StorePriceRepository prices;

    public ComparisonService(TrackedProductRepository products, StorePriceRepository prices) {
        this.products = products; this.prices = prices;
    }

    public List<Row> rows() {
        Map<String, Map<String, BigDecimal>> bySku = new HashMap<>();
        for (StorePrice sp : prices.findAll()) bySku.computeIfAbsent(sp.getSku(), k -> new HashMap<>()).put(sp.getStore(), sp.getPrice());
        List<Row> out = new ArrayList<>();
        for (TrackedProduct p : products.findAllByOrderBySkuAsc()) {
            Map<String, BigDecimal> m = bySku.getOrDefault(p.getSku(), Map.of());
            out.add(row(p, m.get("A"), m.get("B")));
        }
        return out;
    }

    public Optional<Row> row(String sku) {
        return rows().stream().filter(r -> r.sku().equals(sku)).findFirst();
    }

    private Row row(TrackedProduct p, BigDecimal a, BigDecimal b) {
        BigDecimal diff = null, pct = null;
        String best;
        if (a == null && b == null) best = "N/A";
        else if (b == null) best = "Store A";
        else if (a == null) best = "Store B";
        else {
            int c = a.compareTo(b);
            best = c < 0 ? "Store A" : c > 0 ? "Store B" : "Same Price";
            diff = a.subtract(b).abs();
            pct = diff.multiply(BigDecimal.valueOf(100)).divide(a.max(b), 1, RoundingMode.HALF_UP);
        }
        return new Row(p.getSku(), p.getCategory(), p.getName(), a, b, diff, pct, best);
    }
}
