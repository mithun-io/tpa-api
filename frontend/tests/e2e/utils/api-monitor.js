import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const REPORTS_DIR = path.join(__dirname, '../../../reports');
const LOG_FILE = path.join(REPORTS_DIR, 'network-failures.log');

if (!fs.existsSync(REPORTS_DIR)) {
  fs.mkdirSync(REPORTS_DIR, { recursive: true });
}

if (!global.__apiMonitorInitialized) {
  fs.writeFileSync(LOG_FILE, '');
  global.__apiMonitorInitialized = true;
}

/**
 * Attaches a strict API monitor to the page.
 * Tracks 4xx/5xx responses and fails the test.
 * 
 * @param {import('@playwright/test').Page} page
 * @param {import('@playwright/test').TestInfo} testInfo
 */
export function attachApiMonitor(page, testInfo) {
  const failures = [];

  page.on('response', async response => {
    const status = response.status();
    // Exclude redirects (3xx) and successful (2xx).
    // Specifically looking for 400+ errors.
    if (status >= 400) {
      const url = response.url();
      // Allowlist common 404s like missing favicons if necessary
      if (url.includes('favicon.ico')) return;

      let body = '';
      try {
        body = await response.text();
      } catch (e) {
        body = '[Could not read body]';
      }

      const logLine = `[${new Date().toISOString()}] [${testInfo.title}] HTTP ${status} on ${url}\nResponse: ${body.substring(0, 200)}\n`;
      fs.appendFileSync(LOG_FILE, logLine);
      
      failures.push({ url, status, body });
    }
  });

  return {
    getFailures: () => failures,
    assertNoFailures: () => {
      if (failures.length > 0) {
        const msg = failures.map(f => `${f.status} on ${f.url}`).join('\n');
        throw new Error(`Failed network requests detected:\n${msg}`);
      }
    }
  };
}
