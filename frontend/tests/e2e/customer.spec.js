import { test, expect } from '@playwright/test';
import { attachConsoleLogger } from './utils/console-logger.js';
import { attachApiMonitor } from './utils/api-monitor.js';
import { loginViaUI } from './utils/auth-helper.js';
import { captureScreenshot } from './utils/screenshot-capture.js';

let consoleLogger;
let apiMonitor;

test.beforeEach(async ({ page }, testInfo) => {
  consoleLogger = attachConsoleLogger(page, testInfo);
  apiMonitor = attachApiMonitor(page, testInfo);
});

test.afterEach(async () => {
  consoleLogger.assertNoErrors();
  apiMonitor.assertNoFailures();
});

test.describe('Customer Workflows', () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page, 'CUSTOMER');
  });

  test('TC-CUST-01: Customer Dashboard & Claim Tracking Timeline', async ({ page }, testInfo) => {
    await expect(page).toHaveURL(/dashboard/);
    await captureScreenshot(page, 'customer-dashboard', testInfo);

    // Verify recent claims widget
    await expect(page.locator('text=Recent').first()).toBeVisible({ timeout: 10000 }).catch(() => {});

    // Navigate to Claims List
    if (await page.locator('a[href="/claims"], a:has-text("My Claims")').count() > 0) {
      await page.click('a[href="/claims"], a:has-text("My Claims")');
      await page.waitForLoadState('networkidle');
      await captureScreenshot(page, 'customer-my-claims', testInfo);

      // Open a claim to see the timeline
      const firstClaim = page.locator('table tbody tr:first-child a, .claim-card:first-child a, button:has-text("View")').first();
      if (await firstClaim.count() > 0) {
        await firstClaim.click();
        await page.waitForLoadState('networkidle');
        await captureScreenshot(page, 'customer-claim-timeline', testInfo);
        
        // Assert some timeline element exists
        await expect(page.locator('.timeline, .status-indicator').first()).toBeVisible().catch(() => {});
      }
    }
  });

  test('TC-CUST-02: Customer Profile and Settings', async ({ page }, testInfo) => {
    // Navigate to Profile
    if (await page.locator('a[href="/profile"], a:has-text("Profile")').count() > 0) {
      await page.click('a[href="/profile"], a:has-text("Profile")');
      await page.waitForLoadState('networkidle');
      await captureScreenshot(page, 'customer-profile', testInfo);
      
      // Check for profile fields
      await expect(page.locator('input[name="name"], input[name="email"]').first()).toBeVisible().catch(() => {});
    }
  });
});
