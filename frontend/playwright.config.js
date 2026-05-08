// @ts-check
import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright E2E configuration for TPA ClaimSys.
 *
 * Test suites:
 *   1. UI tests  — auth.spec, customer.spec, admin-carrier.spec (offline/mock)
 *   2. Live API  — full-lifecycle.spec, api-tests.spec, fraud-rules.spec (requires live backend)
 *
 * Run modes:
 *   npm run test:e2e          — all tests (headless)
 *   npm run test:e2e:headed   — all tests (headed, visible browser)
 *   npm run test:e2e:ui       — UI-only tests (no live backend needed)
 *   npm run test:e2e:api      — API + lifecycle tests (requires docker stack)
 *   npm run test:e2e:report   — open HTML report
 */
export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,        // serial so login state doesn't collide
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 1,  // auto-retry once on failure
  workers: 1,
  timeout: 45_000,             // 45s per test for slow operations
  expect: { timeout: 10_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: '../reports/html-report', open: 'never' }],
    ['json', { outputFile: '../reports/results.json' }],
  ],

  // Output artifacts to custom directory
  outputDir: '../artifacts/test-results',

  use: {
    baseURL: process.env.APP_URL || 'http://localhost:3000',
    headless: process.env.HEADED !== 'true',
    viewport: { width: 1280, height: 800 },
    ignoreHTTPSErrors: true,
    screenshot: 'on',
    video: 'on',
    trace: 'on',
  },

  projects: [
    {
      name: 'live-backend',
      testMatch: ['**/*.spec.js'],
      use: {
        ...devices['Desktop Chrome'],
      },
    },
  ],

  /**
   * Auto-start Vite dev server when running live tests.
   * Comment out if running against a pre-built docker container.
   */
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
    timeout: 60_000,
  },
});
