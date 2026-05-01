# IBM Bob Hackathon – Flight Dashboard + Auto‑Fix (Bob‑First)

## Role & Mode
Orchestrator mode. Use Code mode and Advanced mode. You have file access, can run commands, have GitHub access.

## Goal
Build Flight Dashboard (Angular + Java) with realistic bugs. Auto‑fix via Bob: analyze log → generate fix → GitHub PR (branch `prod-fix-*`) → Slack → Playwright test.

## Stack
- Backend: Java 25, Spring Boot 3.4.x, Gradle 8.x (port 8080)
- Frontend: Angular 17 LTS, Material (port 4200)
- Log file: `production.log`
- Playwright, GitHub, Slack (optional)

## Best practices
DTOs, validation, global exception handler, env vars, structured logging, unit tests.

---

## Execution Steps (do in order, auto‑continue)

### Step 1 – Backend skeleton
Write:
- `backend/build.gradle` (Spring Boot, web, validation, lombok, okhttp, spotbugs)
- `backend/src/main/java/com/hackathon/MainApplication.java`
- `backend/src/main/resources/application.properties` (placeholders for GITHUB_TOKEN, SLACK_WEBHOOK_URL)
Run: `cd backend && ./gradlew build`

### Step 2 – Domain & DTO
Write:
- `backend/src/main/java/com/hackathon/entity/Flight.java` (id, flightNumber, airline (nullable), destination, scheduledTime, delayMinutes, cancelled)
- `backend/src/main/java/com/hackathon/dto/FlightDTO.java` (same fields)
- Mapper `FlightMapper`

### Step 3 – Flight service with realistic bugs
Write `FlightService.java`:
- 50 dummy flights; some with airline=null, delayMinutes=-3
- `getFlights(page, size)` throws IndexOutOfBounds if page out of range
- `getDelayedPercentage()` throws ArithmeticException if total flights == 0
- `parseFlightDate(String)` throws ParseException for bad timezone

### Step 4 – REST controller with validation
Write `FlightController.java`:
- `GET /api/flights?page=0&size=5` with `@Min(0) @Max(100)`
- Returns `List<FlightDTO>`
- Also endpoints: `/api/stats/delayed-percentage`, `/api/flights/parse-date`

### Step 5 – Global exception handler
Write `GlobalExceptionHandler.java`:
- Catches NPE, IndexOutOfBounds, ArithmeticException, ParseException
- Logs error as JSON to `production.log`
- Returns `ProblemDetail` with appropriate HTTP status

### Step 6 – Log analyzer service
Write `LogAnalyzerService.java`:
- `AnalysisResult` record: fixable, errorType, suggestedFix, originalCodeSnippet
- Regex to detect NullPointer, IndexOutOfBounds, Arithmetic, Parse

### Step 7 – Bob code generator (report logging)
Write `BobCodeGenerator.java`:
- `BobGenerationEvent` (timestamp, errorType, generatedSnippet)
- `generateFix(errorType, originalCode)` returns fix snippet:
  - NullPointer: `"if (obj == null) { return; }"`
  - IndexOutOfBounds: `"if (index < 0 || index >= list.size()) { return defaultValue; }"`
  - Arithmetic: `"if (denominator == 0) return 0;"`
  - Parse: `"try { return parseDefault(date); } catch (Exception e) { return fallbackDate; }"`
- `getBobReport()` returns list

### Step 8 – GitHub service (branch prod-fix-*, PR)
Write `GitHubService.java`:
- Uses env `GITHUB_TOKEN`
- `createPullRequest(repo, filePath, originalCode, fixedCode, errorType)`:
  - Branch name: `prod-fix-<timestamp>`
  - Commit fix
  - PR title: `"Auto-fix by IBM Bob: {errorType}"`
  - Return PR URL

### Step 9 – Slack notifier (mockable)
Write `SlackNotifier.java`:
- If `SLACK_WEBHOOK_URL` set, send real message; else console log
- Message: error type, fix snippet, PR link, mentions @developer @lead

### Step 10 – Playwright Codegen & test runner
Write:
- `PlaywrightCodegenService.java` – runs `npx playwright codegen http://localhost:4200 --output tests/recorded-test.spec.ts`
- `PlaywrightTestRunner.java` – runs `npx playwright test <file>` returns `TestResult`

### Step 11 – Orchestrator REST controller
Write `AutoFixController.java`:
- `POST /api/auto-fix` (body `{"logLine":"..."}`) → analyze → if fixable → generate fix → create PR → (optional) run test → Slack → return `{prUrl, fixGenerated, testPassed}`
- `POST /api/record-test` → launch Codegen

### Step 12 – Scheduled log watcher (auto mode)
Write `LogFileWatcher.java`:
- `@Scheduled(fixedDelay = 5000)`
- Reads `production.log`, sends new error lines to auto‑fix service (async)

### Step 13 – Bob report endpoint (for judging)
Write `BobReportController.java`:
- `GET /api/bob-report` returns JSON from `BobCodeGenerator.getBobReport()`

### Step 14 – Angular frontend (Flight Dashboard)
Write files:
- `frontend/package.json` (Angular 17, Material)
- `frontend/angular.json`
- `frontend/src/app/app.module.ts`
- `frontend/src/app/app.component.ts`, `.html`
- `frontend/src/app/services/flight.service.ts`
- `frontend/src/app/services/auto-fix.service.ts`

Features:
- Table of flights, pagination
- “Fix This Bug” button calls `/api/auto-fix`
- Panel to show PR link and fix snippet
- “Record Test” button
- Loading spinner, error toast

### Step 15 – Unit tests & code quality
Write:
- `backend/src/test/java/.../LogAnalyzerServiceTest.java` (JUnit 5, Mockito)
- `backend/src/test/java/.../GitHubServiceTest.java`
- `backend/spotbugs-exclude.xml`
- `.env.example` (GITHUB_TOKEN=, SLACK_WEBHOOK_URL=)

### Step 16 – Playwright test
Write `tests/fix-verification.spec.ts` (or Codegen generated): loads dashboard, clicks null airline row, verifies no error dialog.

### Step 17 – Verification commands
Run backend, frontend. Simulate error: `echo "ERROR: NullPointerException" >> production.log` or use UI. Expect PR created, Slack notified.

### Step 18 – Deliverables for submission
1. Entire code repository
2. `bob_sessions/` folder (screenshots of task summaries + exported markdown histories)
3. Video demo (5 min)
4. Problem/solution statement (Bob can generate)

---

## Optional watsonx Orchestrate (if time/Bobcoins left)
Create agent that calls `/api/auto-fix`, connect to Slack. Impresses judges but not required.

## Start Execution
Switch to Orchestrator mode. Execute Step 1 now. Auto‑continue.