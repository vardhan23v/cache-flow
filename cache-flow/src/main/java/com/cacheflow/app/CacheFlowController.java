package com.cacheflow.app;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class CacheFlowController {
    private static final List<String> CATEGORIES = List.of("Phones", "Tablets", "Laptops", "Earphones", "Watches");
    private final ComparisonService comparison;
    private final ScraperService scraper;
    private final PriceHistoryRepository history;

    public CacheFlowController(ComparisonService comparison, ScraperService scraper, PriceHistoryRepository history) {
        this.comparison = comparison; this.scraper = scraper; this.history = history;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rows", comparison.rows());
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("lastSync", scraper.getLastSync());
        return "index";
    }

    @GetMapping("/product/{sku}")
    public String product(@PathVariable String sku, Model model) {
        model.addAttribute("row", comparison.row(sku).orElseThrow(() -> new ResponseStatusException(NOT_FOUND)));
        return "product";
    }

    /** "Sync / Refresh Prices Now" button. */
    @PostMapping("/sync")
    public String sync() { scraper.scrapeAll(); return "redirect:/"; }

    // ---------- JSON API ----------

    @PostMapping("/api/sync")
    @ResponseBody
    public Map<String, Object> apiSync() {
        Map<String, Integer> changed = scraper.scrapeAll();
        return Map.of("changed", changed, "lastSync", String.valueOf(scraper.getLastSync()));
    }

    /** Called by Store A / Store B when a price is edited. */
    @PostMapping("/api/webhook/price-changed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> webhook(@RequestBody Map<String, String> body) {
        String store = body.getOrDefault("store", "").toUpperCase();
        int changed = scraper.scrapeStore(store);
        return ResponseEntity.ok(Map.of("store", store, "changed", changed));
    }

    @GetMapping("/api/comparison")
    @ResponseBody
    public Map<String, Object> apiComparison() {
        return Map.of("lastSync", String.valueOf(scraper.getLastSync()), "rows", comparison.rows());
    }

    @GetMapping("/api/history/{sku}")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> apiHistory(@PathVariable String sku) {
        Map<String, List<Map<String, Object>>> out = new LinkedHashMap<>();
        out.put("A", new ArrayList<>()); out.put("B", new ArrayList<>());
        for (PriceHistory h : history.findBySkuOrderByRecordedAtAsc(sku)) {
            out.computeIfAbsent(h.getStore(), k -> new ArrayList<>())
               .add(Map.of("t", h.getRecordedAt().toString(), "price", h.getPrice()));
        }
        return out;
    }
}
