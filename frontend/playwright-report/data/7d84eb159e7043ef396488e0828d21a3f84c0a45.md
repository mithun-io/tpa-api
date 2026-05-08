# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: production-ready.spec.js >> Production Readiness - Full Lifecycle >> Step 3: Admin Approval Workflow
- Location: tests\e2e\production-ready.spec.js:114:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: locator('tr').filter({ hasText: 'POL-READINESS-001' }).first()
Expected: visible
Timeout: 8000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 8000ms
  - waiting for locator('tr').filter({ hasText: 'POL-READINESS-001' }).first()

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
      - generic [ref=e30]:
        - generic [ref=e31]: Email Address
        - generic [ref=e32]:
          - img
          - textbox "Email Address" [ref=e33]:
            - /placeholder: you@example.com
      - generic [ref=e34]:
        - generic [ref=e35]: Password
        - generic [ref=e36]:
          - img
          - textbox "Password" [ref=e37]:
            - /placeholder: ••••••••
          - button "Show password" [ref=e38] [cursor=pointer]:
            - img [ref=e39]
      - link "Forgot password?" [ref=e43] [cursor=pointer]:
        - /url: /forgot-password
      - button "Sign In as Customer" [ref=e44] [cursor=pointer]:
        - generic [ref=e45]: Sign In as Customer
        - img [ref=e46]
    - paragraph [ref=e48]:
      - text: Don't have an account?
      - link "Create account" [ref=e49] [cursor=pointer]:
        - /url: /register
```

# Test source

```ts
  27  |     await page.click('#tab-FMG_ADMIN');
  28  |     await page.locator('#FMG_ADMIN-email').first().fill(ADMIN_EMAIL);
  29  |     await page.locator('#FMG_ADMIN-password').first().fill(ADMIN_PASS);
  30  |     await page.click('#login-submit-FMG_ADMIN');
  31  | 
  32  |     // Wait for redirect to admin dashboard
  33  |     await page.waitForURL('**/admin');
  34  |     await expect(page.locator('h1, h2')).toContainText(/Dashboard|Admin/i);
  35  | 
  36  |     // Check if the refresh button is visible
  37  |     const refreshBtn = page.locator('button:has-text("Refresh Data")');
  38  |     await expect(refreshBtn).toBeVisible();
  39  |     // Logout
  40  |     await page.click('button:has-text("Sign Out")');
  41  |     await expect(page).toHaveURL('/login');
  42  |   });
  43  | 
  44  |   test('Step 2: Customer Registration & Claim Submission', async ({ page }) => {
  45  |     // 1. Register a new customer (to ensure we have a clean state)
  46  |     await page.goto('/register');
  47  |     await page.fill('#reg-name', 'Test Customer');
  48  |     await page.fill('#reg-email', CUSTOMER_EMAIL);
  49  |     await page.fill('#reg-mobile', CUSTOMER_MOBILE);
  50  |     await page.fill('#reg-password', CUSTOMER_PASS);
  51  |     await page.selectOption('#reg-gender', 'MALE');
  52  |     await page.fill('#reg-dob', '1990-01-01');
  53  |     await page.fill('#reg-address', '123 Test St, Bangalore');
  54  |     await page.click('#register-submit');
  55  | 
  56  |     // Handle OTP
  57  |     await page.waitForSelector('.otp-inputs', { timeout: 10000 });
  58  |     await page.waitForTimeout(1000); // wait for redis
  59  |     const otp = execSync(`docker exec tpa-redis redis-cli GET "OTP:${CUSTOMER_EMAIL}"`).toString().trim().replace(/['"]/g, '');
  60  |     
  61  |     if (otp && otp.length === 6) {
  62  |         await page.fill('#otp-digit-0', otp[0]);
  63  |         await page.fill('#otp-digit-1', otp[1]);
  64  |         await page.fill('#otp-digit-2', otp[2]);
  65  |         await page.fill('#otp-digit-3', otp[3]);
  66  |         await page.fill('#otp-digit-4', otp[4]);
  67  |         await page.fill('#otp-digit-5', otp[5]);
  68  |         await page.click('#verify-otp-submit');
  69  |         
  70  |         await page.waitForSelector('.register-card--success');
  71  |         await page.click('#go-to-login');
  72  |     }
  73  |     
  74  |     await page.goto('/login');
  75  |     await page.click('#tab-CUSTOMER');
  76  |     await page.locator('#CUSTOMER-email').first().fill(CUSTOMER_EMAIL);
  77  |     await page.locator('#CUSTOMER-password').first().fill(CUSTOMER_PASS);
  78  |     await page.click('#login-submit-CUSTOMER');
  79  | 
  80  |     // Wait for redirect to dashboard after successful login
  81  |     await page.waitForURL('**/dashboard');
  82  |     
  83  |     // Proceed to upload
  84  |     await page.goto('/claims/upload');
  85  |     await expect(page).toHaveURL('/claims/upload');
  86  | 
  87  |     // Fill form
  88  |     await page.fill('input[name="policyNumber"]', 'POL-READINESS-001');
  89  |     await page.fill('input[name="claimedAmount"]', '5000');
  90  |     await page.fill('input[name="totalBillAmount"]', '5000');
  91  |     await page.fill('input[name="claimFormPatientName"]', 'Test Customer');
  92  |     await page.fill('input[name="claimFormHospitalName"]', 'City General');
  93  |     await page.fill('input[name="claimFormAdmissionDate"]', '2026-01-01');
  94  |     await page.fill('input[name="claimFormDischargeDate"]', '2026-01-05');
  95  | 
  96  |     // Go to Step 2
  97  |     await page.click('button:has-text("Next Step")');
  98  | 
  99  |     // Wait for Step 2 (file input appears)
  100 |     await page.waitForSelector('input[type="file"]');
  101 | 
  102 |     // Upload PDF
  103 |     const filePath = path.resolve('..', 'test-documents', 'claim_form.pdf');
  104 |     await page.setInputFiles('input[type="file"]', filePath);
  105 | 
  106 |     // Submit
  107 |     await page.click('button:has-text("Upload & Validate")');
  108 | 
  109 |     // Verify success redirect to claim detail
  110 |     await page.waitForURL(/.*\/claims\/\d+/);
  111 |     await expect(page.getByText('POL-READINESS-001')).toBeVisible();
  112 |   });
  113 | 
  114 |   test('Step 3: Admin Approval Workflow', async ({ page }) => {
  115 |     // Login as Admin
  116 |     await page.goto('/login');
  117 |     await page.click('#tab-FMG_ADMIN');
  118 |     await page.locator('#FMG_ADMIN-email').first().fill(ADMIN_EMAIL);
  119 |     await page.locator('#FMG_ADMIN-password').first().fill(ADMIN_PASS);
  120 |     await page.click('#login-submit-FMG_ADMIN');
  121 | 
  122 |     // Go to claims list
  123 |     await page.goto('/claims');
  124 |     
  125 |     // Find our claim
  126 |     const claimRow = page.locator('tr', { hasText: 'POL-READINESS-001' }).first();
> 127 |     await expect(claimRow).toBeVisible();
      |                            ^ Error: expect(locator).toBeVisible() failed
  128 |     
  129 |     // Click view/detail
  130 |     await claimRow.locator('button, a').first().click();
  131 | 
  132 |     // Verify detail page
  133 |     await expect(page).toHaveURL(/claims\/\d+/);
  134 |     
  135 |     // Approve the claim
  136 |     await page.click('button:has-text("Approve"), .approve-btn');
  137 |     
  138 |     // Verify status updated
  139 |     await expect(page.getByText(/ADMIN_APPROVED/i)).toBeVisible();
  140 |   });
  141 | });
  142 | 
```