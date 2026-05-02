# Configuration Guide - IBM Bob Hackathon Project

This guide explains how to configure GitHub and Slack integrations to make the auto-fix workflow fully functional.

## 🔧 Configuration Overview

The application requires two main integrations:
1. **GitHub** - For creating branches and pull requests with auto-generated fixes
2. **Slack** - For sending notifications about auto-fix results

## 📋 Prerequisites

- GitHub account with repository access
- Slack workspace with admin permissions
- Java 21 installed
- Node.js 18+ installed

---

## 🐙 GitHub Configuration

### Step 1: Create a GitHub Personal Access Token

1. Go to GitHub Settings: https://github.com/settings/tokens
2. Click **"Generate new token"** → **"Generate new token (classic)"**
3. Give it a descriptive name: `Bob Auto-Fix Token`
4. Set expiration (recommend 90 days for hackathon)
5. Select the following scopes:
   - ✅ `repo` (Full control of private repositories)
     - ✅ `repo:status`
     - ✅ `repo_deployment`
     - ✅ `public_repo`
     - ✅ `repo:invite`
   - ✅ `workflow` (Update GitHub Action workflows)
6. Click **"Generate token"**
7. **IMPORTANT:** Copy the token immediately (you won't see it again!)

### Step 2: Configure GitHub Token in Application

**Option A: Environment Variable (Recommended for Production)**
```bash
# Windows PowerShell
$env:GITHUB_TOKEN = "ghp_your_token_here"

# Windows CMD
set GITHUB_TOKEN=ghp_your_token_here

# Linux/Mac
export GITHUB_TOKEN=ghp_your_token_here
```

**Option B: Application Properties (For Development)**

Edit `backend/src/main/resources/application.properties`:
```properties
# GitHub Configuration
github.token=ghp_your_token_here
github.repo=your-username/your-repo-name
```

### Step 3: Verify GitHub Repository

Make sure your repository exists and you have push access:
```bash
# Test repository access
git remote -v

# Should show something like:
# origin  https://github.com/your-username/your-repo-name.git (fetch)
# origin  https://github.com/your-username/your-repo-name.git (push)
```

---

## 💬 Slack Configuration

### Step 1: Create a Slack Incoming Webhook

1. Go to your Slack workspace: https://api.slack.com/apps
2. Click **"Create New App"** → **"From scratch"**
3. Name it: `Bob Auto-Fix Notifier`
4. Select your workspace
5. Click **"Incoming Webhooks"** in the left sidebar
6. Toggle **"Activate Incoming Webhooks"** to ON
7. Click **"Add New Webhook to Workspace"**
8. Select the channel where you want notifications (e.g., `#bob-notifications`)
9. Click **"Allow"**
10. Copy the Webhook URL (looks like: `https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX`)

### Step 2: Configure Slack Webhook in Application

**Option A: Environment Variable (Recommended for Production)**
```bash
# Windows PowerShell
$env:SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"

# Windows CMD
set SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL

# Linux/Mac
export SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

**Option B: Application Properties (For Development)**

Edit `backend/src/main/resources/application.properties`:
```properties
# Slack Configuration
slack.webhook.url=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

### Step 3: Test Slack Integration

You can test the Slack webhook using curl:
```bash
curl -X POST -H 'Content-type: application/json' \
--data '{"text":"Test message from Bob Auto-Fix!"}' \
YOUR_WEBHOOK_URL
```

---

## 🎭 Playwright Configuration

Playwright is already configured in the project. To set it up:

### Step 1: Install Playwright Browsers
```bash
# Navigate to project root
cd Prod-Issue-Process-Automation-bob-Hackathon

# Install dependencies
npm install

# Install Playwright browsers
npx playwright install
```

### Step 2: Configure Playwright (Optional)

Edit `playwright.config.ts` if you need custom settings:
```typescript
export default defineConfig({
  testDir: './tests',
  timeout: 30000,
  use: {
    baseURL: 'http://localhost:4200',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  // Add more browsers if needed
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
  ],
});
```

---

## 🚀 Complete Configuration File

Here's a complete `application.properties` example:

```properties
# Server Configuration
server.port=8080
spring.application.name=bob-hackathon

# GitHub Configuration
github.token=${GITHUB_TOKEN:}
github.repo=${GITHUB_REPO:owner/repo}

# Slack Configuration
slack.webhook.url=${SLACK_WEBHOOK_URL:}

# Logging Configuration
logging.level.com.hackathon=DEBUG
logging.file.name=logs/production.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Scheduled Task Configuration
spring.task.scheduling.pool.size=2
```

---

## 🔐 Security Best Practices

### 1. Never Commit Secrets
Add to `.gitignore`:
```
# Secrets
application-local.properties
.env
*.key
*.pem
```

### 2. Use Environment Variables
Create a `.env` file (add to .gitignore):
```bash
GITHUB_TOKEN=ghp_your_token_here
GITHUB_REPO=your-username/your-repo-name
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

### 3. Use Spring Profiles
Create `application-local.properties` for local development:
```properties
github.token=ghp_your_local_token
slack.webhook.url=https://hooks.slack.com/services/YOUR/LOCAL/WEBHOOK
```

Run with profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## ✅ Verification Checklist

Before running the application, verify:

- [ ] GitHub token is set (check with `echo $GITHUB_TOKEN` or `echo %GITHUB_TOKEN%`)
- [ ] GitHub repository exists and is accessible
- [ ] Slack webhook URL is set
- [ ] Slack channel receives test messages
- [ ] Playwright browsers are installed (`npx playwright --version`)
- [ ] Java 21 is configured (`java -version`)
- [ ] Node.js is installed (`node --version`)

---

## 🎯 Quick Start Commands

### 1. Set Environment Variables (Windows PowerShell)
```powershell
$env:GITHUB_TOKEN = "ghp_your_token_here"
$env:GITHUB_REPO = "your-username/your-repo-name"
$env:SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
```

### 2. Start Backend
```bash
cd backend
./gradlew bootRun
```

### 3. Start Frontend (New Terminal)
```bash
cd frontend
npm install
npm start
```

### 4. Run Playwright Tests (New Terminal)
```bash
npm install
npx playwright install
npm test
```

---

## 🐛 Troubleshooting

### GitHub Issues

**Problem:** "Bad credentials" error
- **Solution:** Regenerate GitHub token with correct scopes

**Problem:** "Repository not found"
- **Solution:** Check repository name format: `owner/repo`

### Slack Issues

**Problem:** "Invalid webhook URL"
- **Solution:** Verify webhook URL starts with `https://hooks.slack.com/services/`

**Problem:** Messages not appearing
- **Solution:** Check channel permissions and webhook configuration

### Playwright Issues

**Problem:** "Browser not found"
- **Solution:** Run `npx playwright install`

**Problem:** Tests timing out
- **Solution:** Increase timeout in `playwright.config.ts`

---

## 📞 Support

For issues or questions:
1. Check the logs in `logs/production.log`
2. Verify environment variables are set
3. Test integrations individually
4. Review error messages in console

---

## 🎉 Ready to Go!

Once configured, the application will:
1. ✅ Detect errors in production logs
2. ✅ Generate fixes using Bob's AI
3. ✅ Create GitHub branches and PRs automatically
4. ✅ Send Slack notifications with results
5. ✅ Run Playwright tests to verify fixes

**Happy Hacking! 🚀**