package com.cacheflow.app;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * On startup: scrape both stores, and if price_history has no backfill yet,
 * generate ~6 months of daily prices per SKU/store that end at today's live price.
 */
@Component
public class HistorySeeder {
    private static final int DAYS = 182;
    private final ScraperService scraper;
    private final StorePriceRepository prices;
    private final PriceHistoryRepository history;

    public HistorySeeder(ScraperService scraper, StorePriceRepository prices, PriceHistoryRepository history) {
        this.scraper = scraper; this.prices = prices; this.history = history;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        boolean needsBackfill = history.count() == 0;
        scraper.scrapeAll();
        if (!needsBackfill) return;
        LocalDateTime today = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        List<PriceHistory> batch = new ArrayList<>();
        for (StorePrice sp : prices.findAll()) {
            Random rnd = new Random((sp.getSku() + sp.getStore()).hashCode());
            double current = sp.getPrice().doubleValue();
            // Walk backwards from the current price so the series ends where the live data starts.
            double p = current;
            double[] series = new double[DAYS];
            for (int d = DAYS - 1; d >= 0; d--) {
                series[d] = p;
                if (rnd.nextDouble() < 0.12) {                       // occasional price move
                    p *= 1 + (rnd.nextGaussian() * 0.015 + 0.002);    // older prices tend to be slightly higher
                }
                p = Math.max(current * 0.8, Math.min(current * 1.2, p));
            }
            // Short sale events: dip 8–15% for a few days.
            for (int s = 0; s < 2; s++) {
                int start = rnd.nextInt(DAYS - 20), len = 3 + rnd.nextInt(5);
                double cut = 0.85 + rnd.nextDouble() * 0.07;
                for (int d = start; d < start + len; d++) series[d] *= cut;
            }
            Double last = null;
            for (int d = 0; d < DAYS - 1; d++) {                     // last day is the real scraped row
                double rounded = Math.round(series[d] / 100.0) * 100 - 1;
                if (last != null && last == rounded) continue;       // store only changes
                last = rounded;
                batch.add(new PriceHistory(sp.getSku(), sp.getStore(),
                        BigDecimal.valueOf(rounded).setScale(2, RoundingMode.HALF_UP),
                        today.minusDays(DAYS - 1 - d)));
            }
        }
        history.saveAll(batch);
    }
}
