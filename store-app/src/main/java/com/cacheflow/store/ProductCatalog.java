package com.cacheflow.store;

import java.util.ArrayList;
import java.util.List;

/** 100 canonical products (5 categories x 20). Base prices in INR. */
public final class ProductCatalog {
    public record Item(String sku, String category, String name, long basePrice) {}

    public static final List<String> CATEGORIES = List.of("Phones", "Tablets", "Laptops", "Earphones", "Watches");
    public static final List<Item> ITEMS = new ArrayList<>();

    private static void add(String prefix, String category, Object[][] rows) {
        for (int i = 0; i < rows.length; i++) {
            ITEMS.add(new Item(String.format("%s-%03d", prefix, i + 1), category,
                    (String) rows[i][0], ((Number) rows[i][1]).longValue()));
        }
    }

    static {
        add("PHN", "Phones", new Object[][]{
            {"Apple iPhone 15 Pro 128GB", 134900}, {"Apple iPhone 15 128GB", 79900},
            {"Apple iPhone 15 Pro Max 256GB", 159900}, {"Apple iPhone 14 128GB", 69900},
            {"Samsung Galaxy S24 Ultra 256GB", 129999}, {"Samsung Galaxy S24 128GB", 79999},
            {"Samsung Galaxy Z Fold5 256GB", 154999}, {"Samsung Galaxy Z Flip5 256GB", 99999},
            {"Samsung Galaxy A55 5G 128GB", 39999}, {"Google Pixel 8 Pro 128GB", 106999},
            {"Google Pixel 8 128GB", 75999}, {"Google Pixel 7a 128GB", 43999},
            {"OnePlus 12 256GB", 64999}, {"OnePlus Nord 3 5G 128GB", 33999},
            {"Xiaomi 14 512GB", 69999}, {"Nothing Phone (2) 256GB", 44999},
            {"Motorola Edge 50 Pro 256GB", 31999}, {"Vivo X100 Pro 512GB", 89999},
            {"OPPO Find N3 Flip 256GB", 94999}, {"Sony Xperia 1 V 256GB", 119999}});
        add("TAB", "Tablets", new Object[][]{
            {"Apple iPad Pro 11\" M4 256GB Wi-Fi", 99900}, {"Apple iPad Pro 13\" M4 256GB Wi-Fi", 129900},
            {"Apple iPad Air 11\" M2 128GB Wi-Fi", 59900}, {"Apple iPad 10th Gen 64GB Wi-Fi", 34900},
            {"Apple iPad mini 6th Gen 64GB Wi-Fi", 46900}, {"Samsung Galaxy Tab S9 128GB Wi-Fi", 72999},
            {"Samsung Galaxy Tab S9+ 256GB Wi-Fi", 90999}, {"Samsung Galaxy Tab S9 Ultra 256GB Wi-Fi", 108999},
            {"Samsung Galaxy Tab S9 FE 128GB Wi-Fi", 36999}, {"Samsung Galaxy Tab A9+ 64GB Wi-Fi", 18999},
            {"Google Pixel Tablet 128GB", 49999}, {"OnePlus Pad 128GB", 37999},
            {"OnePlus Pad Go 128GB", 19999}, {"Xiaomi Pad 6 128GB", 26999},
            {"Lenovo Tab P12 128GB", 29999}, {"Lenovo Tab M10 Plus 3rd Gen 128GB", 17999},
            {"Microsoft Surface Pro 9 i5 256GB", 109999}, {"Microsoft Surface Go 4 128GB", 62999},
            {"Amazon Fire HD 10 32GB", 14999}, {"Realme Pad 2 128GB", 19999}});
        add("LAP", "Laptops", new Object[][]{
            {"Apple MacBook Air 13\" M3 8GB/256GB", 114900}, {"Apple MacBook Air 15\" M3 8GB/256GB", 134900},
            {"Apple MacBook Pro 14\" M3 8GB/512GB", 169900}, {"Apple MacBook Pro 16\" M3 Pro 18GB/512GB", 249900},
            {"Dell XPS 13 9340 Core Ultra 7", 159990}, {"Dell XPS 15 9530 i7 RTX 4050", 219990},
            {"Dell Inspiron 15 3530 i5", 58990}, {"HP Spectre x360 14 Core Ultra 7", 164999},
            {"HP Pavilion 15 i5 13th Gen", 66999}, {"HP Victus 16 Ryzen 7 RTX 4060", 99999},
            {"Lenovo ThinkPad X1 Carbon Gen 11", 189999}, {"Lenovo Yoga Slim 7i Core Ultra 5", 99999},
            {"Lenovo Legion 5 Pro Ryzen 7 RTX 4060", 139999}, {"ASUS ROG Zephyrus G14 RTX 4060", 159990},
            {"ASUS Zenbook 14 OLED Core Ultra 7", 99990}, {"ASUS Vivobook 15 i5 12th Gen", 49990},
            {"Acer Swift Go 14 Core Ultra 5", 79999}, {"Acer Nitro V 15 i5 RTX 3050", 69999},
            {"MSI Katana 15 i7 RTX 4060", 119990}, {"Microsoft Surface Laptop 5 13.5\" i5", 107999}});
        add("EAR", "Earphones", new Object[][]{
            {"Sony WH-1000XM5", 29990}, {"Sony WF-1000XM5", 24990},
            {"Sony WH-CH720N", 9990}, {"Apple AirPods Pro (2nd Gen) USB-C", 24900},
            {"Apple AirPods (3rd Gen)", 19900}, {"Apple AirPods Max", 59900},
            {"Bose QuietComfort Ultra Headphones", 35900}, {"Bose QuietComfort Ultra Earbuds", 25900},
            {"Bose QuietComfort 45", 29900}, {"Samsung Galaxy Buds2 Pro", 17999},
            {"Samsung Galaxy Buds FE", 7999}, {"Google Pixel Buds Pro", 19990},
            {"Jabra Elite 10", 21999}, {"Sennheiser Momentum 4 Wireless", 34990},
            {"Sennheiser Momentum True Wireless 4", 24990}, {"JBL Tour One M2", 24999},
            {"JBL Tune 770NC", 7999}, {"Beats Studio Pro", 34900},
            {"Nothing Ear (2)", 9999}, {"OnePlus Buds 3", 5499}});
        add("WCH", "Watches", new Object[][]{
            {"Apple Watch Series 9 GPS 45mm", 44900}, {"Apple Watch Series 9 GPS 41mm", 41900},
            {"Apple Watch Ultra 2 49mm", 89900}, {"Apple Watch SE (2nd Gen) GPS 44mm", 32900},
            {"Samsung Galaxy Watch 6 44mm", 32999}, {"Samsung Galaxy Watch 6 Classic 47mm", 39999},
            {"Samsung Galaxy Watch 5 Pro 45mm", 44999}, {"Google Pixel Watch 2 41mm", 39999},
            {"Garmin Fenix 7 Pro Solar", 89990}, {"Garmin Forerunner 265", 47990},
            {"Garmin Venu 3", 49990}, {"Garmin Instinct 2 Solar", 38990},
            {"Fitbit Sense 2", 24999}, {"Fitbit Versa 4", 19999},
            {"Amazfit GTR 4", 16999}, {"Amazfit Balance", 21999},
            {"Huawei Watch GT 4 46mm", 21999}, {"OnePlus Watch 2", 24999},
            {"Fossil Gen 6 Smartwatch 44mm", 23995}, {"Noise ColorFit Pro 5", 3999}});
    }

    private ProductCatalog() {}
}
