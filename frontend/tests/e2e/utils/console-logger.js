import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const REPORTS_DIR = path.join(__dirname, '../../../reports');
const LOG_FILE = path.join(REPORTS_DIR, 'console-errors.log');

if (!fs.existsSync(REPORTS_DIR)) {
  fs.mkdirSync(REPORTS_DIR, { recursive: true });
}

// Clear the log file at start
if (!global.__consoleLoggerInitialized) {
  fs.writeFileSync(LOG_FILE, '');
  global.__consoleLoggerInitialized = true;
}

/**
 * Attaches a strict console monitor to the page.
 * Tracks errors and fails the test if any unexpected errors occur.
 * 
 * @param {import('@playwright/test').Page} page
 * @param {import('@playwright/test').TestInfo} testInfo
 */
export function attachConsoleLogger(page, testInfo) {
  const errors = [];

  page.on('console', msg => {
    if (msg.type() === 'error' || msg.type() === 'warning') {
      const text = msg.text();
      // Allowlist common known React warnings if needed (optional)
      if (text.includes('Download the React DevTools')) return;
      if (msg.type() === 'warning') return; // We'll strictly fail on 'error' only

      const logLine = `[${new Date().toISOString()}] [${testInfo.title}] CONSOLE ERROR: ${text}\n`;
      fs.appendFileSync(LOG_FILE, logLine);
      errors.push(text);
    }
  });

  page.on('pageerror', exception => {
    const logLine = `[${new Date().toISOString()}] [${testInfo.title}] UNCAUGHT EXCEPTION: ${exception.message}\n`;
    fs.appendFileSync(LOG_FILE, logLine);
    errors.push(exception.message);
  });

  return {
    getErrors: () => errors,
    assertNoErrors: () => {
      if (errors.length > 0) {
        throw new Error(`Console errors detected during test: \n${errors.join('\n')}`);
      }
    }
  };
}
