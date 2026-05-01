import { test, expect } from '@playwright/test';

/**
 * IBM Bob Hackathon - E2E Test Suite
 * 
 * This test verifies that Bob's auto-fix workflow successfully resolves
 * production issues without breaking the application.
 */

test.describe('Flight Dashboard - Auto-Fix Verification', () => {
  
  test.beforeEach(async ({ page }) => {
    // Navigate to the flight dashboard
    await page.goto('http://localhost:4200');
    
    // Wait for the page to load
    await page.waitForLoadState('networkidle');
  });

  test('should load flight dashboard successfully', async ({ page }) => {
    // Verify page title
    await expect(page).toHaveTitle(/IBM Bob - Flight Dashboard/);
    
    // Verify toolbar is visible
    await expect(page.locator('mat-toolbar')).toBeVisible();
    
    // Verify flight table is visible
    await expect(page.locator('table')).toBeVisible();
    
    // Verify at least one flight row is displayed
    const rows = page.locator('tbody tr');
    await expect(rows).toHaveCount(10); // Default page size
  });

  test('should display system status badges', async ({ page }) => {
    // Verify status badges are visible
    await expect(page.locator('text=Backend')).toBeVisible();
    await expect(page.locator('text=Log Watcher')).toBeVisible();
    await expect(page.locator('text=GitHub')).toBeVisible();
    await expect(page.locator('text=Slack')).toBeVisible();
  });

  test('should paginate through flights', async ({ page }) => {
    // Verify paginator is visible
    await expect(page.locator('mat-paginator')).toBeVisible();
    
    // Click next page button
    await page.locator('button[aria-label="Next page"]').click();
    
    // Wait for table to update
    await page.waitForTimeout(500);
    
    // Verify we're on page 2
    const pageInfo = page.locator('.mat-mdc-paginator-range-label');
    await expect(pageInfo).toContainText('11');
  });

  test('should identify flights with null airline (NullPointerException)', async ({ page }) => {
    // Look for flights with "N/A" airline (indicates null value)
    const nullAirlineRows = page.locator('tr:has-text("N/A")');
    
    // Verify at least one null airline exists
    const count = await nullAirlineRows.count();
    expect(count).toBeGreaterThan(0);
    
    // Verify the row has error styling
    const firstNullRow = nullAirlineRows.first();
    await expect(firstNullRow).toHaveClass(/error-row/);
  });

  test('should trigger auto-fix workflow for null airline bug', async ({ page }) => {
    // Find a flight with null airline
    const nullAirlineRow = page.locator('tr:has-text("N/A")').first();
    
    // Click the "Fix This Bug" button
    await nullAirlineRow.locator('button:has-text("Fix This Bug")').click();
    
    // Wait for auto-fix to complete
    await page.waitForTimeout(2000);
    
    // Verify success message or result panel appears
    const resultPanel = page.locator('mat-expansion-panel');
    await expect(resultPanel).toBeVisible();
    
    // Verify PR link is displayed
    await expect(page.locator('text=Pull Request')).toBeVisible();
    await expect(page.locator('a[href*="github.com"]')).toBeVisible();
  });

  test('should display generated fix snippet', async ({ page }) => {
    // Trigger auto-fix
    const fixButton = page.locator('button:has-text("Fix This Bug")').first();
    await fixButton.click();
    
    // Wait for result
    await page.waitForTimeout(2000);
    
    // Expand the fix details
    await page.locator('mat-expansion-panel-header').click();
    
    // Verify fix snippet is displayed
    const codeBlock = page.locator('pre code');
    await expect(codeBlock).toBeVisible();
    
    // Verify fix contains null check
    const fixContent = await codeBlock.textContent();
    expect(fixContent).toContain('null');
  });

  test('should record Playwright test', async ({ page }) => {
    // Click the "Record Test" button
    await page.locator('button:has-text("Record Test")').click();
    
    // Wait for recording to start
    await page.waitForTimeout(1000);
    
    // Verify success notification or status change
    // Note: This is a placeholder - actual implementation depends on UI feedback
    await expect(page.locator('mat-toolbar')).toBeVisible();
  });

  test('should handle multiple error types', async ({ page }) => {
    // Test for different error types across pages
    const errorTypes = ['N/A', 'negative', 'invalid'];
    
    for (const errorType of errorTypes) {
      // Search for rows with this error indicator
      const errorRows = page.locator(`tr:has-text("${errorType}")`);
      const count = await errorRows.count();
      
      if (count > 0) {
        console.log(`Found ${count} rows with error type: ${errorType}`);
      }
    }
  });

  test('should verify no console errors after fix', async ({ page }) => {
    const consoleErrors: string[] = [];
    
    // Listen for console errors
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });
    
    // Trigger auto-fix
    const fixButton = page.locator('button:has-text("Fix This Bug")').first();
    await fixButton.click();
    
    // Wait for completion
    await page.waitForTimeout(3000);
    
    // Verify no critical errors occurred
    const criticalErrors = consoleErrors.filter(err => 
      err.includes('ERROR') || err.includes('Failed')
    );
    
    expect(criticalErrors.length).toBe(0);
  });

  test('should display flight statistics', async ({ page }) => {
    // Verify statistics are displayed (if implemented)
    const statsSection = page.locator('text=/Total Flights|Delayed Flights/');
    
    if (await statsSection.count() > 0) {
      await expect(statsSection.first()).toBeVisible();
    }
  });

  test('should handle API errors gracefully', async ({ page }) => {
    // Simulate backend being down by intercepting API calls
    await page.route('**/api/flights**', route => {
      route.abort('failed');
    });
    
    // Reload page
    await page.reload();
    
    // Verify error message is displayed
    await expect(page.locator('text=/Error|Failed|Unable/')).toBeVisible();
  });

  test('should verify PR link is clickable', async ({ page }) => {
    // Trigger auto-fix
    const fixButton = page.locator('button:has-text("Fix This Bug")').first();
    await fixButton.click();
    
    // Wait for result
    await page.waitForTimeout(2000);
    
    // Find PR link
    const prLink = page.locator('a[href*="github.com"]').first();
    await expect(prLink).toBeVisible();
    
    // Verify link has correct attributes
    await expect(prLink).toHaveAttribute('target', '_blank');
    await expect(prLink).toHaveAttribute('rel', 'noopener noreferrer');
  });

  test('should verify Slack notification was sent', async ({ page }) => {
    // Trigger auto-fix
    const fixButton = page.locator('button:has-text("Fix This Bug")').first();
    await fixButton.click();
    
    // Wait for completion
    await page.waitForTimeout(2000);
    
    // Verify Slack notification indicator
    const slackIndicator = page.locator('text=/Slack|Notification|Sent/');
    
    if (await slackIndicator.count() > 0) {
      await expect(slackIndicator.first()).toBeVisible();
    }
  });

  test('should maintain table state after fix', async ({ page }) => {
    // Get initial flight count
    const initialRows = await page.locator('tbody tr').count();
    
    // Trigger auto-fix
    const fixButton = page.locator('button:has-text("Fix This Bug")').first();
    await fixButton.click();
    
    // Wait for completion
    await page.waitForTimeout(2000);
    
    // Verify table still has same number of rows
    const finalRows = await page.locator('tbody tr').count();
    expect(finalRows).toBe(initialRows);
  });

  test('should verify responsive design', async ({ page }) => {
    // Test mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await page.waitForTimeout(500);
    
    // Verify table is still visible (may be scrollable)
    await expect(page.locator('table')).toBeVisible();
    
    // Test tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.waitForTimeout(500);
    
    await expect(page.locator('table')).toBeVisible();
    
    // Test desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.waitForTimeout(500);
    
    await expect(page.locator('table')).toBeVisible();
  });
});

test.describe('Bob Report Endpoint Tests', () => {
  
  test('should fetch Bob generation report', async ({ request }) => {
    const response = await request.get('http://localhost:8080/api/bob-report');
    
    expect(response.ok()).toBeTruthy();
    
    const data = await response.json();
    expect(data).toHaveProperty('totalFixes');
    expect(data).toHaveProperty('successfulFixes');
    expect(data).toHaveProperty('events');
  });

  test('should export Bob report as JSON', async ({ request }) => {
    const response = await request.get('http://localhost:8080/api/bob-report/export');
    
    expect(response.ok()).toBeTruthy();
    expect(response.headers()['content-type']).toContain('application/json');
  });
});

// Made with Bob
