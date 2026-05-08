/**
 * Real UI-based authentication helper.
 * Simulates human interaction by actually typing credentials and clicking.
 */

export const CREDENTIALS = {
  FMG_ADMIN: {
    email: process.env.ADMIN_EMAIL || 'mithun-io@outlook.com',
    password: process.env.ADMIN_PASSWORD || 'Qw3!@sPe:E1'
  },
  CUSTOMER: {
    email: 'customer@tpa.com', // Replace with valid test customer if needed, or register dynamically
    password: 'Qw3!@sPe:E1'
  },
  CARRIER_USER: {
    email: 'carrier@tpa.com',
    password: 'Password123!'
  }
};

/**
 * Performs a real browser login workflow.
 * 
 * @param {import('@playwright/test').Page} page 
 * @param {'CUSTOMER'|'FMG_ADMIN'|'CARRIER_USER'} role 
 */
export async function loginViaUI(page, role) {
  await page.goto('/login');
  
  // Wait for the login form to be visible instead of networkidle
  await page.waitForSelector(`#tab-${role}`);

  // Click the respective tab
  await page.click(`#tab-${role}`);
  
  // Fill credentials
  await page.fill(`#${role}-email`, CREDENTIALS[role].email);
  await page.fill(`#${role}-password`, CREDENTIALS[role].password);
  
  // Submit
  await Promise.all([
    page.waitForNavigation({ url: /dashboard|admin/, waitUntil: 'commit', timeout: 30000 }).catch(() => {}),
    page.click(`#login-submit-${role}`)
  ]);
}

/**
 * Inject fake auth state (for fast offline tests if needed, but not used in strict real mode).
 */
export async function injectFakeAuth(page, role = 'CUSTOMER') {
  const userMap = {
    CUSTOMER:     { id: 1, username: 'testcustomer', email: 'customer@tpa.com', userRole: 'CUSTOMER',     userStatus: 'ACTIVE' },
    FMG_ADMIN:    { id: 2, username: 'testadmin',    email: 'admin@tpa.com',    userRole: 'FMG_ADMIN',    userStatus: 'ACTIVE' },
    CARRIER_USER: { id: 3, username: 'testcarrier',  email: 'carrier@tpa.com',  userRole: 'CARRIER_USER', userStatus: 'ACTIVE' },
  };

  const fakeToken = [
    btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' })),
    btoa(JSON.stringify({ sub: userMap[role].email, role, exp: 9999999999 })),
    'fake-signature',
  ].join('.');

  await page.addInitScript(({ token, user }) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  }, { token: fakeToken, user: userMap[role] });
}
