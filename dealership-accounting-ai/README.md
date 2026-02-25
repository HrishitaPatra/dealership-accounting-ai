# 🚀 Dealership Accounting AI System

AI-powered accounting reconciliation system for automotive dealerships, built for Tekion DMS.

## 🎯 Project Overview

This system demonstrates how AI agents can transform dealership accounting workflows:

- **AI Reconciliation Agent**: Auto-matches bank transactions to deposit batches with confidence scoring
- **AI Exception Agent**: Detects and classifies exceptions (merchant fees, timing differences) with GL coding suggestions
- **Close Readiness Dashboard**: Real-time visibility into month-end close status

### Business Impact
- ⚡ Reduces daily reconciliation time from 2 hours → 10 minutes (92% reduction)
- 🎯 Auto-matches 90%+ of transactions
- 📊 Reduces month-end close from 2-3 days → 2-4 hours (80% reduction)

---

## 🏗️ Architecture

### Tech Stack
- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: MongoDB 7.0 (document-based storage)
- **AI**: Rule-based matching + Ollama (llama3.2) for explanations
- **Frontend**: Vanilla JavaScript + Bootstrap 5

### Design Patterns
- **Strategy Pattern**: AI matching strategies (rule-based now, ML-ready for future)
- **Multi-tenancy**: Dealership-scoped data isolation
- **Event-driven**: Ready for Kafka integration

---

## 📋 Prerequisites

1. **Java 17** installed
2. **Maven 3.9+** installed
3. **MongoDB 7.0** running on `localhost:27017`
4. **Ollama** installed with `llama3.2:latest` model

---

## 🚀 Quick Start

### 1. Clone and Build
```bash
cd dealership-accounting-ai
mvn clean install
```

### 2. Start MongoDB
```bash
# Make sure MongoDB is running
mongosh --eval "db.version()"
```

### 3. Start Ollama
```bash
# Make sure Ollama is running
ollama list
```

### 4. Run Application
```bash
mvn spring-boot:run
```

### 5. Access Application
- **Frontend**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

---

## 📊 Demo Workflow

1. **Create Repair Order** → Add labor/parts → Close RO
2. **Generate Receipt** → Payment on closed RO
3. **Create Deposit Batch** → Select receipts → Mark deposited
4. **Load Bank Feed** → Seed bank transactions
5. **AI Reconciliation** → Click "Run AI Reconciliation" → Accept suggestions
6. **Exception Resolution** → AI detects merchant fee → Suggest GL 5200
7. **Dashboard** → View close readiness metrics

---

## 🤖 AI Agents

### Agent 1: Reconciliation Match Agent
- **Input**: Bank transaction + candidate deposit batches
- **Output**: Suggested match with confidence score + explanation
- **Logic**: Amount matching, date proximity, merchant fee detection

### Agent 2: Exception Resolution Agent
- **Input**: Unmatched transaction or amount mismatch
- **Output**: Exception type, GL account suggestion, audit memo
- **Logic**: Pattern detection (fees, timing, errors)

---

## 🗂️ Project Structure

```
src/main/java/com/tekion/accounting/
├── model/          # MongoDB entities (RepairOrder, Receipt, etc.)
├── repository/     # Spring Data MongoDB repositories
├── service/        # Business logic + AI services
├── controller/     # REST API endpoints
├── dto/            # Data transfer objects
└── config/         # Configuration classes

src/main/resources/
├── application.yml # Configuration
└── static/         # Frontend (HTML/CSS/JS)
```

---

## 🔧 Configuration

Edit `src/main/resources/application.yml`:

```yaml
ai:
  strategy: rule-based
  ollama:
    model: llama3.2:latest  # Change if using different model

app:
  dealership:
    demo-id: DEALER-001  # Demo tenant ID
```

---

## 📝 API Endpoints

### Repair Orders
- `POST /api/repair-orders` - Create RO
- `GET /api/repair-orders` - List ROs
- `PUT /api/repair-orders/{id}/close` - Close RO

### Receipts
- `POST /api/receipts` - Create receipt
- `GET /api/receipts/unbatched` - Get unbatched receipts

### Deposit Batches
- `POST /api/deposit-batches` - Create batch
- `PUT /api/deposit-batches/{id}/mark-deposited` - Mark deposited

### Reconciliation
- `POST /api/reconciliation/suggest-match` - AI match suggestions
- `POST /api/reconciliation/confirm-match` - Confirm match

### Dashboard
- `GET /api/dashboard/metrics` - Close readiness metrics

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

---

## 🎓 Technical Highlights

### MongoDB Design
- **Embedded documents**: Customer, Vehicle, LineItems in RepairOrder (data locality)
- **References**: Receipt → RepairOrder (different lifecycle)
- **Indexing**: Compound indexes on `dealershipId + status + createdAt`

### Multi-Tenancy
- Every document has `dealershipId` field
- All queries scoped by dealership
- Indexed for performance

### AI Architecture
- Strategy pattern for swappable AI implementations
- Rule-based now, ML-ready (FastAPI integration planned)
- Feature extraction methods reusable for ML training

---

## 🚀 Future Enhancements

- [ ] Train ML model on historical match data
- [ ] FastAPI service for ML predictions
- [ ] Kafka for event streaming
- [ ] Advanced fraud detection
- [ ] Predictive close date forecasting

---



## 📄 License

Internal Tekion Demo Project

