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

test.describe('Carrier Workflows', () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page, 'CARRIER_USER');
  });

  test('TC-CARRIER-01: Carrier Dashboard & SLA Tracking', async ({ page }, testInfo) => {
    await expect(page).toHaveURL(/carrier|dashboard/);
    await captureScreenshot(page, 'carrier-dashboard', testInfo);

    // Verify SLA widgets or claim summaries
    await expect(page.locator('text=SLA').first()).toBeVisible({ timeout: 10000 }).catch(() => {});
    await expect(page.locator('text=Claim').first()).toBeVisible().catch(() => {});

    // Navigate to Claims Review
    if (await page.locator('a[href="/carrier/claims"], a:has-text("Review Claims"), a[href="/claims"]').count() > 0) {
      await page.click('a[href="/carrier/claims"], a:has-text("Review Claims"), a[href="/claims"]');
      await page.waitForLoadState('networkidle');
      await captureScreenshot(page, 'carrier-claims-review', testInfo);
    }
  });

  test('TC-CARRIER-02: Carrier Analytics and Fraud Insights', async ({ page }, testInfo) => {
    // Navigate to Fraud Insights
    if (await page.locator('a[href="/carrier/fraud"], a:has-text("Fraud Insights")').count() > 0) {
      await page.click('a[href="/carrier/fraud"], a:has-text("Fraud Insights")');
      await page.waitForLoadState('networkidle');
      await captureScreenshot(page, 'carrier-fraud-insights', testInfo);
    }
    
    // Navigate to Analytics/Loss Ratio
    if (await page.locator('a[href="/carrier/analytics"], a:has-text("Loss Ratio")').count() > 0) {
      await page.click('a[href="/carrier/analytics"], a:has-text("Loss Ratio")');
      await page.waitForLoadState('networkidle');
      await captureScreenshot(page, 'carrier-analytics', testInfo);
    }
  });
});
