# 🎭 Playwright Setup Guide

## Issue: Playwright Browsers Not Installed

If you see this error:
```
Executable doesn't exist at C:\Users\...\ms-playwright\chromium-1217\chrome-win64\chrome.exe
Please run the following command to download new browsers:
    npx playwright install
```

This means Playwright browsers need to be installed.

---

## 🔧 Solution Methods

### Method 1: Automated Script (Recommended)

Run the provided PowerShell script:

```powershell
.\install-playwright.ps1
```

If you get an execution policy error, see Method 2.

---

### Method 2: Manual Installation

#### Step 1: Enable PowerShell Scripts (One-time setup)

Open PowerShell as **Administrator** and run:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

When prompted, type `Y` and press Enter.

#### Step 2: Install Playwright Browsers

In your project directory, run:

```powershell
npx playwright install
```

This will download:
- Chromium (~150 MB)
- Firefox (~80 MB)
- WebKit (~60 MB)

**Total download:** ~290 MB

---

### Method 3: Install Specific Browser Only

If you only want Chromium (for faster download):

```powershell
npx playwright install chromium
```

---

## ✅ Verify Installation

After installation, verify browsers are installed:

```powershell
# Check Chromium
Test-Path "$env:LOCALAPPDATA\ms-playwright\chromium-*\chrome-win64\chrome.exe"

# Should return: True
```

---

## 🧪 Test Playwright

Run a simple test to verify everything works:

```powershell
# Run all tests
npm test

# Run specific test
npx playwright test tests/fix-verification.spec.ts

# Run with UI (interactive mode)
npx playwright test --ui

# Run in headed mode (see browser)
npx playwright test --headed
```

---

## 🐛 Common Issues

### Issue 1: "npx command not found"

**Solution:** Install Node.js from https://nodejs.org/

Verify installation:
```powershell
node --version
npm --version
```

### Issue 2: "Cannot load script because running scripts is disabled"

**Solution:** Run PowerShell as Administrator and execute:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Issue 3: Browsers installed but tests still fail

**Solution:** Clear Playwright cache and reinstall:
```powershell
# Clear cache
Remove-Item -Recurse -Force "$env:LOCALAPPDATA\ms-playwright"

# Reinstall
npx playwright install
```

### Issue 4: Network/Firewall blocking downloads

**Solution:** 
1. Check your firewall settings
2. Try using a VPN
3. Download browsers manually from Playwright CDN

---

## 📊 Browser Locations

After installation, browsers are stored at:

**Windows:**
```
C:\Users\<username>\AppData\Local\ms-playwright\
├── chromium-1217\
├── firefox-1508\
└── webkit-2104\
```

**Linux:**
```
~/.cache/ms-playwright/
```

**macOS:**
```
~/Library/Caches/ms-playwright/
```

---

## 🚀 Alternative: Skip Playwright Tests

If you can't install Playwright browsers, you can still run the application without E2E tests:

### Option 1: Disable Playwright in Backend

Edit [`application.properties`](backend/src/main/resources/application.properties):
```properties
playwright.enabled=false
```

### Option 2: Mock Playwright Service

The application will work without Playwright - it's only needed for automated test verification.

Core auto-fix workflow (error detection → fix generation → GitHub PR → Slack) works independently of Playwright.

---

## 📝 Manual Testing Without Playwright

You can manually verify the auto-fix workflow:

1. **Start Backend:** `cd backend && ./gradlew bootRun`
2. **Start Frontend:** `cd frontend && npm start`
3. **Open Dashboard:** http://localhost:4200
4. **Trigger Bug:** Click "Fix This Bug" button
5. **Verify Results:**
   - Check fix snippet is generated
   - Check PR URL is created
   - Check Slack notification (if configured)

---

## 🎯 For Demo/Judging

If Playwright installation fails during demo:

1. **Show the application working** (dashboard, auto-fix workflow)
2. **Explain Playwright's role** (automated E2E verification)
3. **Show test code** ([`fix-verification.spec.ts`](tests/fix-verification.spec.ts))
4. **Mention it's optional** (core workflow works without it)

The judges will understand that Playwright is an enhancement, not a requirement for the core auto-fix functionality.

---

## 📚 Additional Resources

- **Playwright Docs:** https://playwright.dev/
- **Installation Guide:** https://playwright.dev/docs/intro
- **Troubleshooting:** https://playwright.dev/docs/troubleshooting
- **System Requirements:** https://playwright.dev/docs/browsers

---

## 💡 Quick Reference

```powershell
# Install all browsers
npx playwright install

# Install specific browser
npx playwright install chromium

# Run tests
npm test

# Run with UI
npx playwright test --ui

# Run in debug mode
npx playwright test --debug

# Generate test report
npx playwright show-report

# Update Playwright
npm install -D @playwright/test@latest
npx playwright install
```

---

**Note:** Playwright is optional for the core auto-fix workflow. The main functionality (error detection, fix generation, GitHub PR, Slack notification) works independently.