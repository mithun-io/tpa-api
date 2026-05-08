import { test, expect } from '@playwright/test';
import fs from 'fs';
import path from 'path';

// Output Files
const REPORTS_DIR = path.join(process.cwd(), '..', 'reports', 'live-validation');
if (!fs.existsSync(REPORTS_DIR)) fs.mkdirSync(REPORTS_DIR, { recursive: true });

const SCREENSHOTS_DIR = path.join(REPORTS_DIR, 'screenshots');
if (!fs.existsSync(SCREENSHOTS_DIR)) fs.mkdirSync(SCREENSHOTS_DIR, { recursive: true });

const REPORT_FILE = path.join(REPORTS_DIR, 'browser-test-report.md');
const CONSOLE_LOG_FILE = path.join(REPORTS_DIR, 'console-errors.log');
const NETWORK_LOG_FILE = path.join(REPORTS_DIR, 'network-failures.log');

// Initialize files
fs.writeFileSync(REPORT_FILE, '# Browser Test Report\n\n');
fs.writeFileSync(CONSOLE_LOG_FILE, '');
fs.writeFileSync(NETWORK_LOG_FILE, '');

function logStep(role, step, status, details = '') {
  const line = `- **[${role}]** ${step}: ${status === 'PASS' ? '✅ PASS' : '❌ FAIL'} ${details ? `(${details})` : ''}\n`;
  fs.appendFileSync(REPORT_FILE, line);
  console.log(line.trim());
}

function logConsoleError(role, msg) {
  fs.appendFileSync(CONSOLE_LOG_FILE, `[${new Date().toISOString()}] [${role}] ${msg.type()}: ${msg.text()}\n`);
}

function logNetworkError(role, req, res) {
  if (res && res.status() >= 400 && !res.url().includes('login') && !res.url().includes('health')) {
    fs.appendFileSync(NETWORK_LOG_FILE, `[${new Date().toISOString()}] [${role}] ${req.method()} ${req.url()} - ${res.status()}\n`);
  }
}

const ROLES = [
  { role: 'CUSTOMER', name: 'Test Customer', email: `cust_${Date.now()}@test.com`, password: 'password123', phone: '1234567890' },
  { role: 'ADMIN', name: 'Admin', email: 'mithun-io@outlook.com', password: 'Qw3!@sPe:E1' },
  { role: 'CARRIER', name: 'Test Carrier', email: `carrier_${Date.now()}@test.com`, password: 'password123', companyName: 'Global Health Inc.' }
];

test.describe.configure({ mode: 'serial' });

test.describe('FULL END-TO-END LIVE VALIDATION', () => {

  test.beforeAll(async ({ browser }) => {
    // Register the dynamic Customer and Carrier to ensure they exist
    const page = await browser.newPage();
    const cust = ROLES[0];
    const carrier = ROLES[2];
    
    // Register Customer
    await page.goto('/register');
    await page.fill('input[name="name"]', cust.name);
    await page.fill('input[name="email"]', cust.email);
    await page.fill('input[name="password"]', cust.password);
    await page.fill('input[name="confirmPassword"]', cust.password);
    await page.fill('input[name="phoneNumber"]', cust.phone);
    await page.fill('input[name="dateOfBirth"]', '1990-01-01');
    await page.fill('textarea[name="address"]', '123 Main St');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/login', { timeout: 10000 }).catch(() => {});

    // Register Carrier
    await page.goto('/carrier-register');
    await page.fill('input[name="companyName"]', carrier.companyName);
    await page.fill('input[name="contactEmail"]', carrier.email);
    await page.fill('input[name="password"]', carrier.password);
    await page.fill('input[name="confirmPassword"]', carrier.password);
    await page.fill('input[name="registrationNumber"]', 'REG123456');
    await page.fill('input[name="taxId"]', 'TAX987654');
    await page.fill('textarea[name="headquartersAddress"]', '456 Carrier Ave');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/login', { timeout: 10000 }).catch(() => {});
    
    await page.close();
  });

  for (const user of ROLES) {
    test(`Validate ${user.role} Workflow`, async ({ page }) => {
      let testFailed = false;

      // 1. Attach listeners for Console & Network
      page.on('console', msg => {
        if (msg.type() === 'error') {
          logConsoleError(user.role, msg);
          testFailed = true;
        }
      });
      page.on('response', res => logNetworkError(user.role, res.request(), res));

      try {
        // AUTHENTICATION FLOW
        await page.goto('/');
        await page.waitForLoadState('networkidle');
        
        if (user.role === 'ADMIN') await page.click('#tab-FMG_ADMIN');
        if (user.role === 'CARRIER') await page.click('#tab-CARRIER_USER');
        if (user.role === 'CUSTOMER') await page.click('#tab-CUSTOMER');

        await page.fill('input[type="email"]', user.email);
        await page.fill('input[type="password"]', user.password);
        await page.click('button[type="submit"]');

        // Wait for redirect to dashboard
        try {
           await page.waitForURL('**/dashboard', { timeout: 10000 });
           await page.waitForLoadState('networkidle');
           await page.screenshot({ path: path.join(SCREENSHOTS_DIR, `${user.role}_dashboard.png`) });
           logStep(user.role, 'Login & Redirect to Dashboard', 'PASS');
        } catch(e) {
           await page.screenshot({ path: path.join(SCREENSHOTS_DIR, `${user.role}_login_fail.png`) });
           logStep(user.role, 'Login & Redirect to Dashboard', 'FAIL', 'Login failed or timeout');
           throw e;
        }

        // NAVIGATION VALIDATION
        const navLinks = await page.$$eval('aside nav a', anchors => anchors.map(a => ({ href: a.href, text: a.innerText })));
        
        for (const link of navLinks) {
          if (link.href.includes('logout') || link.href.includes('#')) continue;
          
          await page.goto(link.href);
          await page.waitForLoadState('networkidle', { timeout: 15000 }).catch(() => {});
          
          const title = await page.title();
          await page.screenshot({ path: path.join(SCREENSHOTS_DIR, `${user.role}_${link.text.replace(/\\s+/g, '_')}.png`) });
          
          // Check for blank page (root div is empty)
          const isBlank = await page.$eval('#root', el => el.innerHTML.trim() === '');
          if (isBlank) {
             logStep(user.role, `Navigate to ${link.text}`, 'FAIL', 'Blank page detected');
             testFailed = true;
          } else {
             logStep(user.role, `Navigate to ${link.text}`, 'PASS');
          }
        }

        if (testFailed) {
           logStep(user.role, 'OVERALL STATUS', 'FAIL', 'Errors detected in console or blank pages');
           test.fail(true, 'Test failed due to detected errors');
        } else {
           logStep(user.role, 'OVERALL STATUS', 'PASS');
        }

      } catch (error) {
         logStep(user.role, 'CRITICAL ERROR', 'FAIL', error.message);
         test.fail(true, error.message);
      } finally {
         // Logout
         await page.goto('/dashboard');
         await page.click('button:has-text("Sign Out")').catch(() => {});
         await page.waitForURL('**/login', { timeout: 5000 }).catch(() => {});
      }
    });
  }

});
