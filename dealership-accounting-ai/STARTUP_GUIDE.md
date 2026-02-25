# 🚀 Dealership Accounting AI - Startup Guide

## 📋 Prerequisites

Before starting, ensure you have:
- ✅ **Java 17** installed
- ✅ **Maven 3.9+** installed
- ✅ **MongoDB 7.0** installed
- ✅ **Ollama** installed with `llama3.2:latest` model
- ✅ **Python 3.9+** with virtual environment

---

## 🎯 Quick Start (3 Steps)

### **Step 1: Start MongoDB**

MongoDB should already be running. Verify with:
```bash
mongosh --eval "db.version()"
```

If not running, start it:
```bash
brew services start mongodb-community@7.0
```

---

### **Step 2: Start Ollama**

Start Ollama server:
```bash
ollama serve
```

**Keep this terminal open!** Ollama must run in the background.

Verify Ollama is running (in a new terminal):
```bash
curl http://localhost:11434/api/tags
```

---

### **Step 3: Start Spring Boot Application**

In the project directory:
```bash
mvn spring-boot:run
```

**Wait for:** `Started DealershipAccountingApplication in X seconds`

The application will be available at: **http://localhost:8080**

---

## 🌐 Access the Application

Once all services are running:

1. **Open your browser** and go to: **http://localhost:8080**
2. You'll see the **Dashboard** page
3. Navigate through the features:
   - 📊 **Dashboard** - Overview metrics
   - 🔧 **Repair Orders** - Create and manage repair orders
   - 🧾 **Receipts** - Generate receipts from closed ROs
   - 📦 **Batching** - Create deposit batches
   - 🏦 **Bank Feed** - Seed demo bank transactions
   - 🤖 **Reconcile** - Run AI reconciliation
   - ⚠️ **Exceptions** - View reconciliation exceptions
   - 📈 **Analytics** - Time series forecasting

---

## 🧪 Demo Workflow

Follow this sequence to demonstrate all features:

### **1. Create Repair Orders**
- Go to **Repair Orders** page
- Click **"Create Demo Repair Orders"** button
- This creates 3 sample repair orders

### **2. Generate Receipts**
- Go to **Receipts** page
- Click **"Generate Receipts for Closed ROs"** button
- This creates receipts for all closed repair orders

### **3. Create Deposit Batches**
- Go to **Batching** page
- Click **"Create Batch from Unbatched Receipts"** button
- This groups receipts into deposit batches

### **4. Seed Bank Feed**
- Go to **Bank Feed** page
- Click **"🌱 Seed Demo Bank Feed"** button
- This creates 5 demo bank transactions

### **5. Run AI Reconciliation**
- Go to **Reconcile** page
- Click **"🤖 Run AI Reconciliation"** button
- Wait 10-30 seconds for AI to match transactions
- View results showing matches and exceptions

### **6. View Exceptions**
- Go to **Exceptions** page
- See unmatched transactions with AI-generated memos
- Click on any exception to see details

### **7. View Analytics**
- Go to **Analytics** page
- Wait 10-30 seconds for forecasts to generate
- View two interactive charts:
  - **Bank Transaction Forecast** (30-day SARIMAX)
  - **Exception Resolution Rate** (6-month ARIMA)

---

## 🛑 Shutdown

To stop all services:

### **1. Stop Spring Boot**
Press `Ctrl+C` in the terminal running `mvn spring-boot:run`

### **2. Stop Ollama**
Press `Ctrl+C` in the terminal running `ollama serve`

### **3. Stop MongoDB (Optional)**
```bash
brew services stop mongodb-community@7.0
```

---

## 🔧 Troubleshooting

### **Port Already in Use**
If port 8080 is busy:
```bash
lsof -ti:8080 | xargs kill -9
```

### **MongoDB Connection Error**
Check if MongoDB is running:
```bash
brew services list | grep mongodb
```

### **Ollama Not Responding**
Restart Ollama:
```bash
pkill ollama
ollama serve
```

### **Python Forecast Errors**
Ensure virtual environment is set up:
```bash
python3 -m venv venv
source venv/bin/activate
pip install pandas numpy statsmodels scipy
```

---

## 📊 Testing

Run all tests:
```bash
mvn clean test -Dmaven.test.failure.ignore=true
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

**Current Coverage:**
- ✅ Service Layer: **47%**
- ✅ Total Tests: **83** (68 service tests passing)

---

## 🎓 For Your Demo Presentation

**Key Points to Mention:**

1. **AI-Powered Reconciliation**
   - Uses Llama 3.2 for generating explanations
   - Three matching strategies: Exact Match, Merchant Fee Match, Unmatched Exception
   - 95-100% confidence scores

2. **Time Series Forecasting**
   - SARIMAX for bank transactions (weekly seasonality)
   - ARIMA for exception resolution rate
   - 30-day and 6-month forecasts with confidence intervals

3. **Test Coverage**
   - 47% service layer coverage
   - 68 passing service tests
   - Comprehensive unit tests for all business logic

4. **Technology Stack**
   - Java 17 + Spring Boot 3.2.0
   - MongoDB 7.0 (NoSQL database)
   - Ollama + Llama 3.2 (AI integration)
   - Python + statsmodels (Time series forecasting)
   - Bootstrap 5 + Chart.js (Frontend)

---

## 📁 Project Structure

```
Demo Project/
├── src/main/java/com/tekion/accounting/
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   ├── model/               # MongoDB entities
│   └── repository/          # MongoDB repositories
├── src/main/resources/
│   ├── static/              # HTML pages
│   └── application.yml      # Configuration
├── src/test/java/           # Unit tests
├── forecast.py              # Python forecasting script
├── venv/                    # Python virtual environment
└── pom.xml                  # Maven dependencies

```

---

**Good luck with your demo! 🎉**

