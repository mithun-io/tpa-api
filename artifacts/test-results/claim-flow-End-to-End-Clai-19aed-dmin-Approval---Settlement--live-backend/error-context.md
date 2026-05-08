# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: claim-flow.spec.js >> End-to-End Claim Lifecycle Workflow >> TC-FLOW-01: Full Lifecycle (Customer Upload -> Admin Approval -> Settlement)
- Location: tests\e2e\claim-flow.spec.js:26:3

# Error details

```
Test timeout of 120000ms exceeded.
```

```
Error: page.click: Test timeout of 120000ms exceeded.
Call log:
  - waiting for locator('a[href="/admin/claims"], a:has-text("Claims"), a[href="/claims"]')

```

# Page snapshot

```yaml
- generic [ref=e3]:
  - complementary [ref=e4]:
    - generic [ref=e6]:
      - generic [ref=e8]: T
      - generic [ref=e9]:
        - paragraph [ref=e10]: TPA ClaimSys
        - paragraph [ref=e11]: Insurance Platform
    - generic [ref=e12]:
      - generic [ref=e14]: M
      - generic [ref=e15]:
        - paragraph [ref=e16]: mithun
        - paragraph [ref=e17]: FMG_ADMIN
    - navigation [ref=e18]:
      - paragraph [ref=e19]: Navigation
      - link "Dashboard" [ref=e20] [cursor=pointer]:
        - /url: /dashboard
        - img [ref=e21]
        - generic [ref=e26]: Dashboard
      - link "Profile" [ref=e27] [cursor=pointer]:
        - /url: /profile
        - img [ref=e28]
        - generic [ref=e31]: Profile
      - link "Admin Panel" [ref=e32] [cursor=pointer]:
        - /url: /admin
        - img [ref=e33]
        - generic [ref=e36]: Admin Panel
        - img [ref=e37]
      - link "Fraud Dashboard Admin" [ref=e39] [cursor=pointer]:
        - /url: /admin/fraud-dashboard
        - img [ref=e40]
        - generic [ref=e42]: Fraud Dashboard
        - generic [ref=e43]: Admin
      - link "Analytics Admin" [ref=e44] [cursor=pointer]:
        - /url: /analytics
        - img [ref=e45]
        - generic [ref=e47]: Analytics
        - generic [ref=e48]: Admin
    - button "Sign Out" [ref=e50] [cursor=pointer]:
      - img [ref=e51]
      - text: Sign Out
  - generic [ref=e54]:
    - banner [ref=e55]:
      - paragraph [ref=e57]: Welcome back, mithun
      - generic [ref=e58]:
        - generic [ref=e59]: FMG_ADMIN
        - button [ref=e61] [cursor=pointer]:
          - img [ref=e62]
        - button "Change Password" [ref=e65] [cursor=pointer]:
          - img [ref=e66]
    - main [ref=e69]:
      - generic [ref=e71]:
        - generic [ref=e72]:
          - generic [ref=e73]:
            - heading "Admin Dashboard" [level=1] [ref=e74]
            - paragraph [ref=e75]: Manage users and review insurance claims
          - generic [ref=e76]:
            - button "Notifications" [ref=e78] [cursor=pointer]:
              - img [ref=e79]
            - button "Refresh Data" [ref=e82] [cursor=pointer]:
              - img [ref=e83]
              - text: Refresh Data
        - generic [ref=e88]:
          - button "Claims Management" [ref=e89] [cursor=pointer]:
            - img [ref=e90]
            - text: Claims Management
          - button "User Management" [ref=e93] [cursor=pointer]:
            - img [ref=e94]
            - text: User Management
          - button "System Monitoring" [ref=e99] [cursor=pointer]:
            - img [ref=e100]
            - text: System Monitoring
          - button "Carriers" [ref=e102] [cursor=pointer]:
            - img [ref=e103]
            - text: Carriers
        - generic [ref=e108]:
          - generic [ref=e109]:
            - generic [ref=e110]:
              - img [ref=e111]
              - textbox "Search by username…" [ref=e114]
            - generic [ref=e115]:
              - img
              - combobox [ref=e116]:
                - option "All Statuses" [selected]
                - option "SUBMITTED"
                - option "AI_VALIDATED"
                - option "UNDER_REVIEW"
                - option "ADMIN_APPROVED"
                - option "CARRIER_APPROVED"
                - option "REJECTED"
                - option "PAYMENT_PENDING"
                - option "SETTLED"
          - table [ref=e119]:
            - rowgroup [ref=e120]:
              - row "Claim ID Policy No Amount Date Status Actions" [ref=e121]:
                - columnheader "Claim ID" [ref=e122]
                - columnheader "Policy No" [ref=e123]
                - columnheader "Amount" [ref=e124]
                - columnheader "Date" [ref=e125]
                - columnheader "Status" [ref=e126]
                - columnheader "Actions" [ref=e127]
            - rowgroup [ref=e128]:
              - row "#6 POL-TEST-123 $92,500.00 May 8, 2026 Submitted View Analyze Assign Carrier" [ref=e129]:
                - cell "#6" [ref=e130]
                - cell "POL-TEST-123" [ref=e131]
                - cell "$92,500.00" [ref=e132]
                - cell "May 8, 2026" [ref=e133]
                - cell "Submitted" [ref=e134]:
                  - generic [ref=e135]: Submitted
                - cell "View Analyze Assign Carrier" [ref=e137]:
                  - generic [ref=e138]:
                    - button "View" [ref=e139] [cursor=pointer]:
                      - img [ref=e140]
                      - text: View
                    - button "Analyze" [ref=e143] [cursor=pointer]:
                      - img [ref=e144]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e147] [cursor=pointer]:
                      - img [ref=e148]
                      - text: Assign Carrier
              - row "#5 POL-READINESS-001 $5,000.00 May 8, 2026 Under Review View Analyze Assign Carrier Approve Reject" [ref=e153]:
                - cell "#5" [ref=e154]
                - cell "POL-READINESS-001" [ref=e155]
                - cell "$5,000.00" [ref=e156]
                - cell "May 8, 2026" [ref=e157]
                - cell "Under Review" [ref=e158]:
                  - generic [ref=e159]: Under Review
                - cell "View Analyze Assign Carrier Approve Reject" [ref=e161]:
                  - generic [ref=e162]:
                    - button "View" [ref=e163] [cursor=pointer]:
                      - img [ref=e164]
                      - text: View
                    - button "Analyze" [ref=e167] [cursor=pointer]:
                      - img [ref=e168]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e171] [cursor=pointer]:
                      - img [ref=e172]
                      - text: Assign Carrier
                    - button "Approve" [ref=e177] [cursor=pointer]:
                      - img [ref=e178]
                      - text: Approve
                    - button "Reject" [ref=e181] [cursor=pointer]:
                      - img [ref=e182]
                      - text: Reject
              - row "#4 POL-READINESS-001 $5,000.00 May 8, 2026 Under Review View Analyze Assign Carrier Approve Reject" [ref=e186]:
                - cell "#4" [ref=e187]
                - cell "POL-READINESS-001" [ref=e188]
                - cell "$5,000.00" [ref=e189]
                - cell "May 8, 2026" [ref=e190]
                - cell "Under Review" [ref=e191]:
                  - generic [ref=e192]: Under Review
                - cell "View Analyze Assign Carrier Approve Reject" [ref=e194]:
                  - generic [ref=e195]:
                    - button "View" [ref=e196] [cursor=pointer]:
                      - img [ref=e197]
                      - text: View
                    - button "Analyze" [ref=e200] [cursor=pointer]:
                      - img [ref=e201]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e204] [cursor=pointer]:
                      - img [ref=e205]
                      - text: Assign Carrier
                    - button "Approve" [ref=e210] [cursor=pointer]:
                      - img [ref=e211]
                      - text: Approve
                    - button "Reject" [ref=e214] [cursor=pointer]:
                      - img [ref=e215]
                      - text: Reject
              - row "#3 POL-READINESS-001 $5,000.00 May 8, 2026 Submitted View Analyze Assign Carrier" [ref=e219]:
                - cell "#3" [ref=e220]
                - cell "POL-READINESS-001" [ref=e221]
                - cell "$5,000.00" [ref=e222]
                - cell "May 8, 2026" [ref=e223]
                - cell "Submitted" [ref=e224]:
                  - generic [ref=e225]: Submitted
                - cell "View Analyze Assign Carrier" [ref=e227]:
                  - generic [ref=e228]:
                    - button "View" [ref=e229] [cursor=pointer]:
                      - img [ref=e230]
                      - text: View
                    - button "Analyze" [ref=e233] [cursor=pointer]:
                      - img [ref=e234]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e237] [cursor=pointer]:
                      - img [ref=e238]
                      - text: Assign Carrier
              - row "#2 POL-READINESS-001 $5,000.00 May 8, 2026 Submitted View Analyze Assign Carrier" [ref=e243]:
                - cell "#2" [ref=e244]
                - cell "POL-READINESS-001" [ref=e245]
                - cell "$5,000.00" [ref=e246]
                - cell "May 8, 2026" [ref=e247]
                - cell "Submitted" [ref=e248]:
                  - generic [ref=e249]: Submitted
                - cell "View Analyze Assign Carrier" [ref=e251]:
                  - generic [ref=e252]:
                    - button "View" [ref=e253] [cursor=pointer]:
                      - img [ref=e254]
                      - text: View
                    - button "Analyze" [ref=e257] [cursor=pointer]:
                      - img [ref=e258]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e261] [cursor=pointer]:
                      - img [ref=e262]
                      - text: Assign Carrier
              - row "#1 POL-889977 $1,200.00 May 7, 2026 Under Review View Analyze Assign Carrier Approve Reject" [ref=e267]:
                - cell "#1" [ref=e268]
                - cell "POL-889977" [ref=e269]
                - cell "$1,200.00" [ref=e270]
                - cell "May 7, 2026" [ref=e271]
                - cell "Under Review" [ref=e272]:
                  - generic [ref=e273]: Under Review
                - cell "View Analyze Assign Carrier Approve Reject" [ref=e275]:
                  - generic [ref=e276]:
                    - button "View" [ref=e277] [cursor=pointer]:
                      - img [ref=e278]
                      - text: View
                    - button "Analyze" [ref=e281] [cursor=pointer]:
                      - img [ref=e282]
                      - text: Analyze
                    - button "Assign Carrier" [ref=e285] [cursor=pointer]:
                      - img [ref=e286]
                      - text: Assign Carrier
                    - button "Approve" [ref=e291] [cursor=pointer]:
                      - img [ref=e292]
                      - text: Approve
                    - button "Reject" [ref=e295] [cursor=pointer]:
                      - img [ref=e296]
                      - text: Reject
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | import { attachConsoleLogger } from './utils/console-logger.js';
  3   | import { attachApiMonitor } from './utils/api-monitor.js';
  4   | import { loginViaUI, CREDENTIALS } from './utils/auth-helper.js';
  5   | import { captureScreenshot } from './utils/screenshot-capture.js';
  6   | import path from 'path';
  7   | import { fileURLToPath } from 'url';
  8   | 
  9   | const __dirname = path.dirname(fileURLToPath(import.meta.url));
  10  | 
  11  | let consoleLogger;
  12  | let apiMonitor;
  13  | 
  14  | test.beforeEach(async ({ page }, testInfo) => {
  15  |   consoleLogger = attachConsoleLogger(page, testInfo);
  16  |   apiMonitor = attachApiMonitor(page, testInfo);
  17  | });
  18  | 
  19  | test.afterEach(async () => {
  20  |   consoleLogger.assertNoErrors();
  21  |   apiMonitor.assertNoFailures();
  22  | });
  23  | 
  24  | test.describe('End-to-End Claim Lifecycle Workflow', () => {
  25  | 
  26  |   test('TC-FLOW-01: Full Lifecycle (Customer Upload -> Admin Approval -> Settlement)', async ({ page, browser }, testInfo) => {
  27  |     test.setTimeout(120000); // This flow takes time
  28  | 
  29  |     // Step 1: Customer Login
  30  |     await loginViaUI(page, 'CUSTOMER');
  31  |     await captureScreenshot(page, 'customer-dashboard', testInfo);
  32  |     
  33  |     // Step 2: Navigate to Upload Claim
  34  |     const uploadNav = page.locator('button:has-text("New Claim"), button:has-text("Start New Claim"), a[href="/claims/upload"]');
  35  |     if (await uploadNav.count() > 0) {
  36  |       await uploadNav.first().click();
  37  |     } else {
  38  |       await page.goto('/claims/upload');
  39  |     }
  40  |     await page.waitForURL(/\/claims\/upload/, { timeout: 10000 });
  41  |     
  42  |     // Step 3: Fill Claim Form
  43  |     await page.fill('input[name="policyNumber"]', 'POL-TEST-123');
  44  |     await page.fill('input[name="claimFormPatientName"]', 'Jane Smith');
  45  |     await page.fill('input[name="claimFormHospitalName"]', 'City General Hospital');
  46  |     await page.fill('input[name="claimFormAdmissionDate"]', '2026-05-01');
  47  |     await page.fill('input[name="claimFormDischargeDate"]', '2026-05-05');
  48  |     await page.fill('input[name="claimedAmount"]', '92500');
  49  |     await page.fill('input[name="totalBillAmount"]', '100000');
  50  |     await page.fill('input[name="diagnosis"]', 'Essential Hypertension');
  51  | 
  52  |     // Submit Step 1
  53  |     await page.click('button:has-text("Next Step")');
  54  |     
  55  |     // Wait for Step 2
  56  |     await page.waitForSelector('text="Upload Documents"', { timeout: 10000 });
  57  | 
  58  |     const docPath = path.join(__dirname, 'test-documents', 'hospital-bill.pdf');
  59  |     try {
  60  |       await page.setInputFiles('input[type="file"]', docPath);
  61  |     } catch (e) {
  62  |       console.log('No file input found or not interactable. Proceeding...');
  63  |     }
  64  |     
  65  |     await page.click('button:has-text("Upload & Validate")');
  66  |     
  67  |     // Wait for success indicator or redirect
  68  |     await page.waitForURL(/\/claims\/\d+/, { timeout: 30000 }).catch(() => {});
  69  |     
  70  |     await captureScreenshot(page, 'customer-claim-submitted', testInfo);
  71  | 
  72  |     // Logout
  73  |     await page.click('#sidebar-logout, button:has-text("Sign Out")');
  74  |     await page.waitForURL(/\/login/, { timeout: 10000 });
  75  | 
  76  |     // Step 4: Admin Login
  77  |     await loginViaUI(page, 'FMG_ADMIN');
  78  |     await captureScreenshot(page, 'admin-dashboard', testInfo);
  79  | 
  80  |     // Step 5: Navigate to Claims
> 81  |     await page.click('a[href="/admin/claims"], a:has-text("Claims"), a[href="/claims"]');
      |                ^ Error: page.click: Test timeout of 120000ms exceeded.
  82  |     
  83  |     await captureScreenshot(page, 'admin-claims-list', testInfo);
  84  | 
  85  |     // Click the first claim in the list
  86  |     const firstClaimLink = page.locator('table tbody tr:first-child a, .claim-card:first-child a, table tbody tr:first-child button').first();
  87  |     if (await firstClaimLink.count() > 0) {
  88  |       await firstClaimLink.click();
  89  |       
  90  |       await captureScreenshot(page, 'admin-claim-details', testInfo);
  91  | 
  92  |       // AI Validation / Medical Vault check
  93  |       const validateBtn = page.locator('button:has-text("Run AI Validation"), button:has-text("Validate Medical")');
  94  |       if (await validateBtn.count() > 0) {
  95  |         await validateBtn.click();
  96  |         
  97  |         await captureScreenshot(page, 'admin-ai-validation', testInfo);
  98  |       }
  99  | 
  100 |       // Approve Claim
  101 |       const approveBtn = page.locator('button:has-text("Approve"), button:has-text("Mark Approved")');
  102 |       if (await approveBtn.count() > 0) {
  103 |         await approveBtn.first().click();
  104 |         
  105 |         await captureScreenshot(page, 'admin-claim-approved', testInfo);
  106 |       }
  107 |       
  108 |       // Settle Payment
  109 |       const settleBtn = page.locator('button:has-text("Initiate Payment"), button:has-text("Settle")');
  110 |       if (await settleBtn.count() > 0) {
  111 |         await settleBtn.first().click();
  112 |         
  113 |         await captureScreenshot(page, 'admin-claim-settled', testInfo);
  114 |       }
  115 |     }
  116 |   });
  117 | 
  118 | });
  119 | 
```