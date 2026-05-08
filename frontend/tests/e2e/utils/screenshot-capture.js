import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ARTIFACTS_DIR = path.join(__dirname, '../../../artifacts');
const SCREENSHOTS_DIR = path.join(ARTIFACTS_DIR, 'screenshots');

/**
 * Capture a screenshot and save it to the artifacts directory.
 * 
 * @param {import('@playwright/test').Page} page
 * @param {string} stepName - Description of the step being captured
 * @param {import('@playwright/test').TestInfo} testInfo
 */
export async function captureScreenshot(page, stepName, testInfo) {
  const safeStepName = stepName.replace(/[^a-z0-9]/gi, '_').toLowerCase();
  const safeTestName = testInfo.title.replace(/[^a-z0-9]/gi, '_').toLowerCase();
  const filename = `${safeTestName}-${safeStepName}-${Date.now()}.png`;
  const filepath = path.join(SCREENSHOTS_DIR, filename);

  await page.screenshot({ path: filepath, fullPage: true });
}
