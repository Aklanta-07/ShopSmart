# ShopSmart

> **Inventory and Billing System for Small Shops** — Built with Spring Boot 4, Java 21, and Oracle Database.

A modern, secure REST API for managing products, categories, inventory, and authentication — designed for small retail businesses. Includes a **future-ready AI integration roadmap** for intelligent inventory management.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.1.0 |
| **Build** | Maven (WAR packaging) |
| **Database** | Oracle 19c+ (JDBC Thin) |
| **ORM** | Spring Data JPA / Hibernate 6 |
| **Security** | Spring Security 6 + JWT (JJWT 0.11.5) |
| **API Docs** | SpringDoc OpenAPI 2.3 (Swagger UI) |
| **Validation** | Jakarta Bean Validation |
| **Dev Tools** | Spring Boot DevTools, Lombok |
| **Testing** | JUnit 5, Spring Boot Test, Security Test |

---

## 📁 Project Structure

```
src/main/java/com/shopsmart
├── ShopSmartApplication.java      # Entry point
├── ServletInitializer.java        # WAR deployment support
├── config/
│   └── SecurityConfig.java        # Security filter chain, CORS, JWT config
├── controller/
│   └── AuthController.java        # Auth endpoints (register, login)
├── dto/
│   ├── request/                   # Request DTOs
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CategoryRequest.java
│   │   ├── ProductRequest.java
│   │   ├── InventoryRequest.java
│   │   ├── StockAdjustmentRequest.java
│   │   └── AdjustmentType.java
│   └── response/                  # Response DTOs
│       ├── AuthResponse.java
│       ├── ErrorResponse.java
│       ├── CategoryResponse.java
│       ├── ProductResponse.java
│       ├── InventoryResponse.java
│       ├── InventorySummary.java
│       ├── InventorySummaryResponse.java
│       └── LowStockAlertResponse.java
├── entity/
│   ├── User.java                  # User + Role (auth)
│   ├── Role.java
│   ├── Category.java              # Product categorization
│   ├── Product.java               # Core product info (price, barcode, tax, unit)
│   ├── ProductUnit.java           # Unit of measure (kg, pcs, liter, etc.)
│   └── Inventory.java             # Stock levels, low-stock threshold
├── repository/
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   ├── ProductRepository.java
│   └── InventoryRepository.java
├── security/
│   ├── JwtUtil.java               # Token generation/validation
│   ├── JwtAuthFilter.java         # JWT filter for requests
│   └── CustomUserDetailsService.java
├── service/
│   ├── AuthService.java
│   └── AuthServiceImpl.java
└── exception/
    ├── EmailAlreadyExistsException.java
    ├── InvalidCredentialsException.java
    ├── CategoryNotFoundException.java
    └── GlobalExceptionHandler.java  # Unified error responses
```

---

## 🔐 Authentication & Authorization

- **JWT-based stateless auth** (HS256, configurable expiry via `app.jwt.expiration`)
- **Roles**: `ADMIN`, `STAFF` (extensible via `Role` entity)
- **Endpoints secured** via `JwtAuthFilter` → `SecurityConfig`
- **Password hashing**: BCrypt (via Spring Security)

### Auth Endpoints

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| `POST` | `/api/auth/register` | Register new user | Public |
| `POST` | `/api/auth/login` | Login, return JWT | Public |

> **Swagger UI**: `http://localhost:8080/shopsmart/swagger-ui.html`  
> **OpenAPI Spec**: `http://localhost:8080/shopsmart/v3/api-docs`

---

## ⚙️ Configuration

Key properties in `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=ShopSmart
server.port=8080
server.servlet.context-path=/shopsmart

# Oracle Database
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:ORCL
spring.datasource.username=c##shopsmart_db
spring.datasource.password=shopsmart
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT (set via env vars in production)
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION:86400000}  # 24h default
```

**Environment variables required for production:**
```bash
export JWT_SECRET="your-256-bit-base64-secret"
export JWT_EXPIRATION=86400000
```

---

## 🏃 Getting Started

### Prerequisites
- **JDK 21+** (tested on Eclipse Temurin 21)
- **Maven 3.9+**
- **Oracle Database 19c+** (or XE / Free tier)
- (Optional) Docker for containerized deployment

### Local Development

```bash
# 1. Clone & enter project
git clone <your-repo-url>
cd ShopSmart

# 2. Configure Oracle connection in application.properties
#    (or set SPRING_DATASOURCE_URL/USERNAME/PASSWORD env vars)

# 3. Run
./mvnw spring-boot:run

# 4. Verify
curl http://localhost:8080/shopsmart/actuator/health
open http://localhost:8080/shopsmart/swagger-ui.html
```

### Build & Deploy (WAR)

```bash
# Package as WAR for Tomcat/Jetty
./mvnw clean package -DskipTests

# Output: target/shopmart-0.0.1-SNAPSHOT.war
# Deploy to any Servlet 6.0+ container (Tomcat 10.1+, Jetty 12+)
```

### Run Tests

```bash
./mvnw test
```

---

## 📦 Core Domain Models

### Product
- `name`, `description`, `barcode` (unique), `price`, `taxRate`
- `unit` → `ProductUnit` (kg, pcs, liter, box, etc.)
- `category` → `Category` (hierarchical support planned)
- `inventory` → `Inventory` (OneToOne)

### Inventory
- `quantityOnHand`, `reservedQuantity`, `availableQuantity` (derived)
- `reorderLevel`, `reorderQuantity` — triggers low-stock alerts
- `lastRestockedAt`, `lastAdjustedAt` — audit trail

### Stock Adjustments
```java
enum AdjustmentType {
    PURCHASE,      // Stock in from supplier
    SALE,          // Stock out to customer
    RETURN,        // Customer return
    DAMAGE,        // Write-off
    TRANSFER_IN,   // Inter-store transfer
    TRANSFER_OUT,
    ADJUSTMENT     // Manual correction
}
```

---

## 🤖 Future AI Integration Roadmap

This project is architected for **progressive AI enhancement**. Below is a phased roadmap — each phase delivers standalone value.

---

### Phase 1: Intelligence Layer (v2.0 — Q1–Q2 2026)
*Low effort, high impact — runs as background jobs / scheduled tasks*

| Feature | Description | Tech Approach |
|---------|-------------|---------------|
| **Demand Forecasting** | Predict 30/60/90-day sales per product using historical `Inventory` adjustments | Prophet / ARIMA via Python microservice (Spring Cloud Stream → Kafka) or Spring AI + ONNX |
| **Smart Reorder Suggestions** | Auto-calculate `reorderLevel`/`reorderQuantity` per SKU based on velocity & lead time | Rule engine (Drools) + ML model; expose via `/api/ai/reorder-suggestions` |
| **Low-Stock Anomaly Detection** | Flag unusual stock drops (theft, data entry errors) | Isolation Forest on adjustment deltas; alert via email/Slack |
| **Sales Velocity Dashboard** | Real-time "units/day" per product/category | Materialized view + Actuator metrics → Grafana |

**Integration Points:**
- New `AiInsightService` interface → `AiInsightServiceImpl` (calls Python/ONNX)
- `InventoryRepository` adds `findAdjustmentHistory(productId, days)`
- Scheduled `@Scheduled(cron = "0 0 2 * * ?")` nightly retraining

---

### Phase 2: Natural Language Interface (v2.5 — Q3 2026)
*Conversational access for non-technical shop owners*

| Feature | Description | Tech Approach |
|---------|-------------|---------------|
| **NL-to-SQL Query Layer** | "Show me products under 50 units in Beverages" → executes safe `SELECT` | Spring AI + `Text2SQL` (LangChain4j / custom prompt) with schema guardrails |
| **Voice/Chat Bot** | Telegram/WhatsApp bot for stock checks, reorder triggers | Spring AI `ChatClient` + Twilio / Telegram Bot API |
| **Auto-Categorization** | "New item: Alphonso Mangoes, 1kg, ₹120" → suggests `Category` + `ProductUnit` | Fine-tuned embedding model (sentence-transformers) on existing catalog |

**New Endpoints:**
```
POST   /api/ai/query          # Natural language → structured result
POST   /api/ai/categorize     # Product text → suggested category/unit
GET    /api/ai/insights/{productId}  # Forecast + reorder + anomaly flags
```

---

### Phase 3: Autonomous Operations (v3.0 — 2027+)
*Closed-loop automation with human-in-the-loop approval*

| Feature | Description |
|---------|-------------|
| **Auto-PO Generation** | Draft Purchase Orders when forecast < safety stock; email supplier / integrate with vendor API |
| **Dynamic Pricing** | Adjust `Product.price` based on demand elasticity, competitor data, expiry dates |
| **Shelf-Life Optimization** | For perishables: FIFO enforcement, expiry alerts, markdown automation |
| **Multi-Store Balancing** | Transfer recommendations across locations based on regional demand |

**Architecture Evolution:**
- Event-driven: `InventoryAdjustedEvent` → Kafka → AI workers
- Saga pattern for multi-step PO creation
- Human approval via `/api/ai/approvals/{id}/approve|reject`

---

### Suggested AI Tech Stack Additions

| Need | Recommendation | Why |
|------|----------------|-----|
| **LLM Orchestration** | **Spring AI** (GA 2024) | Native Spring integration, portable across OpenAI, Ollama, Vertex, Bedrock |
| **Local Models** | **Ollama** + `llama3.1:8b` / `mistral:7b` | Zero-cost inference, data stays on-prem |
| **Embeddings** | `bge-m3` or `nomic-embed-text` via Ollama | Multilingual, fast, good for product search |
| **ML Serving** | **BentoML** / **FastAPI** + **ONNX Runtime** | Decouples Python ML from Java backend |
| **Vector Search** | **PGVector** (if migrating to Postgres) or **Oracle AI Vector Search** | Semantic product search, RAG for chatbot |
| **Observability** | **Langfuse** / **LangSmith** | Trace LLM calls, eval prompt quality |

---

### Data Readiness Checklist (Do Now)

> Prepare your data today so Phase 1 requires minimal migration.

- [ ] **Enrich `Inventory` adjustments** — ensure every stock change writes an `AdjustmentType` + timestamp + userId
- [ ] **Add `salesPrice` vs `costPrice`** to `Product` for margin analysis
- [ ] **Capture `supplierId` + `leadTimeDays`** on `Product` or new `Supplier` entity
- [ ] **Log customer-facing sales** (POS integration) — not just inventory deductions
- [ ] **Enable Hibernate Envers** or custom audit table for full history

```java
// Example: Add to Product.java for AI readiness
@Column(name = "cost_price", precision = 10, scale = 2)
private BigDecimal costPrice;

@Column(name = "supplier_id")
private Long supplierId;

@Column(name = "lead_time_days")
private Integer leadTimeDays;

@Column(name = "is_perishable")
private Boolean isPerishable = false;

@Column(name = "shelf_life_days")
private Integer shelfLifeDays;
```

---

## 🧪 Testing Strategy for AI Features

| Layer | Tool | Scope |
|-------|------|-------|
| **Unit** | JUnit 5 + Mockito | `AiInsightService` logic, prompt templates |
| **Contract** | Spring Cloud Contract | `/api/ai/*` request/response schemas |
| **Integration** | Testcontainers (Ollama, Postgres/PGVector) | End-to-end RAG, embedding search |
| **Eval** | **Langfuse** / custom `EvaluationTest` | Prompt quality, hallucination rate, latency budgets |
| **Load** | Gatling / k6 | AI endpoint throughput (target: <500ms p95) |

---

## 📈 Monitoring & Observability

- **Actuator**: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
- **Custom Metrics**: `shopsmart.ai.forecast.accuracy`, `shopsmart.ai.latency.ms`
- **Distributed Tracing**: Micrometer Tracing → Zipkin / Tempo
- **Log Correlation**: Structured JSON logs (Logstash encoder) with `traceId`

---

## 🔒 Security Considerations for AI

| Risk | Mitigation |
|------|------------|
| **Prompt Injection** | Input sanitization, allow-listed SQL templates, no raw SQL from LLM |
| **Data Leakage** | Run local models (Ollama) for sensitive data; mask PII in prompts |
| **Cost Control** | Token budgets per request, circuit breaker on external LLM APIs |
| **Audit Trail** | Log every AI decision with input hash, model version, confidence score |

---

## 🗺️ Migration Path Summary

```
v1.x (Current)          →  v2.0                    →  v2.5                    →  v3.0
──────────────────────────────────────────────────────────────────────────────────────
Spring Boot 4           →  + Spring AI             →  + LangChain4j           →  + Event-driven (Kafka)
JPA + Oracle            →  + Python ML microsvc    →  + Vector DB (PGVector)  →  + Saga orchestration
JWT Auth                →  + Scheduled forecasting →  + NL-to-SQL / Chatbot    →  + Autonomous PO/pricing
REST + Swagger          →  + Actuator AI metrics   →  + Voice/WhatsApp        →  + Multi-store balancing
```

---

## 🤝 Contributing

1. Fork → feature branch (`feat/ai-demand-forecast`)
2. Write tests (unit + integration)
3. Update OpenAPI spec (`springdoc` auto-generates)
4. PR with description + screenshots for UI changes

---

## 📄 License

Proprietary — Internal use only.  
*Contact architecture team before external distribution.*

---

## 🙋 Support

- **Docs**: Swagger UI at `/shopsmart/swagger-ui.html`
- **Issues**: Internal Jira / GitHub Issues
- **AI Questions**: `#shopsmart-ai` Slack channel

---

> **Built for small shops. Designed for AI.**  
> *ShopSmart — where inventory meets intelligence.*