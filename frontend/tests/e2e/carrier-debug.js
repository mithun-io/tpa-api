/**
 * Carrier Portal Live Debug Script — Playwright (headed Chrome, ESM)
 * Run: node tests/e2e/carrier-debug.js
 */
import { chromium } from 'playwright';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const BASE  = 'http://localhost:3000';
const EMAIL = 'zwlat96122@minitts.net';
const PASS  = 'Test@123';

const CARRIER_ROUTES = [
  { path: '/carrier',                    label: 'Strategic Hub (Dashboard)' },
  { path: '/carrier/leakage',            label: 'Leakage & Savings' },
  { path: '/carrier/hospital-analytics', label: 'Hospital Analytics' },
  { path: '/carrier/sla-tracker',        label: 'SLA Tracker' },
  { path: '/carrier/bulk-approvals',     label: 'Bulk Settlement Portal' },
  { path: '/carrier/policy-performance', label: 'Policy Performance' },
  { path: '/carrier/fraud-dashboard',    label: 'Fraud Intelligence' },
  { path: '/carrier/query-center',       label: 'Query Management' },
  { path: '/carrier/export-center',      label: 'Export Center' },
  { path: '/carrier/loss-ratio',         label: 'Loss Ratio Forecasting' },
  { path: '/carrier/underwriting',       label: 'Underwriting Hub' },
  { path: '/carrier/portfolio',          label: 'Customer Portfolio' },
];

const SCREENSHOTS_DIR = path.join(__dirname, 'screenshots');
const REPORT_DIR      = path.join(__dirname, '..', '..', 'reports');

[SCREENSHOTS_DIR, REPORT_DIR].forEach(d => fs.mkdirSync(d, { recursive: true }));

const wait = ms => new Promise(r => setTimeout(r, ms));

async function main() {
  const browser = await chromium.launch({ headless: false, slowMo: 200 });
  const ctx     = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page    = await ctx.newPage();

  const consoleErrors  = [];
  const failedRequests = [];

  page.on('console', msg => {
    if (msg.type() === 'error') {
      const entry = { text: msg.text(), url: page.url() };
      consoleErrors.push(entry);
      console.log(`  [JS ERR] ${msg.text().substring(0, 120)}`);
    }
  });

  page.on('requestfailed', req => {
    failedRequests.push({ url: req.url(), method: req.method(), failure: req.failure()?.errorText, pageUrl: page.url() });
    console.log(`  [NET FAIL] ${req.method()} ${req.url()}`);
  });

  page.on('response', async res => {
    const s = res.status();
    if (s >= 400) {
      let body = '';
      try { body = (await res.text()).substring(0, 200); } catch (_) {}
      failedRequests.push({ url: res.url(), method: res.request().method(), status: s, body, pageUrl: page.url() });
      console.log(`  [HTTP ${s}] ${res.request().method()} ${res.url().replace('http://localhost:3000', '')}`);
    }
  });

  // ─────────────────────────────────────────────────────────────────────────
  // STEP 1: LOGIN
  // ─────────────────────────────────────────────────────────────────────────
  console.log('\n══ STEP 1: LOGIN ══');
  await page.goto(`${BASE}/login`);
  await page.waitForLoadState('domcontentloaded');
  await wait(1500);

  try {
    await page.locator('input[type="email"], input[name="email"]').first().fill(EMAIL);
    await page.locator('input[type="password"]').first().fill(PASS);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '01-login-filled.png') });
    await page.locator('button[type="submit"]').click();
    await page.waitForURL(url => !url.toString().includes('/login'), { timeout: 15000 });
  } catch (e) {
    console.log('  Login timeout or error:', e.message);
  }

  await wait(2500);
  const loginUrl = page.url();
  console.log(`  Redirect → ${loginUrl}`);
  await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '02-post-login.png') });

  if (loginUrl.includes('/login')) {
    console.error('  ❌ LOGIN FAILED');
    await browser.close();
    return;
  }
  console.log('  ✅ Login OK');

  // ─────────────────────────────────────────────────────────────────────────
  // STEP 2: ALL CARRIER ROUTES
  // ─────────────────────────────────────────────────────────────────────────
  console.log('\n══ STEP 2: CARRIER ROUTE SCAN ══');
  const routeResults = [];

  for (const route of CARRIER_ROUTES) {
    const errsBefore = consoleErrors.length;
    const failsBefore = failedRequests.length;

    await page.goto(`${BASE}${route.path}`);
    await wait(3500);   // give SPA + API calls time to settle

    const ssFile = route.path.replace(/\//g, '_').replace(/^_/, '') + '.png';
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, ssFile), fullPage: true });

    const body         = (await page.textContent('body').catch(() => '')).replace(/\s+/g, ' ');
    const newErrors    = consoleErrors.slice(errsBefore);
    const newFails     = failedRequests.slice(failsBefore);
    const hasErrorUI   = /failed to load|cannot read|uncaught|typeerror/i.test(body);
    const hasSpinnerUI = body.length < 300;
    const isBlank      = body.trim().length < 80;

    let status = 'OK';
    if (isBlank)       status = 'BLANK';
    else if (hasErrorUI) status = 'ERROR';
    else if (hasSpinnerUI) status = 'SPINNER';

    routeResults.push({
      route: route.path, label: route.label, status,
      jsErrors: newErrors.map(e => e.text),
      apiErrors: newFails.map(f => `[${f.status || 'NET'}] ${f.method} ${f.url.replace(BASE,'')}`),
      bodyPreview: body.substring(0, 150)
    });

    const icon = status === 'OK' ? '✅' : status === 'BLANK' ? '⬜' : '❌';
    console.log(`  ${icon} ${route.path.padEnd(35)} [${status}]  JS:${newErrors.length}  API-err:${newFails.length}`);
    if (newErrors.length) newErrors.forEach(e => console.log(`      ↳ ${e.text.substring(0, 100)}`));
    if (newFails.length)  newFails.forEach(f  => console.log(`      ↳ ${f.status||'NET'} ${f.method} ${f.url.replace(BASE,'')}`));
  }

  // ─────────────────────────────────────────────────────────────────────────
  // STEP 3: WORKFLOW — Validate / Flag on dashboard
  // ─────────────────────────────────────────────────────────────────────────
  console.log('\n══ STEP 3: WORKFLOW TEST ══');
  await page.goto(`${BASE}/carrier`);
  await wait(3500);

  let validateResult = 'NO_CLAIMS_VISIBLE';
  let flagResult     = 'NO_CLAIMS_VISIBLE';

  // Expand first row
  const expandBtn = page.locator('table tbody tr button').first();
  if (await expandBtn.count() > 0) {
    await expandBtn.click();
    await wait(1000);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, 'workflow-01-expanded.png') });
  }

  // Validate
  const valBtn = page.locator('button:has-text("Validate")').first();
  if (await valBtn.count() > 0) {
    const failsBefore = failedRequests.length;
    const errsBefore  = consoleErrors.length;
    await valBtn.click();
    await wait(2500);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, 'workflow-02-validate.png') });
    const newFails = failedRequests.slice(failsBefore);
    const newErrs  = consoleErrors.slice(errsBefore);
    validateResult = newFails.length === 0 && newErrs.length === 0 ? 'SUCCESS ✅' : `ERROR ❌ ${newFails.map(f=>f.status).join(',')}`;
    console.log(`  Validate: ${validateResult}`);
  }

  // Flag
  const flagBtn = page.locator('button:has-text("Flag")').first();
  if (await flagBtn.count() > 0) {
    const failsBefore = failedRequests.length;
    await flagBtn.click();
    await wait(2500);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, 'workflow-03-flag.png') });
    const newFails = failedRequests.slice(failsBefore);
    flagResult = newFails.length === 0 ? 'SUCCESS ✅' : `ERROR ❌ ${newFails.map(f=>f.status).join(',')}`;
    console.log(`  Flag:     ${flagResult}`);
  }

  // Final screenshot of dashboard
  await page.screenshot({ path: path.join(SCREENSHOTS_DIR, 'workflow-04-final.png') });

  // ─────────────────────────────────────────────────────────────────────────
  // REPORTS
  // ─────────────────────────────────────────────────────────────────────────
  console.log('\n══ GENERATING REPORTS ══');

  const uniqueJsErrors = [...new Set(consoleErrors.map(e => e.text))];

  // ── carrier-bug-report.md ─────────────────────────────────────────────────
  const bugMd = `# 🚨 Carrier Portal Bug Report
Generated: ${new Date().toISOString()}
Application: ${BASE}

---

## ✅ Login
| Field | Value |
|---|---|
| Status | SUCCESS |
| Email | ${EMAIL} |
| Redirect URL | ${loginUrl} |

---

## 📊 Route Status Summary

| Route | Label | Status | JS Errors | API Errors |
|---|---|---|---|---|
${routeResults.map(r =>
  `| \`${r.route}\` | ${r.label} | **${r.status}** | ${r.jsErrors.length} | ${r.apiErrors.length} |`
).join('\n')}

---

## ❌ Broken Pages Detail

${routeResults.filter(r => r.status !== 'OK').map(r => `
### \`${r.route}\` — ${r.label} [${r.status}]

**JS Console Errors:**
${r.jsErrors.length ? r.jsErrors.map(e => `- \`${e}\``).join('\n') : '- None'}

**Failed API Calls:**
${r.apiErrors.length ? r.apiErrors.map(e => `- ${e}`).join('\n') : '- None'}

**Page Body Preview:** ${r.bodyPreview}
`).join('\n---\n') || '\n✅ All pages loaded successfully.\n'}

---

## 🔄 Workflow Tests
| Action | Result |
|---|---|
| Validate Claim | ${validateResult} |
| Flag Claim | ${flagResult} |

---

## 🖥️ All JS Console Errors (${uniqueJsErrors.length} unique)
${uniqueJsErrors.map((e, i) => `${i+1}. \`${e}\``).join('\n') || 'None detected ✅'}

---

## 🌐 All Failed API Calls (${failedRequests.length} total)
${failedRequests.length
  ? failedRequests.map(f =>
      `- **[${f.status || 'NET_FAIL'}]** \`${f.method}\` \`${f.url.replace(BASE, '')}\``
    ).join('\n')
  : 'None ✅'}
`;

  fs.writeFileSync(path.join(REPORT_DIR, 'carrier-bug-report.md'), bugMd);

  // ── api-failure-log.json ──────────────────────────────────────────────────
  fs.writeFileSync(
    path.join(REPORT_DIR, 'api-failure-log.json'),
    JSON.stringify({ generatedAt: new Date().toISOString(), total: failedRequests.length, failures: failedRequests }, null, 2)
  );

  // ── console-errors.log ────────────────────────────────────────────────────
  fs.writeFileSync(
    path.join(REPORT_DIR, 'console-errors.log'),
    [
      `Console Error Log — ${new Date().toISOString()}`,
      `Total: ${consoleErrors.length} | Unique: ${uniqueJsErrors.length}`,
      '─'.repeat(60),
      ...consoleErrors.map((e, i) => `[${i+1}] PAGE: ${e.url}\n    ${e.text}`)
    ].join('\n')
  );

  // ── fix-recommendation.md ─────────────────────────────────────────────────
  const fixMd = `# 🔧 Fix Recommendations — Carrier Portal

Generated: ${new Date().toISOString()}

## Fixes Already Applied

### 1. CarrierAnalytics.jsx
- **Bug**: Used raw \`axios\` (no auth token → 401 errors)
- **Bug**: Called non-existent \`/analytics/carrier?carrierId=1\` endpoint
- **Bug**: Crashed with \`TypeError\` when \`data\` was \`null\`
- **Fix**: Replaced with \`axiosInstance\`, calls real \`/analytics/dashboard\`, \`/analytics/leakage\`, \`/analytics/hospitals\`, \`/analytics/sla/performance\`; added full null-safe rendering + error state

### 2. BulkSettlement.jsx
- **Bug**: Used raw \`axios\` (no auth token)
- **Bug**: Used wrong field names: \`c.id\` → \`c.claimId\`, \`c.patientName\` → \`c.patient?.name\`, \`c.riskScore\` → \`c.fraud?.riskScore\`, \`c.claimStatus\` → \`c.status\`
- **Bug**: Fetched from \`/api/v1/claims\` (public endpoint, wrong data shape)
- **Bug**: ApiResponse unwrapping was \`response.data\` (flat) but needed \`response.data.data\` (nested)
- **Fix**: Replaced with \`axiosInstance\`, correct endpoint \`/carrier/claims\`, fixed all field names, correct unwrapping

### 3. UnderwritingIntelligence.jsx
- **Bug**: ApiResponse unwrapping: \`res.data?.content || res.data\` → should be \`res.data?.data\`
- **Bug**: \`c.policyNumber.substring(0,5)\` crashed when \`policyNumber\` was \`null\`
- **Bug**: \`c.aiFraudScore\` doesn't exist — correct field is \`c.fraud?.riskScore\`
- **Fix**: Correct unwrapping, null-safe policyNumber, correct fraud score field, added error state

### 4. CustomerPortfolio.jsx
- **Bug**: ApiResponse unwrapping wrong: \`res.data?.content || res.data\`
- **Bug**: \`c.patientName\` doesn't exist — correct field is \`c.patient?.name\`
- **Bug**: \`c.aiFraudScore\` doesn't exist — correct field is \`c.fraud?.riskScore\`
- **Bug**: Division by zero in avgRiskScore when totalClaims=0
- **Fix**: Correct unwrapping \`res.data?.data\`, correct field names, null-safe division, added retry error state

### 5. RuleBuilder.jsx, SystemMonitor.jsx, QueryThread.jsx
- **Bug**: All used raw \`axios\` without auth token → 401 errors
- **Fix**: Replaced all with \`axiosInstance\` from \`../api/axios\`, removed hardcoded \`/api/v1\` prefix (already set in baseURL)

---

## API Contract Reference (CarrierController)

All carrier endpoints require \`Authorization: Bearer <JWT>\` with role \`CARRIER_USER\`.

| Endpoint | Method | Purpose |
|---|---|---|
| \`/api/v1/carrier/claims\` | GET | Get assigned claims → \`ApiResponse<List<CarrierClaimDetailResponse>>\` |
| \`/api/v1/carrier/claims/{id}/validate\` | PATCH | Validate policy |
| \`/api/v1/carrier/claims/{id}/approve\` | PATCH | Approve claim |
| \`/api/v1/carrier/claims/{id}/reject\` | PATCH | Reject claim |
| \`/api/v1/carrier/claims/{id}/flag\` | PATCH | Flag as suspicious |
| \`/api/v1/carrier/claims/{id}/remark\` | PATCH | Add remark |
| \`/api/v1/carrier/claims/{id}/ai-analyze\` | POST | Run AI analysis |
| \`/api/v1/analytics/dashboard\` | GET | General analytics |
| \`/api/v1/analytics/leakage\` | GET | Leakage data |
| \`/api/v1/analytics/hospitals\` | GET | Hospital analytics |
| \`/api/v1/analytics/sla/performance\` | GET | SLA metrics |
| \`/api/v1/fraud/carrier/dashboard\` | GET | Fraud dashboard |
| \`/api/v1/claims/bulk/approve\` | POST | Bulk approve |
`;

  fs.writeFileSync(path.join(REPORT_DIR, 'fix-recommendation.md'), fixMd);

  // ── Summary ───────────────────────────────────────────────────────────────
  console.log('\n══════════════════════════════════════════════');
  console.log('        CARRIER PORTAL DEBUG COMPLETE         ');
  console.log('══════════════════════════════════════════════');
  console.log(`Login:            ✅ SUCCESS`);
  console.log(`Routes scanned:   ${routeResults.length}`);
  console.log(`Routes OK:        ${routeResults.filter(r=>r.status==='OK').length}`);
  console.log(`Routes ERROR:     ${routeResults.filter(r=>r.status==='ERROR').length}`);
  console.log(`Routes BLANK:     ${routeResults.filter(r=>r.status==='BLANK').length}`);
  console.log(`JS Errors total:  ${consoleErrors.length}`);
  console.log(`API Failures:     ${failedRequests.length}`);
  console.log(`\nReports → ${REPORT_DIR}`);
  console.log(`Screenshots → ${SCREENSHOTS_DIR}`);

  await wait(4000);
  await browser.close();
}

main().catch(err => {
  console.error('Script error:', err);
  process.exit(1);
});
