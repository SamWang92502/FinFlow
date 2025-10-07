🏦 FinFlow

A backend-driven fintech system inspired by Affirm’s loan-to-merchant disbursement model

🌍 Project Overview

FinFlow is a backend-focused fintech simulation built with Java + Spring Boot that models how a modern Buy Now, Pay Later (BNPL) provider like Affirm operates behind the scenes.

It demonstrates realistic financial workflows such as loan approval, bank linking, merchant disbursement, and repayment scheduling, while solving real-world engineering challenges like distributed consistency, retry logic, and observability.

🧩 Core Domain Aggregates
Entity	Description
Customer	Represents an end user applying for credit within FinFlow.
BankLink	Represents the user’s linked bank account or financial institution — used for underwriting and repayment via mock Plaid-like integration.
Disbursement	Represents the funds sent from FinFlow to merchants after a customer’s loan is approved and finalized.
💰 Example Business Flow

Create Customer → POST /customers
Stores user profile and credit data in Postgres.

Link Bank → POST /banklinks
Connects the customer’s external bank using mock Plaid-like credentials, simulating secure data access for underwriting and repayment setup.

Initiate Loan → POST /loans (optional extension)
Applies configurable underwriting rules to determine approval and generate loan terms.

Disburse Funds → POST /disbursements
Once the loan is approved, FinFlow disburses funds to the merchant on behalf of the customer.

Check Disbursement Status → GET /disbursements/{id}
Fetches transaction status — first from Redis (cache) for low latency, then Postgres as a fallback.

🧠 Tech Stack

Backend: Java 21, Spring Boot 3 (Web, Data JPA, Validation, Redis)

Database: PostgreSQL (via Docker)

Caching: Redis for low-latency reads and idempotent writes

Message/Event Layer (optional): Kafka for saga orchestration

Observability: Prometheus + OpenTelemetry

Testing: JUnit 5, Spring Boot Test

Build Tool: Gradle

⚙️ Key Engineering Features

Idempotency Keys: Prevent double disbursements or duplicate API retries

Saga Workflow (Kafka): Ensures eventual consistency across loan → ledger → disbursement → repayment flows

Circuit Breakers & Retries: Handle partner API outages gracefully

Config-Driven Rules: Hot-reloadable underwriting and approval logic

Outbox Pattern: Guarantees exactly-once delivery of disbursement events

Structured Logging & Metrics: Enable deep observability and debugging

💡 Pain Points & Solutions
1. API Fragility & Partner Unreliability

Banks, ACH gateways, and payment processors often fail, throttle, or timeout — risking double disbursements or incomplete payments.

✅ Solution: Implement circuit breakers, retry policies, and an outbox pattern to guarantee at-least-once delivery and prevent duplicate merchant payouts.

2. Legacy vs. Modern System Mismatch

Many financial institutions still use batch-based legacy systems (e.g., NACHA files), while fintechs rely on real-time APIs.

✅ Solution: Introduce a legacy adapter for batch ingestion and a modern REST/GraphQL API layer, ensuring seamless integration between both worlds.

3. Slow Loan Decisioning (Underwriting Bottlenecks)

Underwriting can be slow when every loan approval request repeatedly queries databases or external APIs.

✅ Solution: Use Redis caching for high-frequency lookups and hot-reloadable underwriting rules, reducing latency and enabling instant decisions.

4. Distributed Transaction Consistency

The multi-step flow — approve loan → record ledger → disburse to merchant → schedule repayments — risks inconsistency if any step fails.

✅ Solution: Use a Kafka-based saga workflow and compensating transactions to ensure eventual consistency across systems.

5. Lack of Auditability & Compliance Adaptability

Fintech lenders must explain why a loan was approved or declined and adjust policies as regulations evolve.

✅ Solution: Store config-driven underwriting policies with auditable reason codes, enabling transparent and explainable loan decisions.

6. Observability Gaps

Without visibility, issues like stuck disbursements, ACH failures, or slow partner SLAs can go unnoticed.

✅ Solution: Add OpenTelemetry tracing, Prometheus metrics, and structured audit logs to track system health in real time.

7. Reconciliation & Settlement Drift (BNPL-Specific)

Merchant settlement reports, ACH files, and internal ledgers can drift out of sync.

✅ Solution: Implement a nightly reconciliation job that compares external and internal records, flagging mismatches for manual review.

8. ACH Failures & Repayment Risk (BNPL-Specific)

After paying merchants, ACH debits from customer accounts can fail (e.g., insufficient funds, authorization errors).

✅ Solution: Track installment schedules and ACH return codes (R01, R10), trigger retry/backoff logic, and update repayment status for risk monitoring.

9. Optional Advanced Extension: Funding Channel Routing

Some loans are held internally, while others are sold to partner banks.

✅ Solution: Add a funding router that marks loans as held or sold and directs disbursements accordingly — simulating real-world loan distribution.
