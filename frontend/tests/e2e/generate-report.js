/**
 * HTML Test Report Generator
 * Reads playwright-report/results.json and generates a rich HTML dashboard.
 *
 * Run: node tests/e2e/generate-report.js
 * Output: playwright-report/summary.html
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const RESULTS_FILE = path.join(__dirname, '..', '..', 'playwright-report', 'results.json');
const OUTPUT_FILE = path.join(__dirname, '..', '..', 'playwright-report', 'summary.html');

function readResults() {
  if (!fs.existsSync(RESULTS_FILE)) {
    console.warn('⚠️  results.json not found. Run: npm run test:e2e:all first.');
    return null;
  }
  return JSON.parse(fs.readFileSync(RESULTS_FILE, 'utf8'));
}

function statusBadge(status) {
  const map = {
    passed: { color: '#10b981', bg: '#d1fae5', label: '✅ PASSED' },
    failed: { color: '#ef4444', bg: '#fee2e2', label: '❌ FAILED' },
    skipped: { color: '#f59e0b', bg: '#fef3c7', label: '⏭️ SKIPPED' },
    timedOut: { color: '#f97316', bg: '#fff7ed', label: '⏱️ TIMEOUT' },
  };
  const s = map[status] || { color: '#6b7280', bg: '#f3f4f6', label: status };
  return `<span style="background:${s.bg};color:${s.color};padding:2px 10px;border-radius:12px;font-size:12px;font-weight:600;">${s.label}</span>`;
}

function formatMs(ms) {
  if (!ms) return '—';
  if (ms < 1000) return `${ms}ms`;
  return `${(ms / 1000).toFixed(1)}s`;
}

function generateHtml(data) {
  const suites = data.suites || [];
  const allTests = [];

  function collectTests(suite, suiteName = '') {
    const name = suiteName || suite.title || '';
    (suite.specs || []).forEach(spec => {
      (spec.tests || []).forEach(test => {
        const result = test.results?.[0] || {};
        allTests.push({
          suite: name || spec.file || '',
          title: spec.title,
          status: result.status || 'unknown',
          duration: result.duration || 0,
          error: result.error?.message || '',
          file: spec.file || '',
        });
      });
    });
    (suite.suites || []).forEach(sub => collectTests(sub, name || suite.title));
  }

  suites.forEach(s => collectTests(s));

  const total = allTests.length;
  const passed = allTests.filter(t => t.status === 'passed').length;
  const failed = allTests.filter(t => t.status === 'failed').length;
  const skipped = allTests.filter(t => t.status === 'skipped').length;
  const timedOut = allTests.filter(t => t.status === 'timedOut').length;
  const passRate = total > 0 ? Math.round(passed / total * 100) : 0;
  const totalDuration = allTests.reduce((s, t) => s + t.duration, 0);

  // Group by suite
  const grouped = {};
  allTests.forEach(t => {
    const key = t.file || t.suite || 'General';
    if (!grouped[key]) grouped[key] = [];
    grouped[key].push(t);
  });

  const suiteRows = Object.entries(grouped).map(([suite, tests]) => {
    const sp = tests.filter(t => t.status === 'passed').length;
    const sf = tests.filter(t => t.status === 'failed').length;
    const sRate = Math.round(sp / tests.length * 100);
    const testRows = tests.map(t => `
      <tr style="border-bottom:1px solid #f3f4f6;">
        <td style="padding:10px 16px;color:#374151;font-size:13px;">${t.title}</td>
        <td style="padding:10px 16px;text-align:center;">${statusBadge(t.status)}</td>
        <td style="padding:10px 16px;text-align:right;color:#6b7280;font-size:13px;">${formatMs(t.duration)}</td>
        <td style="padding:10px 16px;color:#ef4444;font-size:12px;max-width:350px;word-break:break-word;">${t.error ? t.error.split('\n')[0].substring(0, 120) : ''}</td>
      </tr>`).join('');
    return `
      <div style="margin-bottom:32px;background:#fff;border-radius:12px;box-shadow:0 1px 3px rgba(0,0,0,0.1);overflow:hidden;">
        <div style="background:linear-gradient(135deg,#1e40af,#3b82f6);padding:14px 20px;display:flex;justify-content:space-between;align-items:center;">
          <span style="color:#fff;font-weight:700;font-size:15px;">${path.basename(suite)}</span>
          <span style="color:#bfdbfe;font-size:13px;">${sp}/${tests.length} passed &nbsp;|&nbsp; ${sRate}% &nbsp;|&nbsp; ${sf > 0 ? `<span style="color:#fca5a5;">${sf} failed</span>` : '✅ All passed'}</span>
        </div>
        <table style="width:100%;border-collapse:collapse;">
          <thead><tr style="background:#f8fafc;font-size:12px;color:#6b7280;font-weight:600;text-transform:uppercase;">
            <th style="padding:10px 16px;text-align:left;">Test</th>
            <th style="padding:10px 16px;text-align:center;">Status</th>
            <th style="padding:10px 16px;text-align:right;">Duration</th>
            <th style="padding:10px 16px;text-align:left;">Error</th>
          </tr></thead>
          <tbody>${testRows}</tbody>
        </table>
      </div>`;
  }).join('');

  const failedTests = allTests.filter(t => t.status === 'failed');
  const failureAnalysis = failedTests.length === 0 ? `
    <div style="background:#d1fae5;border:1px solid #6ee7b7;border-radius:12px;padding:20px 24px;text-align:center;">
      <div style="font-size:48px;">🎉</div>
      <div style="color:#065f46;font-weight:700;font-size:18px;margin-top:8px;">All tests passed!</div>
      <div style="color:#047857;margin-top:4px;">Zero failures detected.</div>
    </div>` : `
    <div style="background:#fff;border-radius:12px;box-shadow:0 1px 3px rgba(0,0,0,0.1);overflow:hidden;margin-top:24px;">
      <div style="background:#ef4444;padding:14px 20px;">
        <span style="color:#fff;font-weight:700;font-size:15px;">❌ Failed Tests (${failedTests.length})</span>
      </div>
      ${failedTests.map(t => `
        <div style="padding:16px 20px;border-bottom:1px solid #fee2e2;">
          <div style="font-weight:600;color:#111827;">${t.title}</div>
          <div style="font-size:12px;color:#6b7280;margin-top:2px;">${t.file}</div>
          ${t.error ? `<pre style="background:#fef2f2;color:#ef4444;padding:10px;border-radius:6px;margin-top:8px;font-size:12px;overflow-x:auto;">${t.error.substring(0, 500)}</pre>` : ''}
        </div>`).join('')}
    </div>`;

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TPA ClaimSys — E2E Test Report</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: 'Inter', system-ui, sans-serif; background: #f1f5f9; color: #111827; }
    .container { max-width: 1100px; margin: 0 auto; padding: 32px 24px; }
    .header { background: linear-gradient(135deg, #0f172a, #1e40af); color: white; border-radius: 16px; padding: 32px; margin-bottom: 32px; }
    .stat-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 32px; }
    .stat-card { background: #fff; border-radius: 12px; padding: 20px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .stat-value { font-size: 36px; font-weight: 700; }
    .stat-label { color: #6b7280; font-size: 13px; margin-top: 4px; }
    .progress-bar { background: #e5e7eb; border-radius: 8px; height: 12px; margin-bottom: 32px; overflow: hidden; }
    .progress-fill { height: 100%; border-radius: 8px; background: linear-gradient(90deg, #10b981, #34d399); transition: width 0.3s; }
    @media (max-width: 768px) { .stat-grid { grid-template-columns: repeat(2, 1fr); } }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:16px;">
        <div>
          <h1 style="font-size:28px;font-weight:700;margin-bottom:8px;">🏥 TPA ClaimSys</h1>
          <p style="font-size:18px;opacity:0.85;">E2E Test Report — Full QA Audit</p>
          <p style="font-size:13px;opacity:0.6;margin-top:8px;">Generated: ${new Date().toLocaleString('en-IN')}</p>
        </div>
        <div style="text-align:right;">
          <div style="font-size:56px;font-weight:800;">${passRate}%</div>
          <div style="opacity:0.7;">Pass Rate</div>
        </div>
      </div>
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-value" style="color:#111827;">${total}</div>
        <div class="stat-label">Total Tests</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#10b981;">${passed}</div>
        <div class="stat-label">Passed</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#ef4444;">${failed}</div>
        <div class="stat-label">Failed</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#f59e0b;">${skipped}</div>
        <div class="stat-label">Skipped</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#6b7280;">${formatMs(totalDuration)}</div>
        <div class="stat-label">Total Duration</div>
      </div>
    </div>

    <div class="progress-bar">
      <div class="progress-fill" style="width:${passRate}%;"></div>
    </div>

    <h2 style="font-size:20px;font-weight:700;margin-bottom:20px;">📋 Test Suites</h2>
    ${suiteRows || '<p style="color:#6b7280;">No test data available. Run tests first.</p>'}

    <h2 style="font-size:20px;font-weight:700;margin:32px 0 16px;">🔍 Failure Analysis</h2>
    ${failureAnalysis}

    <div style="margin-top:32px;padding:16px 20px;background:#f8fafc;border-radius:10px;font-size:12px;color:#6b7280;text-align:center;">
      Generated by TPA ClaimSys E2E QA Engine &nbsp;|&nbsp;
      Playwright ${data.stats?.startTime ? new Date(data.stats.startTime).toLocaleDateString() : 'N/A'} &nbsp;|&nbsp;
      ${data.stats?.workerCount || 1} worker(s)
    </div>
  </div>
</body>
</html>`;
}

// ── Main ──────────────────────────────────────────────────────────────────────

const data = readResults();
if (!data) {
  console.log('Creating placeholder report...');
  const placeholder = generateHtml({ suites: [], stats: {} });
  fs.writeFileSync(OUTPUT_FILE, placeholder);
  console.log(`📋 Placeholder report: ${OUTPUT_FILE}`);
} else {
  const html = generateHtml(data);
  fs.writeFileSync(OUTPUT_FILE, html);
  console.log(`✅ Report generated: ${OUTPUT_FILE}`);

  const allTests = [];
  const collectTests = (suite) => {
    (suite.specs || []).forEach(spec =>
      (spec.tests || []).forEach(t => allTests.push(t.results?.[0]?.status))
    );
    (suite.suites || []).forEach(collectTests);
  };
  (data.suites || []).forEach(collectTests);

  const p = allTests.filter(s => s === 'passed').length;
  const f = allTests.filter(s => s === 'failed').length;
  console.log(`\n📊 Results: ${p} passed, ${f} failed, ${allTests.length} total`);
  console.log(`🎯 Pass rate: ${allTests.length > 0 ? Math.round(p / allTests.length * 100) : 0}%`);
}
