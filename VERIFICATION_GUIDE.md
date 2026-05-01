# IBM Bob Hackathon - Verification Guide

This guide provides step-by-step instructions for judges to verify the IBM Bob auto-fix demonstration.

## 🎯 Verification Checklist

- [ ] Backend starts successfully
- [ ] Frontend loads without errors
- [ ] Flight dashboard displays 50 flights
- [ ] Intentional bugs are visible
- [ ] Auto-fix workflow completes successfully
- [ ] GitHub PR is created (or mock URL displayed)
- [ ] Slack notification is sent (or console logged)
- [ ] Playwright tests pass
- [ ] Bob report shows statistics

## 📋 Prerequisites Verification

### 1. Check Java Version
```bash
java -version
```
Expected: Java 25 or Java 21+

### 2. Check Node.js Version
```bash
node --version
npm --version
```
Expected: Node.js 18+ and npm 9+

### 3. Check Gradle
```bash
cd backend
./gradlew --version
```
Expected: Gradle 8.x

## 🚀 Step-by-Step Verification

### Step 1: Start the Backend

```bash
cd backend
./gradlew bootRun
```

**Expected Output:**
```
> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

IBM Bob Hackathon Application
Started MainApplication in X.XXX seconds
```

**Verification Points:**
- [ ] Application starts without errors
- [ ] Port 8080 is listening
- [ ] No exception stack traces in console
- [ ] Log file `production.log` is created

**Test Backend API:**
```bash
curl http://localhost:8080/api/flights?page=0&size=10
```

Expected: JSON response with 10 flights

### Step 2: Start the Frontend

Open a new terminal:
```bash
cd frontend
npm install
npm start
```

**Expected Output:**
```
✔ Browser application bundle generation complete.
** Angular Live Development Server is listening on localhost:4200 **
```

**Verification Points:**
- [ ] npm install completes without errors
- [ ] Angular dev server starts on port 4200
- [ ] No compilation errors

**Test Frontend:**
Open browser to `http://localhost:4200`

Expected: Flight Dashboard loads with Material UI styling

### Step 3: Verify Dashboard UI

**Visual Verification:**
- [ ] Toolbar displays "IBM Bob - Flight Dashboard"
- [ ] System status badges visible (Backend, Log Watcher, GitHub, Slack)
- [ ] Flight table displays 10 rows
- [ ] Pagination controls visible at bottom
- [ ] Columns: Flight Number, Airline, Origin, Destination, Departure, Status, Delay, Actions

**Interaction Verification:**
- [ ] Click "Next page" - table updates to show flights 11-20
- [ ] Click "Previous page" - table returns to flights 1-10
- [ ] Change page size dropdown - table updates accordingly

### Step 4: Identify Intentional Bugs

**Look for these indicators:**

1. **Null Airlines (NullPointerException)**
   - Rows with "N/A" in Airline column
   - Red background highlighting
   - "Fix This Bug" button enabled

2. **Negative Delays (IndexOutOfBoundsException)**
   - Rows with negative delay values (e.g., "-15 min")
   - Red background highlighting

3. **Zero Flight Count (ArithmeticException)**
   - Check statistics endpoint: `curl http://localhost:8080/api/flights/stats`
   - Look for division by zero scenario

4. **Invalid Date Formats (DateTimeParseException)**
   - Rows with malformed departure times
   - Red background highlighting

**Verification:**
- [ ] At least 5 flights with "N/A" airline visible
- [ ] At least 3 flights with negative delays visible
- [ ] Error rows have distinct red styling

### Step 5: Trigger Auto-Fix Workflow

**Steps:**
1. Find a flight with "N/A" airline (null pointer bug)
2. Click the "Fix This Bug" button in the Actions column
3. Wait 2-3 seconds for processing

**Expected Behavior:**
- [ ] Loading spinner appears
- [ ] Result panel expands below the table
- [ ] Panel shows:
  - Error type: "NullPointerException"
  - Fix snippet with null check code
  - GitHub PR link (or mock URL)
  - Slack notification status
  - Timestamp of fix generation

**Backend Console Verification:**
Check backend terminal for log output:
```
INFO: Analyzing error from production.log
INFO: Error type detected: NullPointerException
INFO: Generating fix for NullPointerException
INFO: Creating GitHub branch: prod-fix-1714574400000
INFO: Creating pull request...
INFO: PR created: https://github.com/...
INFO: Sending Slack notification...
INFO: Slack notification sent successfully
```

**Verification Points:**
- [ ] No exceptions in backend console
- [ ] All workflow steps complete
- [ ] PR URL is displayed (real or mock)
- [ ] Fix snippet contains null check logic

### Step 6: Verify GitHub Integration

**If GitHub token is configured:**
1. Click the PR link in the UI
2. Verify PR exists in GitHub repository
3. Check PR title: "Auto-fix by IBM Bob: NullPointerException"
4. Check PR description contains:
   - Error details
   - Fix snippet
   - Timestamp
   - Bob signature

**If GitHub token is NOT configured:**
- [ ] Mock URL displayed: `https://github.com/mock-org/mock-repo/pull/123`
- [ ] Console shows: "GitHub token not configured, using mock URL"

### Step 7: Verify Slack Integration

**If Slack webhook is configured:**
1. Check Slack channel for notification
2. Verify message contains:
   - Error type
   - Fix summary
   - PR link
   - Timestamp

**If Slack webhook is NOT configured:**
- [ ] Console shows: "Slack webhook not configured, logging to console"
- [ ] Console displays formatted Slack message JSON

### Step 8: Verify Log File

**Check production.log:**
```bash
cat production.log
```

**Expected Content:**
```json
{"timestamp":"2026-05-01T15:00:00","errorType":"NullPointerException","message":"Cannot invoke method on null object","stackTrace":["at com.hackathon.service.FlightService.getFlights(FlightService.java:45)"]}
```

**Verification Points:**
- [ ] Log file exists in project root
- [ ] Contains JSON-formatted error entries
- [ ] Includes timestamp, errorType, message, stackTrace

### Step 9: Simulate Additional Errors

**Trigger different error types:**

1. **Trigger IndexOutOfBoundsException:**
```bash
curl "http://localhost:8080/api/flights?page=100&size=10"
```
Expected: Error logged to production.log

2. **Trigger ArithmeticException:**
```bash
curl "http://localhost:8080/api/flights/stats"
```
Expected: Division by zero error (if no flights)

3. **Trigger DateTimeParseException:**
```bash
curl "http://localhost:8080/api/flights/parse-date?date=invalid-date"
```
Expected: Parse error logged

**Verification:**
- [ ] Each error appears in production.log
- [ ] Each error can be fixed via UI
- [ ] Different fix snippets generated for each error type

### Step 10: Test Scheduled Log Watcher

**Enable automatic monitoring:**
```bash
curl -X POST http://localhost:8080/api/auto-fix/enable-watcher
```

**Simulate error:**
```bash
echo '{"timestamp":"2026-05-01T15:00:00","errorType":"NullPointerException","message":"Test error","stackTrace":["at Test.java:1"]}' >> production.log
```

**Wait 5-10 seconds**

**Expected Behavior:**
- [ ] Backend console shows: "Processing new error from log file"
- [ ] Auto-fix workflow triggers automatically
- [ ] PR created without manual intervention
- [ ] Slack notification sent automatically

**Disable watcher:**
```bash
curl -X POST http://localhost:8080/api/auto-fix/disable-watcher
```

### Step 11: Run Unit Tests

```bash
cd backend
./gradlew test
```

**Expected Output:**
```
> Task :test

LogAnalyzerServiceTest > shouldDetectNullPointerException() PASSED
LogAnalyzerServiceTest > shouldDetectIndexOutOfBoundsException() PASSED
GitHubServiceTest > shouldGenerateValidBranchName() PASSED
...

BUILD SUCCESSFUL in Xs
```

**Verification Points:**
- [ ] All tests pass
- [ ] No test failures
- [ ] Test report generated in `build/reports/tests/test/index.html`

### Step 12: Run E2E Tests

```bash
npm install
npx playwright install
npm test
```

**Expected Output:**
```
Running 15 tests using 1 worker

✓ should load flight dashboard successfully (2s)
✓ should display system status badges (1s)
✓ should paginate through flights (2s)
✓ should identify flights with null airline (1s)
✓ should trigger auto-fix workflow (3s)
...

15 passed (25s)
```

**Verification Points:**
- [ ] All Playwright tests pass
- [ ] HTML report generated
- [ ] Screenshots captured for failures (if any)

### Step 13: Verify Bob Report Endpoint

**Fetch Bob's statistics:**
```bash
curl http://localhost:8080/api/bob-report
```

**Expected Response:**
```json
{
  "totalFixes": 5,
  "successfulFixes": 5,
  "failedFixes": 0,
  "events": [
    {
      "timestamp": "2026-05-01T15:00:00",
      "errorType": "NullPointerException",
      "fixGenerated": true,
      "prUrl": "https://github.com/...",
      "slackNotified": true
    }
  ]
}
```

**Verification Points:**
- [ ] totalFixes > 0
- [ ] successfulFixes matches number of triggered fixes
- [ ] events array contains fix details
- [ ] Each event has timestamp, errorType, prUrl

**Export report:**
```bash
curl http://localhost:8080/api/bob-report/export > bob-report.json
```

### Step 14: Code Quality Check

```bash
cd backend
./gradlew spotbugsMain
```

**Expected Output:**
```
> Task :spotbugsMain

BUILD SUCCESSFUL
```

**Verification Points:**
- [ ] SpotBugs analysis completes
- [ ] Intentional bugs are excluded (via spotbugs-exclude.xml)
- [ ] No critical bugs reported in production code

### Step 15: Record Playwright Test

**From the UI:**
1. Click "Record Test" button in toolbar
2. Perform actions on the dashboard
3. Check backend console for Playwright codegen output

**Or from command line:**
```bash
npm run test:codegen
```

**Verification Points:**
- [ ] Playwright inspector opens
- [ ] Actions are recorded
- [ ] Test code is generated

## 🎥 Video Demonstration

A 5-minute video demonstration is available showing:
1. Application startup
2. Dashboard overview
3. Bug identification
4. Auto-fix workflow
5. GitHub PR creation
6. Slack notification
7. Test execution

**Video Location:** `bob_sessions/demo-video.mp4`

## 📊 Success Criteria

### Minimum Requirements (Must Pass)
- [x] Backend starts without errors
- [x] Frontend loads and displays flights
- [x] At least one auto-fix workflow completes successfully
- [x] Fix snippet is generated and displayed
- [x] PR URL is created (real or mock)
- [x] Unit tests pass

### Full Demonstration (Ideal)
- [x] All 4 error types demonstrated
- [x] GitHub PR actually created
- [x] Slack notification actually sent
- [x] Scheduled log watcher works
- [x] E2E tests pass
- [x] Bob report shows statistics

## 🐛 Troubleshooting

### Backend won't start
- Check Java version: `java -version`
- Check port 8080 is free: `netstat -an | grep 8080`
- Check Gradle build: `./gradlew clean build`

### Frontend won't start
- Check Node version: `node --version`
- Clear npm cache: `npm cache clean --force`
- Delete node_modules: `rm -rf node_modules && npm install`

### Auto-fix doesn't work
- Check production.log exists and is writable
- Check backend console for error messages
- Verify API endpoint: `curl http://localhost:8080/api/auto-fix/status`

### Tests fail
- Ensure both backend and frontend are running
- Check ports 8080 and 4200 are accessible
- Review test output for specific failures

## 📞 Support

For questions or issues during verification, please refer to:
- README.md for setup instructions
- architecture.md for technical details
- Backend logs in console
- Frontend console in browser DevTools

---

**Thank you for verifying IBM Bob's auto-fix capabilities!** 🚀