# Finflow

> A backend service for managing accounts, payments, and ledgers — built with **Java 21** and **Spring Boot**.  
> Designed as a **FinTech practice project** to explore production-grade patterns such as caching, retries, circuit breakers, and containerized deployment with Kubernetes.

---

## ✨ Features

- **Accounts**: create, view, and close accounts
- **Payments**: initiate transfers with idempotency keys
- **Ledger**: double-entry system (credit/debit tracking)
- **Caching**: optional Redis integration for faster reads
- **Resilience**: retry & circuit breaker patterns (Resilience4j)
- **Observability**: Spring Actuator endpoints for health and metrics
- **Deployable to Kubernetes**: packaged as Docker image, ready for K8s manifests

---

## 🛠 Tech Stack

- **Language:** Java 21  
- **Framework:** Spring Boot 3.x  
- **Build Tool:** Gradle (Kotlin DSL)  
- **Database:** PostgreSQL (with Flyway migrations)  
- **Cache:** Redis  
- **Testing:** JUnit 5 + Spring Boot Test  
- **Deployment:** Docker & Kubernetes (manifests in `/k8s`)  

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Gradle (or use the included wrapper)
- Docker (for Postgres + Redis)

### Run locally (with Docker Compose)
```bash
# 1. Start dependencies
docker compose up -d

# 2. Run the app
./gradlew bootRun
