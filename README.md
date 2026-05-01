# IBM Bob Hackathon - Production Issue Auto-Fix Demonstration

![IBM Bob](https://img.shields.io/badge/IBM-Bob-blue)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)
![Angular](https://img.shields.io/badge/Angular-17-red)
![Playwright](https://img.shields.io/badge/Playwright-E2E-purple)

## 🎯 Project Overview

This project demonstrates IBM Bob's capability to automatically detect production bugs, generate fixes, create GitHub pull requests, and send Slack notifications - all without human intervention.

### Key Features

- **Automatic Bug Detection**: Monitors production logs for runtime exceptions
- **Intelligent Fix Generation**: Bob analyzes errors and generates appropriate fixes
- **GitHub Integration**: Automatically creates branches and pull requests
- **Slack Notifications**: Real-time alerts with fix details
- **Playwright Testing**: Automated E2E verification of fixes
- **Flight Dashboard**: Interactive UI to trigger and monitor auto-fix workflow

## 🏗️ Architecture

```
┌─────────────────┐
│  Flight Dashboard│  (Angular 17 + Material UI)
│   (Port 4200)   │
└────────┬────────┘
         │ REST API
         ▼
┌─────────────────┐
│  Spring Boot    │  (Java 25)
│   Backend       │
│   (Port 8080)   │
└────────┬────────┘
         │
    ┌────┴────┬──────────┬──────────┐
    ▼         ▼          ▼          ▼
┌────────┐ ┌──────┐ ┌────────┐ ┌──────────┐
│  Log   │ │GitHub│ │ Slack  │ │Playwright│
│Analyzer│ │  API │ │Webhook │ │  Tests   │
└────────┘ └──────┘ └────────┘ └──────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 25** (or Java 21+)
- **Node.js 18+** and npm
- **Gradle 8.x**
- **Angular CLI 17**
- **Git**
- (Optional) GitHub Personal Access Token
- (Optional) Slack Webhook URL

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd Prod-Issue-Process-Automation-bob-Hackathon
```

2. **Configure environment variables**
```bash
cp .env.example .env
# Edit .env with your GitHub token and Slack webhook (optional for demo)
```

3. **Install backend dependencies**
```bash
cd backend
./gradlew build
```

4. **Install frontend dependencies**
```bash
cd ../frontend
npm install
```

5. **Install Playwright**
```bash
cd ..
npm install
npx playwright install
```

### Running the Application

#### Terminal 1: Start Backend
```bash
cd backend
./gradlew bootRun
```
Backend will start on `http://localhost:8080`

#### Terminal 2: Start Frontend
```bash
cd frontend
npm start
```
Frontend will start on `http://localhost:4200`

#### Terminal 3: Run E2E Tests (Optional)
```bash
npm test
```

## 🎮 Demo Workflow

### Step 1: Access the Dashboard
Navigate to `http://localhost:4200` to see the Flight Dashboard with 50 flights.

### Step 2: Identify Buggy Flights
Look for flights with:
- **Red highlighting**: Indicates intentional bugs
- **"N/A" airline**: Triggers NullPointerException
- **Negative delays**: Triggers IndexOutOfBoundsException

### Step 3: Trigger Auto-Fix
1. Click "Fix This Bug" button on any problematic flight
2. Bob analyzes the error from production.log
3. Bob generates a fix snippet
4. Bob creates a GitHub branch and PR
5. Bob sends Slack notification
6. View the PR link and fix details in the UI

### Step 4: Verify the Fix
1. Click "Record Test" to generate Playwright test
2. Run E2E tests to verify the fix works
3. Check the Bob Report endpoint for statistics

## 📊 Intentional Bugs

The application includes 4 types of intentional bugs for demonstration:

### 1. NullPointerException
**Location**: `FlightService.getFlights()`
```java
// Intentional bug: No null check
String airline = flight.getAirline();
return airline.toUpperCase(); // NPE when airline is null
```

**Bob's Fix**:
```java
if (airline == null) {
    log.warn("Null airline detected for flight {}", flight.getId());
    return "UNKNOWN";
}
return airline.toUpperCase();
```

### 2. IndexOutOfBoundsException
**Location**: `FlightService.getFlights()`
```java
// Intentional bug: No bounds checking
return flights.get(page * size); // Throws when index >= size
```

**Bob's Fix**:
```java
int index = page * size;
if (index >= 0 && index < flights.size()) {
    return flights.get(index);
}
throw new IllegalArgumentException("Invalid page number");
```

### 3. ArithmeticException
**Location**: `FlightService.getDelayedPercentage()`
```java
// Intentional bug: Division by zero
return (delayedCount * 100) / totalFlights; // Throws when totalFlights = 0
```

**Bob's Fix**:
```java
if (totalFlights == 0) {
    log.warn("No flights available for percentage calculation");
    return 0.0;
}
return (delayedCount * 100.0) / totalFlights;
```

### 4. DateTimeParseException
**Location**: `FlightService.parseFlightDate()`
```java
// Intentional bug: Strict date format
DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
return LocalDateTime.parse(dateStr, formatter); // Fails on simple formats
```

**Bob's Fix**:
```java
try {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
} catch (DateTimeParseException e) {
    log.warn("Failed to parse date: {}, trying alternative format", dateStr);
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
}
```

## 🧪 Testing

### Unit Tests
```bash
cd backend
./gradlew test
```

### E2E Tests
```bash
npm test
```

### Code Quality
```bash
cd backend
./gradlew spotbugsMain
```

## 📡 API Endpoints

### Flight Management
- `GET /api/flights?page=0&size=10` - Get paginated flights
- `GET /api/flights/stats` - Get flight statistics

### Auto-Fix Workflow
- `POST /api/auto-fix` - Trigger auto-fix for an error
- `GET /api/auto-fix/status` - Get system status
- `POST /api/auto-fix/batch` - Process multiple log entries

### Bob Reporting (for Judges)
- `GET /api/bob-report` - Get Bob's generation report
- `GET /api/bob-report/export` - Export report as JSON
- `GET /api/bob-report/stats` - Get fix statistics

### Playwright Integration
- `POST /api/record-test` - Record a Playwright test

## 🎥 Video Demo

A 5-minute video demonstration is available in the `bob_sessions/` folder showing:
1. Dashboard overview
2. Triggering auto-fix workflow
3. GitHub PR creation
4. Slack notification
5. Playwright test verification

## 📁 Project Structure

```
Prod-Issue-Process-Automation-bob-Hackathon/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/hackathon/
│   │       ├── controller/     # REST controllers
│   │       ├── service/        # Business logic
│   │       ├── entity/         # JPA entities
│   │       ├── dto/            # Data transfer objects
│   │       ├── model/          # Domain models
│   │       ├── mapper/         # Entity-DTO mappers
│   │       └── exception/      # Exception handlers
│   ├── src/test/java/          # Unit tests
│   └── build.gradle            # Gradle configuration
├── frontend/                   # Angular frontend
│   ├── src/app/
│   │   ├── services/           # HTTP services
│   │   ├── app.component.*     # Main component
│   │   └── app.module.ts       # App module
│   └── package.json            # npm dependencies
├── tests/                      # Playwright E2E tests
│   └── fix-verification.spec.ts
├── bob_sessions/               # Bob interaction logs
├── .env.example                # Environment template
├── playwright.config.ts        # Playwright configuration
└── README.md                   # This file
```

## 🔧 Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.properties`:
```properties
server.port=8080
github.token=${GITHUB_TOKEN:}
github.repo.owner=${GITHUB_REPO_OWNER:}
github.repo.name=${GITHUB_REPO_NAME:}
slack.webhook.url=${SLACK_WEBHOOK_URL:}
```

### Frontend Configuration
The frontend connects to `http://localhost:8080` by default. To change:
Edit `frontend/src/app/services/*.service.ts` and update the `apiUrl`.

## 🎯 Judging Criteria Alignment

### 1. Problem-Solution Fit
- **Problem**: Production bugs require manual intervention, causing delays
- **Solution**: Bob automatically detects, fixes, and deploys solutions

### 2. Technical Implementation
- Java 25 with Spring Boot 3.4.0
- Angular 17 with Material UI
- Playwright for E2E testing
- GitHub API integration
- Slack webhook integration

### 3. Innovation
- Real-time log monitoring with @Scheduled tasks
- Intelligent fix generation based on error patterns
- Automated PR creation with detailed descriptions
- E2E test generation for verification

### 4. Code Quality
- JUnit 5 unit tests with Mockito
- SpotBugs static analysis
- Comprehensive error handling
- Clean architecture with separation of concerns

### 5. Demonstration
- Interactive dashboard
- Real-time workflow visualization
- Comprehensive logging
- Video demonstration included

## 🤝 Contributing

This is a hackathon project. For questions or feedback, please contact the team.

## 📄 License

MIT License - IBM Hackathon 2026

## 🙏 Acknowledgments

- IBM Bob Team
- Spring Boot Community
- Angular Team
- Playwright Contributors

---

**Built with ❤️ for IBM Bob Hackathon 2026**