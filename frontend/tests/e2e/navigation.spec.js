import { test, expect } from '@playwright/test';
import { attachConsoleLogger } from './utils/console-logger.js';
import { attachApiMonitor } from './utils/api-monitor.js';
import { loginViaUI } from './utils/auth-helper.js';

let consoleLogger;
let apiMonitor;

test.beforeEach(async ({ page }, testInfo) => {
  consoleLogger = attachConsoleLogger(page, testInfo);
  apiMonitor = attachApiMonitor(page, testInfo);
});

test.afterEach(async () => {
  consoleLogger.assertNoErrors();
  apiMonitor.assertNoFailures();
});

test.describe('Self-Discovery Mode / UI Crawler', () => {
  
  test('TC-CRAWL-01: Dynamically crawl all reachable application links (Admin)', async ({ page, baseURL }) => {
    test.setTimeout(120000); // Allow generous time for crawling
    
    await loginViaUI(page, 'FMG_ADMIN');

    const visited = new Set();
    const queue = ['/admin', '/dashboard']; // Starting points
    const maxPages = 25; // Limit to avoid infinite loops if dynamic IDs are many
    let pagesCrawled = 0;

    while (queue.length > 0 && pagesCrawled < maxPages) {
      const currentPath = queue.shift();
      
      // Skip if already visited or external link
      if (visited.has(currentPath)) continue;
      if (currentPath.startsWith('http') && !currentPath.startsWith(baseURL)) continue;
      
      // Normalize URL
      const fullUrl = currentPath.startsWith('http') ? currentPath : `${baseURL}${currentPath}`;
      visited.add(currentPath);
      pagesCrawled++;
      
      console.log(`Crawling: ${fullUrl}`);
      await page.goto(fullUrl);
      await page.waitForLoadState('networkidle');

      // The global console and api monitors are active and will catch errors automatically.

      // Discover new links
      const hrefs = await page.evaluate(() => {
        return Array.from(document.querySelectorAll('a'))
          .map(a => a.getAttribute('href'))
          .filter(href => href && !href.startsWith('#') && !href.startsWith('mailto:'));
      });

      for (const href of hrefs) {
        // Strip out origin if it's an absolute local link
        let cleanPath = href;
        if (href.startsWith(baseURL)) {
          cleanPath = href.replace(baseURL, '');
        }
        
        // Exclude logout to prevent ending the session
        if (cleanPath.includes('logout')) continue;
        
        if (!visited.has(cleanPath) && !queue.includes(cleanPath)) {
          queue.push(cleanPath);
        }
      }
    }
    
    console.log(`Crawler finished. Visited ${pagesCrawled} pages.`);
    // Assert we actually crawled something
    expect(pagesCrawled).toBeGreaterThan(1);
  });
});
