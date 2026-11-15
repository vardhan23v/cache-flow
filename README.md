# Cache Flow

A price tracker built with Java and Spring Boot. Cache Flow scrapes two dummy stores with Jsoup, compares their prices, highlights the best deal, and keeps a price history in MySQL.

| App        | Port | Database        | What it does |
|------------|------|-----------------|--------------|
| Store A    | 8081 | `store_a_db`    | Dummy shop: 100 products, inline price editor |
| Store B    | 8082 | `store_b_db`    | Same codebase (`store-app`), profile `b` |
| Cache Flow | 8080 | `cache_flow_db` | Scraper, comparison, history charts |

## Requirements
- JDK 17+ (21 recommended) and Maven
- MySQL 8 running on `localhost:3306`. The databases are created automatically. `db/init.sql` is optional.

## Run
```bash
export DB_USER=root DB_PASS=yourpassword   # defaults: root / (empty)
./run.sh
```
If something else is using port 8080:
```bash
PORT=8090 CACHEFLOW_URL=http://localhost:8090 ./run.sh
```
(`CACHEFLOW_URL` tells the stores where to send their webhook.)

To stop everything: `pkill -f 'store-app-1.0.0.jar|cache-flow-1.0.0.jar'`

## How syncing works
1. **Scheduled scrape:** every 60 s (`scrape.interval-ms`), `ScraperService` loads `<store>/all` with Jsoup and reads
   `.product-item[data-sku]`, `.product-title`, and `.product-price[data-price]`.
2. **Instant sync:** when a store price is edited, the store POSTs `{store, sku}` to
   `/api/webhook/price-changed`, and Cache Flow re-scrapes that store right away. The **Sync / Refresh Prices Now** button scrapes both stores.
3. A row is added to `price_history` only when a price actually changes. The comparison page polls `/api/comparison` every 5 s and refreshes without a page reload.
4. On the first start, `HistorySeeder` backfills about 6 months of daily price history for each product and store. The backfill ends at the live price.

## Store API
```bash
curl -X PATCH localhost:8081/api/products/PHN-001/price -H 'Content-Type: application/json' -d '{"price":99999}'
```
Pages: `/category/{Phones|Tablets|Laptops|Earphones|Watches}` and `/all`.

## Cache Flow endpoints
`GET /` comparison · `GET /product/{sku}` chart · `POST /api/sync` · `GET /api/comparison` · `GET /api/history/{sku}`

## Reset data
```sql
DROP DATABASE store_a_db; DROP DATABASE store_b_db; DROP DATABASE cache_flow_db;
```
