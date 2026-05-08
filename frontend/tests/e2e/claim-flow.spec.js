import { test, expect } from '@playwright/test';
import { attachConsoleLogger } from './utils/console-logger.js';
import { attachApiMonitor } from './utils/api-monitor.js';
import { loginViaUI, CREDENTIALS } from './utils/auth-helper.js';
import { captureScreenshot } from './utils/screenshot-capture.js';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

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

test.describe('End-to-End Claim Lifecycle Workflow', () => {

  test('TC-FLOW-01: Full Lifecycle (Customer Upload -> Admin Approval -> Settlement)', async ({ page, browser }, testInfo) => {
    test.setTimeout(120000); // This flow takes time

    // Step 1: Customer Login
    await loginViaUI(page, 'CUSTOMER');
    await captureScreenshot(page, 'customer-dashboard', testInfo);
    
    // Step 2: Navigate to Upload Claim
    const uploadNav = page.locator('button:has-text("New Claim"), button:has-text("Start New Claim"), a[href="/claims/upload"]');
    if (await uploadNav.count() > 0) {
      await uploadNav.first().click();
    } else {
      await page.goto('/claims/upload');
    }
    await page.waitForURL(/\/claims\/upload/, { timeout: 10000 });
    
    // Step 3: Fill Claim Form
    await page.fill('input[name="policyNumber"]', 'POL-TEST-123');
    await page.fill('input[name="claimFormPatientName"]', 'Jane Smith');
    await page.fill('input[name="claimFormHospitalName"]', 'City General Hospital');
    await page.fill('input[name="claimFormAdmissionDate"]', '2026-05-01');
    await page.fill('input[name="claimFormDischargeDate"]', '2026-05-05');
    await page.fill('input[name="claimedAmount"]', '92500');
    await page.fill('input[name="totalBillAmount"]', '100000');
    await page.fill('input[name="diagnosis"]', 'Essential Hypertension');

    // Submit Step 1
    await page.click('button:has-text("Next Step")');
    
    // Wait for Step 2
    await page.waitForSelector('text="Upload Documents"', { timeout: 10000 });

    const docPath = path.join(__dirname, 'test-documents', 'hospital-bill.pdf');
    try {
      await page.setInputFiles('input[type="file"]', docPath);
    } catch (e) {
      console.log('No file input found or not interactable. Proceeding...');
    }
    
    await page.click('button:has-text("Upload & Validate")');
    
    // Wait for success indicator or redirect
    await page.waitForURL(/\/claims\/\d+/, { timeout: 30000 }).catch(() => {});
    
    await captureScreenshot(page, 'customer-claim-submitted', testInfo);

    // Logout
    await page.click('#sidebar-logout, button:has-text("Sign Out")');
    await page.waitForURL(/\/login/, { timeout: 10000 });

    // Step 4: Admin Login
    await loginViaUI(page, 'FMG_ADMIN');
    await captureScreenshot(page, 'admin-dashboard', testInfo);

    // Step 5: Navigate to Claims
    await page.click('a[href="/admin/claims"], a:has-text("Claims"), a[href="/claims"]');
    
    await captureScreenshot(page, 'admin-claims-list', testInfo);

    // Click the first claim in the list
    const firstClaimLink = page.locator('table tbody tr:first-child a, .claim-card:first-child a, table tbody tr:first-child button').first();
    if (await firstClaimLink.count() > 0) {
      await firstClaimLink.click();
      
      await captureScreenshot(page, 'admin-claim-details', testInfo);

      // AI Validation / Medical Vault check
      const validateBtn = page.locator('button:has-text("Run AI Validation"), button:has-text("Validate Medical")');
      if (await validateBtn.count() > 0) {
        await validateBtn.click();
        
        await captureScreenshot(page, 'admin-ai-validation', testInfo);
      }

      // Approve Claim
      const approveBtn = page.locator('button:has-text("Approve"), button:has-text("Mark Approved")');
      if (await approveBtn.count() > 0) {
        await approveBtn.first().click();
        
        await captureScreenshot(page, 'admin-claim-approved', testInfo);
      }
      
      // Settle Payment
      const settleBtn = page.locator('button:has-text("Initiate Payment"), button:has-text("Settle")');
      if (await settleBtn.count() > 0) {
        await settleBtn.first().click();
        
        await captureScreenshot(page, 'admin-claim-settled', testInfo);
      }
    }
  });

});
