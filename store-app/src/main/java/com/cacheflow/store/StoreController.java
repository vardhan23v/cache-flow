package com.cacheflow.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class StoreController {
    private final ProductRepository repo;
    private final WebhookNotifier notifier;
    private final String storeName;

    public StoreController(ProductRepository repo, WebhookNotifier notifier, @Value("${store.name}") String storeName) {
        this.repo = repo; this.notifier = notifier; this.storeName = storeName;
    }

    @ModelAttribute
    void common(Model model) {
        model.addAttribute("storeName", storeName);
        model.addAttribute("categories", ProductCatalog.CATEGORIES);
    }

    @GetMapping("/")
    public String home() { return "redirect:/category/Phones"; }

    @GetMapping("/category/{category}")
    public String category(@PathVariable String category, Model model) {
        model.addAttribute("title", category);
        model.addAttribute("current", category);
        model.addAttribute("products", repo.findByCategoryOrderBySkuAsc(category));
        return "list";
    }

    /** Every product on one page – the page Cache Flow scrapes. */
    @GetMapping("/all")
    public String all(Model model) {
        model.addAttribute("title", "All Products");
        model.addAttribute("current", "all");
        model.addAttribute("products", repo.findAllByOrderBySkuAsc());
        return "list";
    }

    /** Form post from the inline price editor. */
    @PostMapping("/products/{sku}/price")
    public String updateFromForm(@PathVariable String sku, @RequestParam BigDecimal price,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        update(sku, price);
        return "redirect:" + (referer != null ? referer : "/");
    }

    /** REST endpoint: PATCH or POST {"price": 12345.00} */
    @RequestMapping(value = "/api/products/{sku}/price", method = {RequestMethod.PATCH, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateApi(@PathVariable String sku, @RequestBody Map<String, BigDecimal> body) {
        Product p = update(sku, body.get("price"));
        return ResponseEntity.ok(Map.of("sku", p.getSku(), "price", p.getPrice(), "updatedAt", p.getUpdatedAt().toString()));
    }

    protected Product update(String sku, BigDecimal price) {
        if (price == null || price.signum() <= 0) throw new ResponseStatusException(BAD_REQUEST, "price must be > 0");
        Product p = repo.findBySku(sku).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unknown SKU " + sku));
        p.setPrice(price.setScale(2, RoundingMode.HALF_UP));
        repo.save(p);
        notifier.priceChanged(sku);
        return p;
    }
}
