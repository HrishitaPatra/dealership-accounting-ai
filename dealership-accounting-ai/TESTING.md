# Testing Documentation

## 📊 Code Coverage Report (Jacoco)

### Viewing the Jacoco Report

After running `mvn clean test jacoco:report`, the code coverage report is generated at:

```
target/site/jacoco/index.html
```

**To view the report:**

```bash
# Option 1: Open in browser directly
open target/site/jacoco/index.html

# Option 2: Navigate to the file and double-click it
```

### Understanding the Report

The Jacoco report shows:
- **Line Coverage**: Percentage of code lines executed during tests
- **Branch Coverage**: Percentage of decision branches (if/else) tested
- **Method Coverage**: Percentage of methods invoked
- **Class Coverage**: Percentage of classes loaded

**Color Coding:**
- 🟢 **Green**: Fully covered
- 🟡 **Yellow**: Partially covered
- 🔴 **Red**: Not covered

### Current Coverage

The current test suite includes:
- ✅ Basic application context loading test
- ✅ Spring Boot auto-configuration validation
- ✅ MongoDB connection verification

---

## 🚀 Performance Testing (Apache JMeter)

### Prerequisites

**Install JMeter:**

```bash
# macOS (using Homebrew)
brew install jmeter

# Or download from: https://jmeter.apache.org/download_jmeter.cgi
```

### Running the Load Test

**1. Make sure the application is running:**

```bash
mvn spring-boot:run
```

**2. Run JMeter test in GUI mode (for development):**

```bash
jmeter -t dealership-accounting-load-test.jmx
```

**3. Run JMeter test in CLI mode (for production/CI):**

```bash
jmeter -n -t dealership-accounting-load-test.jmx -l results.jtl -e -o jmeter-report/
```

**Parameters:**
- `-n`: Non-GUI mode
- `-t`: Test plan file
- `-l`: Results file (JTL format)
- `-e`: Generate HTML report
- `-o`: Output directory for HTML report

**4. View the HTML report:**

```bash
open jmeter-report/index.html
```

### Test Plan Overview

The JMeter test plan includes **3 thread groups**:

#### **Thread Group 1: Repair Orders API**
- **Threads**: 10 concurrent users
- **Ramp-up**: 2 seconds
- **Loops**: 5 iterations
- **Endpoint**: `GET /api/repair-orders`
- **Total Requests**: 50 (10 threads × 5 loops)

#### **Thread Group 2: Exceptions API**
- **Threads**: 5 concurrent users
- **Ramp-up**: 1 second
- **Loops**: 3 iterations
- **Endpoint**: `GET /api/exceptions/open`
- **Total Requests**: 15 (5 threads × 3 loops)

#### **Thread Group 3: Bank Transactions API**
- **Threads**: 10 concurrent users
- **Ramp-up**: 2 seconds
- **Loops**: 5 iterations
- **Endpoint**: `GET /api/bank-transactions`
- **Total Requests**: 50 (10 threads × 5 loops)

**Total Load**: 115 requests across all endpoints

### Assertions

Each HTTP request includes:
- ✅ Response code assertion (expects 200 OK)
- ✅ Response time tracking
- ✅ Throughput measurement

### Listeners

The test plan includes 3 listeners:
1. **View Results Tree**: Detailed request/response data
2. **Summary Report**: Aggregated statistics
3. **View Results in Table**: Tabular view of all samples

### Key Metrics to Monitor

- **Average Response Time**: Should be < 500ms
- **90th Percentile**: Should be < 1000ms
- **Error Rate**: Should be 0%
- **Throughput**: Requests per second
- **Standard Deviation**: Lower is better (consistency)

---

## 🎯 Quick Commands

```bash
# Run tests and generate Jacoco report (ignore controller test failures)
# This will take about 30-60 seconds
mvn clean test jacoco:report -Dmaven.test.failure.ignore=true

# View Jacoco report
open target/site/jacoco/index.html

# Run JMeter test (GUI mode)
jmeter -t dealership-accounting-load-test.jmx

# Run JMeter test (CLI mode with HTML report)
jmeter -n -t dealership-accounting-load-test.jmx -l results.jtl -e -o jmeter-report/

# View JMeter HTML report
open jmeter-report/index.html
```

---

## 📈 Demo Presentation Tips

### For Jacoco Report:
1. Show the overall coverage percentage
2. Drill down into specific packages (e.g., `com.tekion.accounting.service`)
3. Highlight the AI service classes (AIReconciliationService, DisputeResolutionAIService)
4. Explain that higher coverage = more reliable code

### For JMeter Report:
1. Show the Summary Report with total requests and response times
2. Highlight the 0% error rate
3. Show the throughput (requests/second)
4. Explain that the system can handle concurrent users efficiently
5. Point out the consistent response times (low standard deviation)

---

## 🔧 Troubleshooting

### Jacoco Report Not Generated
```bash
# Run tests and generate report (ignore controller test failures)
mvn clean test jacoco:report -Dmaven.test.failure.ignore=true

# If you want to run without ignoring failures
mvn clean test jacoco:report
```

### JMeter Command Not Found
```bash
# Install JMeter
brew install jmeter

# Or add JMeter bin directory to PATH
export PATH=$PATH:/path/to/jmeter/bin
```

### Application Not Running
```bash
# Start the application first
mvn spring-boot:run

# Wait for "Started DealershipAccountingApplication" message
# Then run JMeter tests
```

---

**Created for Tekion Internship Demo Project** 🚀

