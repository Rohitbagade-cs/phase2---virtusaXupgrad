# ✅ Banking System Simulator — Microservices Architecture (Spring Boot)

A complete Banking System built using Spring Boot Microservices, featuring:

- Eureka Server (Service Discovery)
- API Gateway (Routing + Load Balancing)
- Account Service (MongoDB)
- Transaction Service (MongoDB + Feign + Resilience4j)
- Notification Service (MongoDB)
- Docker Compose (Run all services together)
- Unit Tests (Assignment Requirement)

---

## 🏛️ Architecture Diagram

### High-Level Microservices Architecture

```
Clients
   ↓
API Gateway (8080)
   │
   ├──> Account Service (8081) ------- MongoDB (accounts_db)
   ├──> Transaction Service (8082) --- MongoDB (transactions_db)
   └──> Notification Service (8083) -- MongoDB (notifications_db)

All services register with:
Eureka Server (8761)
```

### Architecture Diagram

<img width="5824" height="2822" alt="Untitled diagram-2025-12-11-131219" src="https://github.com/user-attachments/assets/810166e2-11dd-4872-8e66-3775f8a37b75" />

---

## 📂 Project Structure

```
banking-system/
├── eureka-server/
├── api-gateway/
├── account-service/
├── transaction-service/
├── notification-service/
├── docker-compose.yml
└── README.md
```

---

## ⚙️ Prerequisites

Before running, install:

- Java 17
- Maven 3.6+
- Docker + Docker Compose
- (Optional) Postman / curl

### Verify Installation:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## 🚀 How to Build & Run the Entire System

### Step 1 — Build all services (required before Docker Compose)

Run these commands inside project root:

```bash
cd eureka-server && mvn clean package -DskipTests && cd ..
cd account-service && mvn clean package -DskipTests && cd ..
cd transaction-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
cd api-gateway && mvn clean package -DskipTests && cd ..
```

Each service will generate its JAR file inside `target/`.

### 🐳 Step 2 — Start Everything Using Docker Compose

From the project root:

```bash
docker compose up --build -d
```

Check running containers:

```bash
docker compose ps
```

Check logs:

```bash
docker compose logs -f
```

---

## 🌐 Service URLs

| Service              | URL                                            |
| -------------------- | ---------------------------------------------- |
| Eureka Server        | [http://localhost:8761](http://localhost:8761) |
| API Gateway          | [http://localhost:8080](http://localhost:8080) |
| Account Service      | [http://localhost:8081](http://localhost:8081) |
| Transaction Service  | [http://localhost:8082](http://localhost:8082) |
| Notification Service | [http://localhost:8083](http://localhost:8083) |

---

## 🧪 API Testing (Through API Gateway Only)

### 1️⃣ Create Account

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"ACC1001","holderName":"Rohit","balance":1000}'
```

### 2️⃣ Get Account

```bash
curl http://localhost:8080/api/accounts/ACC1001
```

### 3️⃣ Deposit

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"ACC1001","amount":500}'
```

### 4️⃣ Withdraw

```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"ACC1001","amount":100}'
```

### 5️⃣ Transfer

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{"sourceAccount":"ACC1001","destinationAccount":"ACC2001","amount":50}'
```

### 6️⃣ Send Notification

```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"TXN-1","message":"Test Notification","accountNumber":"ACC1001"}'
```

---

## 🧪 Unit Testing (Assignment Requirement)

### Run tests for all services:

```bash
mvn test
```

### Run tests for AccountService only:

```bash
mvn -pl account-service test
```

### Run tests for TransactionService only:

```bash
mvn -pl transaction-service test
```

### Unit tests validate:

- ✔ Account creation
- ✔ Balance updates
- ✔ Deposit / Withdraw logic
- ✔ Transaction success & failure
- ✔ Compensation logic in transfers (refund case)

---

## 🔧 Troubleshooting Guide

### ❌ Service not registering in Eureka

**Fix:** Ensure this value exists in `docker-compose.yml`:

```yaml
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka/
```

### ❌ MongoDB connection refused

**Fix:** Ensure MongoDB service names match:

- `mongodb_accounts`
- `mongodb_transactions`
- `mongodb_notifications`

### ❌ Gateway returns 503

**Cause:** Service not yet registered in Eureka.

**Fix:** Wait a few seconds & refresh: http://localhost:8761

### ❌ JAR not found during docker build

**Fix:** Build each service first:

```bash
mvn clean package -DskipTests
```

---

## 📝 Future Enhancements (Optional)

- Add JWT authentication via API Gateway
- Implement Saga pattern for transaction consistency
- Add ELK (Elasticsearch + Logstash + Kibana) for centralized logs
- Add Prometheus + Grafana for metrics
- Add Kafka or RabbitMQ for async notification service
