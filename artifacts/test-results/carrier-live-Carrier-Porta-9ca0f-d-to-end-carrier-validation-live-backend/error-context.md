# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: carrier-live.spec.js >> Carrier Portal Live Validation >> Full end-to-end carrier validation
- Location: tests\e2e\carrier-live.spec.js:8:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: locator('text=Strategic Hub').first()
Expected: visible
Timeout: 20000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 20000ms
  - waiting for locator('text=Strategic Hub').first()

```

# Page snapshot

```yaml
- generic [ref=e4]:
  - generic [ref=e5]:
    - generic [ref=e7]: T
    - heading "TPA ClaimSys" [level=1] [ref=e8]
    - paragraph [ref=e9]: Intelligent Insurance Claims Platform
  - tablist "Login role" [ref=e10]:
    - tab "Customer" [selected] [ref=e11] [cursor=pointer]:
      - img [ref=e12]
      - generic [ref=e15]: Customer
    - tab "Admin" [ref=e16] [cursor=pointer]:
      - img [ref=e17]
      - generic [ref=e19]: Admin
    - tab "Carrier" [ref=e20] [cursor=pointer]:
      - img [ref=e21]
      - generic [ref=e26]: Carrier
  - tabpanel "Customer" [ref=e27]:
    - paragraph [ref=e28]: Access your insurance claims
    - generic [ref=e29]:
      - alert [ref=e30]:
        - img [ref=e31]
        - generic [ref=e33]: Login failed. Please try again.
      - generic [ref=e34]:
        - generic [ref=e35]: Email Address
        - generic [ref=e36]:
          - img
          - textbox "Email Address" [ref=e37]:
            - /placeholder: you@example.com
            - text: pwgcy57804@minitts.net
      - generic [ref=e38]:
        - generic [ref=e39]: Password
        - generic [ref=e40]:
          - img
          - textbox "Password" [ref=e41]:
            - /placeholder: ••••••••
            - text: Test@123
          - button "Show password" [ref=e42] [cursor=pointer]:
            - img [ref=e43]
      - link "Forgot password?" [ref=e47] [cursor=pointer]:
        - /url: /forgot-password
      - button "Sign In as Customer" [ref=e48] [cursor=pointer]:
        - generic [ref=e49]: Sign In as Customer
        - img [ref=e50]
    - paragraph [ref=e52]:
      - text: Don't have an account?
      - link "Create account" [ref=e53] [cursor=pointer]:
        - /url: /register
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | import fs from 'fs';
  3  | 
  4  | test.describe('Carrier Portal Live Validation', () => {
  5  |   let consoleErrors = [];
  6  |   let failedRequests = [];
  7  | 
  8  |   test('Full end-to-end carrier validation', async ({ page }) => {
  9  |     page.on('console', msg => {
  10 |       if (msg.type() === 'error') {
  11 |         consoleErrors.push(`[Console Error] ${msg.text()}`);
  12 |       }
  13 |     });
  14 | 
  15 |     page.on('response', response => {
  16 |       if (!response.ok() && response.url().includes('/api/v1/')) {
  17 |         failedRequests.push(`[Network Error] ${response.status()} ${response.url()}`);
  18 |       }
  19 |     });
  20 | 
  21 |     await page.goto('http://localhost:3000');
  22 |     await page.waitForLoadState('networkidle');
  23 | 
  24 |     await page.fill('input[type="email"], input[name="email"]', 'pwgcy57804@minitts.net');
  25 |     await page.fill('input[type="password"], input[name="password"]', 'Test@123');
  26 |     await page.click('button[type="submit"], button:has-text("Sign In"), button:has-text("Login")');
  27 | 
  28 |     await page.waitForLoadState('networkidle');
> 29 |     await expect(page.locator('text=Strategic Hub').first()).toBeVisible({ timeout: 20000 });
     |                                                              ^ Error: expect(locator).toBeVisible() failed
  30 |     
  31 |     const screenshotDir = 'C:\\Users\\mithun\\.gemini\\antigravity\\brain\\e695002e-bda3-4dd9-9445-6a5be867551b';
  32 | 
  33 |     const visitAndCapture = async (menuName, fileName) => {
  34 |       await page.click(`text="${menuName}"`);
  35 |       await page.waitForLoadState('networkidle');
  36 |       await page.waitForTimeout(1000);
  37 |       await page.screenshot({ path: `${screenshotDir}\\${fileName}.png`, fullPage: true });
  38 |     };
  39 | 
  40 |     await page.screenshot({ path: `${screenshotDir}\\01_Dashboard.png`, fullPage: true });
  41 | 
  42 |     await visitAndCapture('Leakage & Savings', '02_Leakage_Savings');
  43 |     await visitAndCapture('Loss Ratio Forecasting', '03_Loss_Ratio');
  44 |     await visitAndCapture('Reinsurance Export', '04_Reinsurance_Export');
  45 |     await visitAndCapture('Hospital Analytics', '05_Hospital_Analytics');
  46 |     await visitAndCapture('Real-time SLA Tracker', '06_SLA_Tracker');
  47 |     await visitAndCapture('Policy Heatmap', '07_Policy_Heatmap');
  48 |     await visitAndCapture('PPN Configuration', '08_PPN_Config');
  49 |     await visitAndCapture('Fraud Visualization', '09_Fraud_Dashboard');
  50 |     await visitAndCapture('Underwriting Hub', '10_Underwriting_Hub');
  51 |     await visitAndCapture('Customer Portfolio', '11_Customer_Portfolio');
  52 |     await visitAndCapture('Bulk Settlement Portal', '12_Bulk_Settlement');
  53 |     await visitAndCapture('Direct Query Management', '13_Direct_Query');
  54 | 
  55 |     fs.writeFileSync(`${screenshotDir}\\console-errors.log`, consoleErrors.join('\n'));
  56 |     fs.writeFileSync(`${screenshotDir}\\failed-network-calls.log`, failedRequests.join('\n'));
  57 |     
  58 |     const report = `# Browser Validation Report\n\n` +
  59 |       `- **Date:** ${new Date().toISOString()}\n` +
  60 |       `- **Total Console Errors:** ${consoleErrors.length}\n` +
  61 |       `- **Total Network Errors:** ${failedRequests.length}\n\n` +
  62 |       `All pages visited successfully.`;
  63 |     fs.writeFileSync(`${screenshotDir}\\browser-validation-report.md`, report);
  64 | 
  65 |     expect(consoleErrors.length).toBe(0);
  66 |     expect(failedRequests.length).toBe(0);
  67 |   });
  68 | });
  69 | 
```