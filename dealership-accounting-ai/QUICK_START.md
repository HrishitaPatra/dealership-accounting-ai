# ⚡ Quick Start - 3 Commands

## 🚀 Start Your Application

### **Terminal 1: Start Ollama**
```bash
ollama serve
```
**Keep this running!**

---

### **Terminal 2: Start Spring Boot**
```bash
cd "/Users/hrishitap/Tekion/Internship/Demo Project"
mvn spring-boot:run
```
**Wait for:** `Started DealershipAccountingApplication`

---

### **Terminal 3: Open Browser**
```bash
open http://localhost:8080
```

---

## ✅ Verify Everything is Running

```bash
# Check MongoDB
mongosh --eval "db.version()"

# Check Ollama
curl http://localhost:11434/api/tags

# Check Spring Boot
curl http://localhost:8080/actuator/health
```

---

## 🎬 Demo Flow (5 Minutes)

1. **Dashboard** → View metrics
2. **Repair Orders** → Click "Create Demo Repair Orders"
3. **Receipts** → Click "Generate Receipts for Closed ROs"
4. **Batching** → Click "Create Batch from Unbatched Receipts"
5. **Bank Feed** → Click "🌱 Seed Demo Bank Feed"
6. **Reconcile** → Click "🤖 Run AI Reconciliation" (wait 10-30s)
7. **Exceptions** → View AI-generated memos
8. **Analytics** → View forecasting charts (wait 10-30s)

---

## 🛑 Stop Everything

```bash
# Stop Spring Boot (Terminal 2)
Ctrl+C

# Stop Ollama (Terminal 1)
Ctrl+C
```

---

## 🆘 Emergency Reset

```bash
# Kill all processes
pkill -f "spring-boot:run"
pkill ollama

# Clear database
mongosh dealership_db --eval "db.dropDatabase()"

# Restart
ollama serve &
mvn spring-boot:run
```

---

## 📊 URLs

- **Application:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health
- **Ollama:** http://localhost:11434
- **MongoDB:** localhost:27017

---

**See STARTUP_GUIDE.md for detailed instructions!**

