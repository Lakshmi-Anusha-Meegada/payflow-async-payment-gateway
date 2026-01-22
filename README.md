# PayFlow – Async Payment Gateway

## 📌 Overview
PayFlow is a production-style **asynchronous payment gateway** built using **Spring Boot and Redis**.  
It demonstrates how real-world payment systems handle **non-blocking processing, background workers, and webhooks**.

The system uses **Redis queues** to decouple payment initiation, processing, and webhook delivery.

---

## 🚀 Features
- Asynchronous payment processing using Redis queues
- Background worker for payment execution
- Separate webhook worker for event delivery
- Retry mechanism for failed webhooks
- Event-driven architecture
- In-memory fake database for safe testing (no PostgreSQL required)
- Dockerized Redis setup

---

## 🛠 Tech Stack
- **Java 17**
- **Spring Boot**
- **Redis**
- **Docker & Docker Compose**
- **Maven**

---

## 🧠 Architecture

Payment Request
↓
Redis Queue (payflow_jobs)
↓
PaymentWorker
↓
Payment Result (success / failed)
↓
Redis Queue (webhook_jobs)
↓
WebhookWorker
↓
Webhook Delivered (with retry)


---

## 📂 Project Structure



payflow/
├── backend/
│ ├── src/main/java/com/gateway/
│ │ ├── GatewayApplication.java
│ │ ├── config/RedisConfig.java
│ │ └── workers/
│ │ ├── PaymentWorker.java
│ │ └── WebhookWorker.java
│ ├── Dockerfile
│ ├── Dockerfile.worker
│ └── pom.xml
├── docker-compose.yml
└── .gitignore


---

## ▶️ How to Run the Project

### 1️⃣ Start Redis using Docker
```bash
docker-compose up -d

2️⃣ Run the Spring Boot application
cd backend
mvn spring-boot:run

## 🧪 How to Test the Flow
## Push a payment job into Redis
docker exec -it redis_payflow redis-cli
LPUSH payflow_jobs "process_payment:pay_test_1001:upi"

## Expected Console Output
Job received: process_payment:pay_test_1001:upi
Processing payment: pay_test_1001
PAYMENT SUCCESS for pay_test_1001
Webhook job queued: webhook:payment.success:pay_test_1001
Delivering webhook: webhook:payment.success:pay_test_1001
Webhook delivered: webhook:payment.success:pay_test_1001

## 🔁 Webhook Retry Logic

Webhook delivery is simulated with random failures

Failed webhooks are pushed back into the Redis queue

Worker retries delivery automatically

##📌 Notes

Database is intentionally replaced with an in-memory fake DB for simplicity

Real database integration (PostgreSQL/JPA) can be added later without changing core logic

Architecture follows real-world fintech async design

## 👩‍💻 Author

Lakshmi Anusha Meegada
