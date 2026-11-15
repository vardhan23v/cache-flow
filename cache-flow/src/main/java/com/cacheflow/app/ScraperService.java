package com.cacheflow.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Jsoup scraper for Store A / Store B plus the scheduled poller. */
@Service
public class ScraperService {
    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private final TrackedProductRepository products;
    private final StorePriceRepository prices;
    private final PriceHistoryRepository history;
    private final Map<String, String> stores = new LinkedHashMap<>();
    private volatile LocalDateTime lastSync;

    public ScraperService(TrackedProductRepository products, StorePriceRepository prices, PriceHistoryRepository history,
                          @Value("${stores.a-url}") String aUrl, @Value("${stores.b-url}") String bUrl) {
        this.products = products; this.prices = prices; this.history = history;
        stores.put("A", aUrl); stores.put("B", bUrl);
    }

    public LocalDateTime getLastSync() { return lastSync; }

    @Scheduled(initialDelayString = "${scrape.interval-ms:60000}", fixedDelayString = "${scrape.interval-ms:60000}")
    public void scheduledScrape() { scrapeAll(); }

    public Map<String, Integer> scrapeAll() {
        Map<String, Integer> changed = new LinkedHashMap<>();
        stores.keySet().forEach(code -> changed.put(code, scrapeStore(code)));
        return changed;
    }

    /** Scrapes one store; returns number of prices that changed. Synchronized so webhook + schedule don't race. */
    public synchronized int scrapeStore(String code) {
        String base = stores.get(code);
        if (base == null) throw new IllegalArgumentException("Unknown store " + code);
        try {
            Document doc = Jsoup.connect(base + "/all").timeout(10_000).get();
            int changed = 0;
            LocalDateTime now = LocalDateTime.now();
            for (Element item : doc.select(".product-item")) {
                String sku = item.attr("data-sku");
                String name = item.selectFirst(".product-title").text();
                Element priceEl = item.selectFirst(".product-price");
                if (sku.isBlank() || priceEl == null) continue;
                BigDecimal price = new BigDecimal(priceEl.attr("data-price"));

                TrackedProduct p = products.findById(sku)
                        .orElseGet(() -> new TrackedProduct(sku, item.attr("data-category"), name));
                p.setName(name);
                if (!item.attr("data-category").isBlank()) p.setCategory(item.attr("data-category"));
                products.save(p);

                StorePrice sp = prices.findBySkuAndStore(sku, code).orElseGet(() -> new StorePrice(sku, code));
                boolean isChange = sp.getPrice() == null || sp.getPrice().compareTo(price) != 0;
                sp.setPrice(price);
                prices.save(sp);
                if (isChange) {
                    history.save(new PriceHistory(sku, code, price, now));
                    changed++;
                }
            }
            lastSync = now;
            log.info("Scraped Store {} – {} items, {} price changes", code, doc.select(".product-item").size(), changed);
            return changed;
        } catch (Exception e) {
            log.warn("Scrape of Store {} ({}) failed: {}", code, base, e.getMessage());
            return -1;
        }
    }
}
