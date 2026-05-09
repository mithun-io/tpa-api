import { test, expect } from '@playwright/test';
import fs from 'fs';

test.describe('Carrier Portal Live Validation', () => {
  let consoleErrors = [];
  let failedRequests = [];

  test('Full end-to-end carrier validation', async ({ page }) => {
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(`[Console Error] ${msg.text()}`);
      }
    });

    page.on('response', response => {
      if (!response.ok() && response.url().includes('/api/v1/')) {
        failedRequests.push(`[Network Error] ${response.status()} ${response.url()}`);
      }
    });

    await page.goto('http://localhost:3000');
    await page.waitForLoadState('networkidle');

    await page.fill('input[type="email"], input[name="email"]', 'pwgcy57804@minitts.net');
    await page.fill('input[type="password"], input[name="password"]', 'Test@123');
    await page.click('button[type="submit"], button:has-text("Sign In"), button:has-text("Login")');

    await page.waitForLoadState('networkidle');
    await expect(page.locator('text=Strategic Hub').first()).toBeVisible({ timeout: 20000 });
    
    const screenshotDir = 'C:\\Users\\mithun\\.gemini\\antigravity\\brain\\e695002e-bda3-4dd9-9445-6a5be867551b';

    const visitAndCapture = async (menuName, fileName) => {
      await page.click(`text="${menuName}"`);
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1000);
      await page.screenshot({ path: `${screenshotDir}\\${fileName}.png`, fullPage: true });
    };

    await page.screenshot({ path: `${screenshotDir}\\01_Dashboard.png`, fullPage: true });

    await visitAndCapture('Leakage & Savings', '02_Leakage_Savings');
    await visitAndCapture('Loss Ratio Forecasting', '03_Loss_Ratio');
    await visitAndCapture('Reinsurance Export', '04_Reinsurance_Export');
    await visitAndCapture('Hospital Analytics', '05_Hospital_Analytics');
    await visitAndCapture('Real-time SLA Tracker', '06_SLA_Tracker');
    await visitAndCapture('Policy Heatmap', '07_Policy_Heatmap');
    await visitAndCapture('PPN Configuration', '08_PPN_Config');
    await visitAndCapture('Fraud Visualization', '09_Fraud_Dashboard');
    await visitAndCapture('Underwriting Hub', '10_Underwriting_Hub');
    await visitAndCapture('Customer Portfolio', '11_Customer_Portfolio');
    await visitAndCapture('Bulk Settlement Portal', '12_Bulk_Settlement');
    await visitAndCapture('Direct Query Management', '13_Direct_Query');

    fs.writeFileSync(`${screenshotDir}\\console-errors.log`, consoleErrors.join('\n'));
    fs.writeFileSync(`${screenshotDir}\\failed-network-calls.log`, failedRequests.join('\n'));
    
    const report = `# Browser Validation Report\n\n` +
      `- **Date:** ${new Date().toISOString()}\n` +
      `- **Total Console Errors:** ${consoleErrors.length}\n` +
      `- **Total Network Errors:** ${failedRequests.length}\n\n` +
      `All pages visited successfully.`;
    fs.writeFileSync(`${screenshotDir}\\browser-validation-report.md`, report);

    expect(consoleErrors.length).toBe(0);
    expect(failedRequests.length).toBe(0);
  });
});
