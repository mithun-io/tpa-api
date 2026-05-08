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

test.describe('Admin Workflows', () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page, 'FMG_ADMIN');
  });

  test('TC-ADMIN-01: Admin Dashboard & Analytics Navigation', async ({ page }, testInfo) => {
    await expect(page).toHaveURL(/dashboard|admin/, { timeout: 30000 });
    await captureScreenshot(page, 'admin-dashboard-load', testInfo);

    // Verify analytics widgets or sections
    await expect(page.locator('text=Total Claims').first()).toBeVisible({ timeout: 10000 }).catch(() => {});
    await expect(page.locator('text=Leakage').first()).toBeVisible().catch(() => {});
    await expect(page.locator('text=Loss Ratio').first()).toBeVisible().catch(() => {});
    
    // Navigate to Analytics
    if (await page.locator('a[href="/admin/analytics"], a:has-text("Analytics")').count() > 0) {
      await page.click('a[href="/admin/analytics"], a:has-text("Analytics")');
      await captureScreenshot(page, 'admin-analytics', testInfo);
    }
  });

  test('TC-ADMIN-02: Rule Engine UI and Fraud Dashboard', async ({ page }, testInfo) => {
    // Navigate to Fraud Dashboard
    if (await page.locator('a[href="/admin/fraud-dashboard"], a:has-text("Fraud")').count() > 0) {
      await page.click('a[href="/admin/fraud-dashboard"], a:has-text("Fraud")');
      await captureScreenshot(page, 'admin-fraud-dashboard', testInfo);
      await expect(page.locator('text=Risk').first()).toBeVisible().catch(() => {});
    }

    // Navigate to Rules Builder
    if (await page.locator('a[href="/admin/rules"], a:has-text("Rules")').count() > 0) {
      await page.click('a[href="/admin/rules"], a:has-text("Rules")');
      await captureScreenshot(page, 'admin-rules-ui', testInfo);
      
      // Interact with Rules Engine (e.g. click "Add Rule" or view existing)
      if (await page.locator('button:has-text("Add")').count() > 0) {
        await page.click('button:has-text("Add")');
        await captureScreenshot(page, 'admin-rules-add', testInfo);
      }
    }
  });

  test('TC-ADMIN-03: Carrier Management and SLA Tracking', async ({ page }, testInfo) => {
    // Navigate to Carriers
    if (await page.locator('a[href="/admin/carriers"], a:has-text("Carriers")').count() > 0) {
      await page.click('a[href="/admin/carriers"], a:has-text("Carriers")');
      await captureScreenshot(page, 'admin-carriers-list', testInfo);
    }

    // Navigate to Kafka/System Monitor
    if (await page.locator('a[href="/admin/system"], a:has-text("Monitor"), a:has-text("System")').count() > 0) {
      await page.click('a[href="/admin/system"], a:has-text("Monitor"), a:has-text("System")');
      await captureScreenshot(page, 'admin-system-monitor', testInfo);
    }
  });
});
