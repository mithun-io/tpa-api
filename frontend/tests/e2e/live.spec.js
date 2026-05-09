import { test, expect } from '@playwright/test';
import fs from 'fs';

const CONSOLE_ERRORS = [];
const NETWORK_FAILURES = [];

test.beforeEach(async ({ page }) => {
  page.on('console', msg => {
    if (msg.type() === 'error') {
      CONSOLE_ERRORS.push(`[${new Date().toISOString()}] ${msg.text()}`);
    }
  });

  page.on('response', response => {
    if (response.status() >= 400 && response.url().includes('/api/v1/')) {
      NETWORK_FAILURES.push(`[${new Date().toISOString()}] [${response.status()}] ${response.request().method()} ${response.url()}`);
    }
  });
});

test.afterAll(() => {
  fs.writeFileSync('../reports/console-errors.log', CONSOLE_ERRORS.join('\n'));
  fs.writeFileSync('../reports/network-failures.log', NETWORK_FAILURES.join('\n'));
  
  const apiReport = `# API Failure Report\n\nTotal Failures: ${NETWORK_FAILURES.length}\n\n` + NETWORK_FAILURES.join('\n');
  fs.writeFileSync('../reports/api-failure-report.md', apiReport);
  
  const uiReport = `# UI Validation Report\n\nTotal Console Errors: ${CONSOLE_ERRORS.length}\n\n` + CONSOLE_ERRORS.join('\n');
  fs.writeFileSync('../reports/ui-validation-report.md', uiReport);
});

test.describe('Full Live End-to-End Validation', () => {

  test('1. Customer Portal Validation', async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.click('button:has-text("Customer")');
    await page.fill('input[type="email"]', 'aerica.pancake@allfreemail.net');
    await page.fill('input[type="password"]', 'Test@123');
    await page.click('button:has-text("Sign In as Customer")');
    
    // Wait for Dashboard
    await page.waitForURL('/customer');
    await expect(page.locator('h1').filter({ hasText: 'Customer Dashboard' })).toBeVisible({ timeout: 15000 });
    
    // Navigate to Plan Details
    await page.click('text=Plan Details');
    await expect(page.locator('text=Coverage Information')).toBeVisible();

    // Navigate to Upload Claim
    await page.click('text=Upload Claim');
    await expect(page.locator('text=Submit New Claim')).toBeVisible();

    // Navigate to Claim History
    await page.click('text=Claim History');
    await expect(page.locator('text=All Claims')).toBeVisible();
    
    // Navigate to Notification Center
    await page.click('text=Notification Center');
    await expect(page.locator('text=Alerts')).toBeVisible();

    // Profile
    await page.click('text=Profile');
    await expect(page.locator('text=aerica.pancake@allfreemail.net')).toBeVisible();
    
    await page.click('button:has-text("Logout")');
  });

  test('2. Carrier Portal Validation', async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.click('button:has-text("Carrier")');
    await page.fill('input[type="email"]', 'zwlat96122@minitts.net');
    await page.fill('input[type="password"]', 'Test@123');
    await page.click('button:has-text("Sign In as Carrier")');
    
    // Wait for Dashboard
    await page.waitForURL('/carrier');
    await expect(page.locator('text=Strategic Hub')).toBeVisible({ timeout: 15000 });

    const carrierTabs = [
      { name: 'Leakage & Savings', text: 'Leakage / Overpayment' },
      { name: 'Loss Ratio Forecasting', text: 'Risk & Underwriting' },
      { name: 'Hospital Analytics', text: 'Hospital Analytics' },
      { name: 'Real-time SLA Tracker', text: 'SLA Tracking' },
      { name: 'Policy Heatmap', text: 'Policy' },
      { name: 'Fraud Visualization', text: 'Fraud Dashboard' },
      { name: 'Underwriting Hub', text: 'Risk & Underwriting' },
      { name: 'Customer Portfolio', text: 'Customer Portfolio' },
      { name: 'Bulk Settlement Portal', text: 'Bulk Settlement' },
      { name: 'Direct Query Management', text: 'Claims' }
    ];

    for (const tab of carrierTabs) {
      await page.click(`text=${tab.name}`);
      // Wait for network idle or text visibility
      await page.waitForTimeout(1000);
      await expect(page.locator('body')).toContainText(tab.text);
    }
    
    await page.click('button:has-text("Logout")');
  });

  test('3. Admin Portal Validation', async ({ page }) => {
    // Login as Admin
    await page.goto('/login');
    await page.click('button:has-text("Admin")');
    await page.fill('input[type="email"]', 'admin@tpa.com');
    await page.fill('input[type="password"]', 'Admin@123');
    await page.click('button:has-text("Sign In as Admin")');
    
    // Wait for Dashboard
    await page.waitForURL('/admin');
    await expect(page.locator('text=Operational Cockpit')).toBeVisible({ timeout: 15000 });

    const adminTabs = [
      { name: 'Intelligent Workbasket', text: 'Intelligent Workbasket' },
      { name: 'OCR Correction Queue', text: 'OCR' },
      { name: 'Full Claim Lineage', text: 'Audit' },
      { name: 'Visual Rule Builder', text: 'Rule Builder' },
      { name: 'System Intelligence', text: 'Intelligence' },
      { name: 'Medical Vault', text: 'Medical' },
      { name: 'SLA Breach Alerts', text: 'SLA' },
      { name: 'Kafka Monitor', text: 'Kafka' },
      { name: 'Compliance Center', text: 'Compliance' },
      { name: 'Blacklist Manager', text: 'Blacklist' }
    ];

    for (const tab of adminTabs) {
      await page.click(`text=${tab.name}`);
      await page.waitForTimeout(1000);
      await expect(page.locator('body')).toContainText(tab.text);
    }
    
    await page.click('button:has-text("Logout")');
  });
});
