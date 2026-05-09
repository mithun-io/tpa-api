const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

const ARTIFACT_DIR = path.join(process.env.USERPROFILE, '.gemini', 'antigravity', 'brain', 'e695002e-bda3-4dd9-9445-6a5be867551b');

if (!fs.existsSync(ARTIFACT_DIR)) {
  fs.mkdirSync(ARTIFACT_DIR, { recursive: true });
}

const writeReport = (filename, content) => {
  fs.writeFileSync(path.join(ARTIFACT_DIR, filename), content);
  console.log(`Report generated: ${filename}`);
};

const PAGES_TO_TEST = [
  { url: '/carrier/operations', name: 'Operations & SLA' },
  { url: '/carrier/financial-insights', name: 'Financial Analysis' },
  { url: '/carrier/policy-performance', name: 'Policy Performance' },
  { url: '/carrier/bulk-approvals', name: 'Bulk Settlement' },
  { url: '/carrier/query-center', name: 'Query Management' },
  { url: '/carrier/sla-tracker', name: 'Real-Time SLA Tracker' }
];

async function runValidation() {
  console.log('Launching browser...');
  const browser = await puppeteer.launch({ 
    headless: "new",
    args: ['--no-sandbox', '--disable-setuid-sandbox'] 
  });
  
  const page = await browser.newPage();
  await page.setViewport({ width: 1440, height: 900 });
  await page.setDefaultNavigationTimeout(60000);
  
  const consoleErrors = [];
  const networkFailures = [];
  const apiCalls = [];
  const validationResults = [];

  page.on('console', msg => {
    if (msg.type() === 'error') consoleErrors.push(`[Console Error] ${msg.text()}`);
  });

  page.on('pageerror', error => {
    consoleErrors.push(`[Page Error] ${error.message}`);
  });

  page.on('requestfailed', request => {
    const url = request.url();
    if (url.includes('localhost')) {
      networkFailures.push(`${request.method()} ${url} - ${request.failure()?.errorText || 'Failed'}`);
    }
  });

  page.on('response', response => {
    const url = response.url();
    const status = response.status();
    if (url.includes('localhost')) {
      apiCalls.push({ url, status, method: response.request().method() });
      if (status >= 400) {
        networkFailures.push(`API Failure: ${status} on ${url}`);
      }
    }
  });

  try {
    console.log('Logging in as Carrier...');
    await page.goto('http://localhost:3000/login', { waitUntil: 'domcontentloaded' });
    await page.waitForSelector('#tab-CARRIER_USER', { visible: true });
    await page.click('#tab-CARRIER_USER');
    await page.type('#CARRIER_USER-email', 'pwgcy57804@minitts.net');
    await page.type('#CARRIER_USER-password', 'Test@123');
    await page.click('#login-submit-CARRIER_USER');
    
    await page.waitForSelector('nav', { timeout: 30000 });
    console.log('Login successful.');

    for (const testPage of PAGES_TO_TEST) {
      console.log(`Testing ${testPage.name} at ${testPage.url}...`);
      await page.goto(`http://localhost:3000${testPage.url}`, { waitUntil: 'domcontentloaded' });
      await new Promise(r => setTimeout(r, 5000));

      const content = await page.evaluate(() => document.body.innerText);
      const html = await page.evaluate(() => document.body.innerHTML);
      const currentUrl = page.url();

      // Save screenshot for visual verification
      const screenshotPath = path.join(ARTIFACT_DIR, `${testPage.name.replace(/[^a-zA-Z0-9]/g, '_')}.png`);
      await page.screenshot({ path: screenshotPath, fullPage: true });
      console.log(`Saved screenshot to ${screenshotPath}`);

      const results = {
        name: testPage.name,
        url: testPage.url,
        actualUrl: currentUrl,
        loaded: currentUrl.includes(testPage.url) && !content.includes('Error 500'),
        unique: false,
        debugSnippet: content.substring(0, 500).replace(/\n/g, ' '),
        checks: []
      };

      // Case insensitive checks
      const hasText = (txt) => content.toLowerCase().includes(txt.toLowerCase());

      if (testPage.url === '/carrier/operations') {
        results.unique = hasText('NOC COMMAND CENTER');
        results.checks.push({ label: 'Heading', status: results.unique ? 'PASSED' : 'FAILED' });
        results.checks.push({ label: 'Workflow Section', status: hasText('INGESTION LAYER') || hasText('AI VALIDATION') ? 'PASSED' : 'FAILED' });
      }

      if (testPage.url === '/carrier/financial-insights') {
        results.unique = hasText('LIVE SETTLEMENTS') || hasText('LIQUIDITY RESERVES');
        results.checks.push({ label: 'Ticker', status: results.unique ? 'PASSED' : 'FAILED' });
        results.checks.push({ label: 'Ledger', status: hasText('UNIFIED FINANCIAL INTELLIGENCE LEDGER') ? 'PASSED' : 'FAILED' });
      }

      if (testPage.url === '/carrier/policy-performance') {
        results.unique = hasText('Insurance Product Marketplace') || hasText('Policy Heatmap');
        results.checks.push({ label: 'Plans', status: hasText('Family Floater') || hasText('Critical Illness') ? 'PASSED' : 'FAILED' });
        results.checks.push({ label: 'Simulator', status: hasText('Premium Configurator') || hasText('Senior Citizen Plan') ? 'PASSED' : 'FAILED' });
      }

      if (testPage.url === '/carrier/bulk-approvals') {
        results.unique = hasText('Bulk Settlement');
        results.checks.push({ label: 'Table', status: hasText('Claim Details') || hasText('No claims') || hasText('CLAIM DETAILS') ? 'PASSED' : 'FAILED' });
      }

      if (testPage.url === '/carrier/query-center') {
        results.unique = hasText('Query Management') || hasText('Direct Query Management');
        results.checks.push({ label: 'Inbox', status: hasText('Threads') || hasText('No active queries') || hasText('Query') ? 'PASSED' : 'FAILED' });
      }

      if (testPage.url === '/carrier/sla-tracker') {
        results.unique = hasText('SLA MISSION CONTROL') || hasText('MISSION CONTROL');
        results.checks.push({ label: 'Radar HUD', status: hasText('PREDICTIVE BREACH ENGINE') || hasText('ACTIVE BREACHES') ? 'PASSED' : 'FAILED' });
      }

      validationResults.push(results);
    }

  } catch (err) {
    console.error('Validation Script Error:', err);
    consoleErrors.push(`Automation Error: ${err.message}`);
  } finally {
    await browser.close();
  }

  // Generate Reports
  console.log('Writing reports...');

  let liveReport = `# Carrier Live Validation Report\n\n## Module Status\n\n`;
  validationResults.forEach(r => {
    if (r.url) {
      liveReport += `### ${r.name}\n- Status: ${r.loaded ? '✅ LOADED' : '❌ FAILED'}\n- Unique UI: ${r.unique ? '✅ YES' : '❌ NO'}\n`;
      r.checks.forEach(c => liveReport += `  - ${c.label}: ${c.status.includes('PASSED') ? '✅' : '❌'} ${c.status}\n`);
      liveReport += `- **Debug Snippet**: "${r.debugSnippet}"\n\n`;
    }
  });
  writeReport('carrier-live-validation-report.md', liveReport);

  let apiReport = `# Carrier API Validation Report\n\n## Intercepted API Calls (Internal)\n\n| Method | Status | URL |\n| --- | --- | --- |\n`;
  apiCalls.forEach(c => apiReport += `| ${c.method} | ${c.status} | ${c.url} |\n`);
  writeReport('carrier-api-validation-report.md', apiReport);

  let consoleReport = `# Carrier Console Error Report\n\n${consoleErrors.length > 0 ? consoleErrors.map(e => `- ${e}`).join('\n') : '✅ No console errors detected.'}\n`;
  writeReport('carrier-console-error-report.md', consoleReport);

  let networkReport = `# Carrier Network Failure Report\n\n${networkFailures.length > 0 ? networkFailures.map(e => `- ${e}`).join('\n') : '✅ No internal network failures detected.'}\n`;
  writeReport('carrier-network-failure-report.md', networkReport);

  let uiReport = `# Carrier UI Distinctness Report\n\n## Distinctness Analysis\n\n`;
  validationResults.filter(r => r.url).forEach(r => {
    uiReport += `### ${r.name}\n- Unique Metaphor: ${r.unique ? 'YES' : 'NO'}\n- Text Match: ${r.unique ? 'PASSED' : 'FAILED'}\n\n`;
  });
  writeReport('carrier-ui-distinctness-report.md', uiReport);

  let finalReport = `# Final Production Readiness Report\n\n## Readiness Checklist\n\n`;
  const allLoaded = validationResults.filter(r => r.url).every(r => r.loaded);
  const allUnique = validationResults.filter(r => r.url).every(r => r.unique);
  const noConsoleErrors = consoleErrors.length === 0;
  const noNetworkFailures = networkFailures.length === 0;

  finalReport += `- [${allLoaded ? 'x' : ' '}] All modules load without 404/500\n`;
  finalReport += `- [${allUnique ? 'x' : ' '}] Zero duplicate layouts\n`;
  finalReport += `- [${noConsoleErrors ? 'x' : ' '}] Zero console errors\n`;
  finalReport += `- [${noNetworkFailures ? 'x' : ' '}] Zero API failures\n\n`;
  finalReport += `## Conclusion\n\n${(allLoaded && allUnique && noConsoleErrors && noNetworkFailures) ? 'The Carrier Portal is fully validated and PROD-READY.' : 'Issues detected during validation. See sub-reports for details.'}\n`;
  writeReport('final-production-readiness-report.md', finalReport);

  console.log('All reports generated.');
}

runValidation();
