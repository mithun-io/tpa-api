// @ts-check
import { test, expect } from '@playwright/test';
import path from 'path';
import { execSync } from 'child_process';

/**
 * PRODUCTION READINESS E2E TEST
 * Validates the full lifecycle of a claim:
 * 1. Admin Login & Dashboard Check
 * 2. Customer Login & Claim Submission (with PDF)
 * 3. Admin Claim Review & Approval
 */

const ADMIN_EMAIL = 'mithun-io@outlook.com';
const ADMIN_PASS = 'Qw3!@sPe:E1';

// We'll create a test customer for this run with a unique email and mobile to prevent 'already registered' errors.
const UNIQUE_ID = Date.now();
const CUSTOMER_EMAIL = `customer-${UNIQUE_ID}@tpa.com`;
const CUSTOMER_MOBILE = `9${Math.floor(Math.random() * 1000000000).toString().padStart(9, '0')}`;
const CUSTOMER_PASS = 'Qw3!@sPe:E1'; // Must match pattern

test.describe('Production Readiness - Full Lifecycle', () => {

  test('Step 1: Admin Login and Dashboard Validation', async ({ page }) => {
    await page.goto('/login');
    await page.click('#tab-FMG_ADMIN');
    await page.locator('#FMG_ADMIN-email').first().fill(ADMIN_EMAIL);
    await page.locator('#FMG_ADMIN-password').first().fill(ADMIN_PASS);
    await page.click('#login-submit-FMG_ADMIN');

    // Wait for redirect to admin dashboard
    await page.waitForURL('**/admin');
    await expect(page.locator('h1, h2')).toContainText(/Dashboard|Admin/i);

    // Check if the refresh button is visible
    const refreshBtn = page.locator('button:has-text("Refresh Data")');
    await expect(refreshBtn).toBeVisible();
    // Logout
    await page.click('button:has-text("Sign Out")');
    await expect(page).toHaveURL('/login');
  });

  test('Step 2: Customer Registration & Claim Submission', async ({ page }) => {
    // 1. Register a new customer (to ensure we have a clean state)
    await page.goto('/register');
    await page.fill('#reg-name', 'Test Customer');
    await page.fill('#reg-email', CUSTOMER_EMAIL);
    await page.fill('#reg-mobile', CUSTOMER_MOBILE);
    await page.fill('#reg-password', CUSTOMER_PASS);
    await page.selectOption('#reg-gender', 'MALE');
    await page.fill('#reg-dob', '1990-01-01');
    await page.fill('#reg-address', '123 Test St, Bangalore');
    await page.click('#register-submit');

    // Handle OTP
    await page.waitForSelector('.otp-inputs', { timeout: 10000 });
    await page.waitForTimeout(1000); // wait for redis
    const otp = execSync(`docker exec tpa-redis redis-cli GET "OTP:${CUSTOMER_EMAIL}"`).toString().trim().replace(/['"]/g, '');
    
    if (otp && otp.length === 6) {
        await page.fill('#otp-digit-0', otp[0]);
        await page.fill('#otp-digit-1', otp[1]);
        await page.fill('#otp-digit-2', otp[2]);
        await page.fill('#otp-digit-3', otp[3]);
        await page.fill('#otp-digit-4', otp[4]);
        await page.fill('#otp-digit-5', otp[5]);
        await page.click('#verify-otp-submit');
        
        await page.waitForSelector('.register-card--success');
        await page.click('#go-to-login');
    }
    
    await page.goto('/login');
    await page.click('#tab-CUSTOMER');
    await page.locator('#CUSTOMER-email').first().fill(CUSTOMER_EMAIL);
    await page.locator('#CUSTOMER-password').first().fill(CUSTOMER_PASS);
    await page.click('#login-submit-CUSTOMER');

    // Wait for redirect to dashboard after successful login
    await page.waitForURL('**/dashboard');
    
    // Proceed to upload
    await page.goto('/claims/upload');
    await expect(page).toHaveURL('/claims/upload');

    // Fill form
    await page.fill('input[name="policyNumber"]', 'POL-READINESS-001');
    await page.fill('input[name="claimedAmount"]', '5000');
    await page.fill('input[name="totalBillAmount"]', '5000');
    await page.fill('input[name="claimFormPatientName"]', 'Test Customer');
    await page.fill('input[name="claimFormHospitalName"]', 'City General');
    await page.fill('input[name="claimFormAdmissionDate"]', '2026-01-01');
    await page.fill('input[name="claimFormDischargeDate"]', '2026-01-05');

    // Go to Step 2
    await page.click('button:has-text("Next Step")');

    // Wait for Step 2 (file input appears)
    await page.waitForSelector('input[type="file"]');

    // Upload PDF
    const filePath = path.resolve('..', 'test-documents', 'claim_form.pdf');
    await page.setInputFiles('input[type="file"]', filePath);

    // Submit
    await page.click('button:has-text("Upload & Validate")');

    // Verify success redirect to claim detail
    await page.waitForURL(/.*\/claims\/\d+/);
    await expect(page.getByText('POL-READINESS-001')).toBeVisible();
  });

  test('Step 3: Admin Approval Workflow', async ({ page }) => {
    // Login as Admin
    await page.goto('/login');
    await page.click('#tab-FMG_ADMIN');
    await page.locator('#FMG_ADMIN-email').first().fill(ADMIN_EMAIL);
    await page.locator('#FMG_ADMIN-password').first().fill(ADMIN_PASS);
    await page.click('#login-submit-FMG_ADMIN');

    // Go to claims list
    await page.goto('/claims');
    
    // Find our claim
    const claimRow = page.locator('tr', { hasText: 'POL-READINESS-001' }).first();
    await expect(claimRow).toBeVisible();
    
    // Click view/detail
    await claimRow.locator('button, a').first().click();

    // Verify detail page
    await expect(page).toHaveURL(/claims\/\d+/);
    
    // Approve the claim
    await page.click('button:has-text("Approve"), .approve-btn');
    
    // Verify status updated
    await expect(page.getByText(/ADMIN_APPROVED/i)).toBeVisible();
  });
});
