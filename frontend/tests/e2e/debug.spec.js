import { test, expect } from '@playwright/test';
import { attachConsoleLogger } from './utils/console-logger.js';
import { attachApiMonitor } from './utils/api-monitor.js';
import { CREDENTIALS } from './utils/auth-helper.js';

test('DEBUG-01: Manual UI Login to see why it fails', async ({ page }) => {
  console.log('Navigating to /login');
  await page.goto('/login');
  
  await page.waitForLoadState('networkidle');

  console.log('Clicking Admin Tab');
  await page.click('#tab-FMG_ADMIN');
  
  console.log('Filling form');
  await page.fill('#FMG_ADMIN-email', CREDENTIALS.FMG_ADMIN.email);
  await page.fill('#FMG_ADMIN-password', CREDENTIALS.FMG_ADMIN.password);
  
  console.log('Listening to network responses...');
  page.on('response', resp => {
      console.log(`[NETWORK] ${resp.status()} - ${resp.url()}`);
  });

  page.on('framenavigated', frame => {
    if (frame === page.mainFrame()) {
      console.log(`[NAVIGATED] ${frame.url()}`);
    }
  });

  console.log('Submitting form');
  await page.click('#login-submit-FMG_ADMIN');
  
  console.log('Waiting for network idle...');
  await page.waitForLoadState('networkidle', { timeout: 5000 }).catch(() => console.log('networkidle timeout'));
  
  const currentUrl = page.url();
  console.log(`[CURRENT URL] ${currentUrl}`);

  const errorBanner = await page.locator('.login-error-banner').count();
  if (errorBanner > 0) {
      console.log(`[ERROR BANNER] ${await page.locator('.login-error-banner').textContent()}`);
  }
  
  // Try to grab page content for debugging
  const body = await page.innerHTML('body');
  require('fs').writeFileSync('../artifacts/debug-body.html', body);
});
