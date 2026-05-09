// Enterprise Carrier & Admin Demo Datasets — Pre-seeded operational intelligence
// Used by DemoDataProvider to instantly populate all carrier/admin modules

// ── Carrier Financial Data ─────────────────────────────────────────────────
export const DEMO_SETTLEMENT_TICKER = [
  { id: 'TXN-84291', claimRef: 'FAM-FLT-001', hospital: 'Apollo Hospitals', amount: 82400, status: 'CLEARED', time: '09:42:18', policy: 'Family Floater' },
  { id: 'TXN-84290', claimRef: 'HOSP-INS-042', hospital: 'Fortis Healthcare', amount: 145000, status: 'CLEARED', time: '09:38:05', policy: 'Hospitalization' },
  { id: 'TXN-84289', claimRef: 'CRIT-ILL-007', hospital: 'AIIMS Delhi', amount: 420000, status: 'BLOCKED', time: '09:31:44', policy: 'Critical Illness' },
  { id: 'TXN-84288', claimRef: 'MAT-COV-018', hospital: 'Kokilaben Hospital', amount: 62000, status: 'CLEARED', time: '09:28:12', policy: 'Maternity' },
  { id: 'TXN-84287', claimRef: 'CORP-GRP-201', hospital: 'Max Hospital', amount: 38500, status: 'CLEARED', time: '09:22:58', policy: 'Corporate Group' },
  { id: 'TXN-84286', claimRef: 'SNR-CIT-009', hospital: 'Medanta', amount: 95000, status: 'PENALTY', time: '09:18:30', policy: 'Senior Citizen' },
  { id: 'TXN-84285', claimRef: 'CARD-CARE-003', hospital: 'Narayana Health', amount: 520000, status: 'CLEARED', time: '09:14:02', policy: 'Cardiac Care' },
  { id: 'TXN-84284', claimRef: 'ACC-INS-091', hospital: 'Lilavati Hospital', amount: 28000, status: 'CLEARED', time: '09:08:44', policy: 'Accident' },
  { id: 'TXN-84283', claimRef: 'CANC-CARE-011', hospital: 'Tata Memorial', amount: 680000, status: 'CLEARED', time: '09:02:15', policy: 'Cancer Care' },
  { id: 'TXN-84282', claimRef: 'OPD-WEL-214', hospital: 'Columbia Asia', amount: 3200, status: 'CLEARED', time: '08:58:30', policy: 'OPD & Wellness' },
];

export const DEMO_FINANCIAL_METRICS = {
  reserves: 145000000,
  projectedDrain4h: 4280000,
  breachPenalties: 142000,
  carrierSavings: 12500000,
  leakagePrevented: 3840000,
  reimbursementVelocity: 94.2, // % settled within SLA
  avgSettlementTime: 2.4, // days
  pendingSettlements: 48,
  rejectedPayouts: 7,
};

export const DEMO_MONTHLY_TRENDS = {
  months: ['Nov', 'Dec', 'Jan', 'Feb', 'Mar', 'Apr'],
  settlements: [4.2, 4.8, 5.1, 4.9, 5.4, 5.8], // ₹ Crore
  rejections: [0.31, 0.28, 0.33, 0.29, 0.27, 0.24],
  savings: [0.92, 1.04, 1.18, 1.12, 1.28, 1.38],
  fraudPrevented: [0.42, 0.38, 0.51, 0.44, 0.48, 0.52],
};

// ── Loss Ratio Data ────────────────────────────────────────────────────────
export const DEMO_LOSS_RATIOS = [
  { plan: 'Accident Insurance', lossRatio: 42.8, premium: 6.18, claims: 2.64, target: 60 },
  { plan: 'AD&D Insurance', lossRatio: 28.4, premium: 3.21, claims: 0.91, target: 55 },
  { plan: 'Hospitalization', lossRatio: 68.2, premium: 22.64, claims: 15.44, target: 70 },
  { plan: 'Critical Illness', lossRatio: 74.8, premium: 5.75, claims: 4.30, target: 75 },
  { plan: 'Family Floater', lossRatio: 61.4, premium: 27.26, claims: 16.74, target: 65 },
  { plan: 'Senior Citizen', lossRatio: 82.6, premium: 7.58, claims: 6.26, target: 80 },
  { plan: 'Corporate Group', lossRatio: 54.2, premium: 46.27, claims: 25.08, target: 65 },
  { plan: 'Maternity', lossRatio: 66.8, premium: 4.51, claims: 3.01, target: 70 },
  { plan: 'Cancer Care', lossRatio: 78.4, premium: 4.43, claims: 3.47, target: 78 },
  { plan: 'Cardiac Care', lossRatio: 76.2, premium: 4.78, claims: 3.64, target: 76 },
];

// ── Fraud Heatmap Data ─────────────────────────────────────────────────────
export const DEMO_FRAUD_SIGNALS = [
  { id: 'FRD-2841', claimRef: 'CRIT-ILL-2024-004', hospital: 'Metro Hospital Pvt Ltd', score: 0.91, flags: 'DUPLICATE_BILLING,INFLATED_PROCEDURES', type: 'Critical Illness', amount: 420000, status: 'INVESTIGATING', city: 'Delhi' },
  { id: 'FRD-2840', claimRef: 'HOSP-INS-2024-118', hospital: 'CareFirst Clinic', score: 0.84, flags: 'UPCODING_DETECTED,NON_PPN_HOSPITAL', type: 'Hospitalization', amount: 180000, status: 'CONFIRMED', city: 'Mumbai' },
  { id: 'FRD-2839', claimRef: 'SNR-CIT-2024-031', hospital: 'Senior Care Multi Specialty', score: 0.78, flags: 'GHOST_PATIENT,FORGED_DOCUMENTS', type: 'Senior Citizen', amount: 95000, status: 'INVESTIGATING', city: 'Hyderabad' },
  { id: 'FRD-2838', claimRef: 'CORP-GRP-2024-201', hospital: 'Sunrise Medical Center', score: 0.72, flags: 'INFLATED_BILLING,PROVIDER_WATCHLIST', type: 'Corporate Group', amount: 72000, status: 'WATCHLIST', city: 'Bangalore' },
  { id: 'FRD-2837', claimRef: 'ACC-INS-2024-092', hospital: 'Rapid Care Hospital', score: 0.68, flags: 'PRE_PLANNED_ACCIDENT,PRIOR_CLAIM_PATTERN', type: 'Accident', amount: 48000, status: 'WATCHLIST', city: 'Chennai' },
  { id: 'FRD-2836', claimRef: 'CANC-CARE-2024-011', hospital: 'Oncology Plus Pvt Ltd', score: 0.65, flags: 'BILLING_BENCHMARK_EXCEEDED,UPCODING', type: 'Cancer Care', amount: 680000, status: 'REVIEWING', city: 'Kolkata' },
  { id: 'FRD-2835', claimRef: 'FAM-FLT-2024-088', hospital: 'QuickCare Network', score: 0.61, flags: 'DUPLICATE_POLICY_NUMBER', type: 'Family Floater', amount: 62000, status: 'REVIEWING', city: 'Pune' },
];

// ── SLA Breach Queue ───────────────────────────────────────────────────────
export const DEMO_SLA_BREACHES = [
  { id: 'SLA-BR-0841', claimRef: 'CRIT-ILL-2024-004-007', patient: 'Rajesh Nair', hospital: 'AIIMS Delhi', slaDeadline: '2026-05-08T14:00:00', breachedAt: '2026-05-08T16:42:00', hoursBreach: 2.7, penalty: 42000, status: 'CRITICAL', reviewer: 'reviewer.01@tpa.internal' },
  { id: 'SLA-BR-0840', claimRef: 'SNR-CIT-2024-031-009', patient: 'Sundari Krishnan', hospital: 'Medanta', slaDeadline: '2026-05-08T10:00:00', breachedAt: '2026-05-08T11:15:00', hoursBreach: 1.25, penalty: 9500, status: 'CRITICAL', reviewer: 'medical.officer@tpa.internal' },
  { id: 'SLA-BR-0839', claimRef: 'CANC-CARE-2024-011-003', patient: 'Priya Iyer', hospital: 'Tata Memorial', slaDeadline: '2026-05-07T18:00:00', breachedAt: '2026-05-07T22:30:00', hoursBreach: 4.5, penalty: 68000, status: 'ESCALATED', reviewer: 'claims.head@tpa.internal' },
  { id: 'SLA-BR-0838', claimRef: 'CORP-GRP-2024-201', patient: 'Ravi Sharma', hospital: 'Max Hospital', slaDeadline: '2026-05-08T09:00:00', breachedAt: '2026-05-08T10:20:00', hoursBreach: 1.3, penalty: 7200, status: 'WARNING', reviewer: 'reviewer.02@tpa.internal' },
  { id: 'SLA-BR-0837', claimRef: 'CARD-CARE-2024-003', patient: 'Venkat Rao', hospital: 'Narayana Health', slaDeadline: '2026-05-06T14:00:00', breachedAt: '2026-05-06T20:00:00', hoursBreach: 6.0, penalty: 52000, status: 'RESOLVED', reviewer: 'analyst.senior@tpa.internal' },
];

// ── PPN Hospital Rankings ──────────────────────────────────────────────────
export const DEMO_HOSPITALS = [
  { name: 'Apollo Hospitals', city: 'Chennai', tier: 1, claimsProcessed: 4820, avgAmount: 68000, approvalRate: 96.8, fraudRate: 0.8, rating: 4.9 },
  { name: 'Fortis Healthcare', city: 'Gurugram', tier: 1, claimsProcessed: 3640, avgAmount: 82000, approvalRate: 94.2, fraudRate: 1.2, rating: 4.7 },
  { name: 'Manipal Hospital', city: 'Bangalore', tier: 1, claimsProcessed: 3280, avgAmount: 72000, approvalRate: 95.6, fraudRate: 0.9, rating: 4.8 },
  { name: 'Narayana Health', city: 'Bangalore', tier: 1, claimsProcessed: 2840, avgAmount: 115000, approvalRate: 97.1, fraudRate: 0.4, rating: 4.9 },
  { name: 'Medanta The Medicity', city: 'Gurugram', tier: 1, claimsProcessed: 2640, avgAmount: 124000, approvalRate: 93.4, fraudRate: 1.8, rating: 4.6 },
  { name: 'KIMS Hospital', city: 'Hyderabad', tier: 2, claimsProcessed: 2180, avgAmount: 58000, approvalRate: 91.2, fraudRate: 2.4, rating: 4.4 },
  { name: 'Ruby Hall Clinic', city: 'Pune', tier: 2, claimsProcessed: 1980, avgAmount: 64000, approvalRate: 92.8, fraudRate: 1.8, rating: 4.5 },
  { name: 'Global Hospital', city: 'Mumbai', tier: 2, claimsProcessed: 1720, avgAmount: 78000, approvalRate: 90.4, fraudRate: 3.2, rating: 4.2 },
  { name: 'Aster Medcity', city: 'Kochi', tier: 2, claimsProcessed: 1540, avgAmount: 71000, approvalRate: 93.6, fraudRate: 1.4, rating: 4.6 },
  { name: 'Care Hospital', city: 'Hyderabad', tier: 2, claimsProcessed: 1380, avgAmount: 54000, approvalRate: 94.8, fraudRate: 0.8, rating: 4.7 },
];

// ── Admin Workbasket Data ──────────────────────────────────────────────────
export const DEMO_ADMIN_ESCALATIONS = [
  { id: 'ESC-2841', claimRef: 'CRIT-ILL-2024-004', patient: 'Rajesh Nair', amount: 420000, reason: 'High-value critical illness claim with moderate fraud indicators', assignedTo: 'reviewer.01@tpa.internal', priority: 'CRITICAL', age: 6 },
  { id: 'ESC-2840', claimRef: 'CANC-CARE-2024-011', patient: 'Priya Iyer', amount: 680000, reason: 'Cancer Care claim: Billing 28% above oncology benchmark. Second opinion requested.', assignedTo: 'medical.officer@tpa.internal', priority: 'HIGH', age: 3 },
  { id: 'ESC-2839', claimRef: 'CARD-CARE-2024-003', patient: 'Venkat Rao', amount: 520000, reason: 'CABG procedure with upcoded billing codes. Cardiology expert review needed.', assignedTo: 'claims.head@tpa.internal', priority: 'HIGH', age: 8 },
  { id: 'ESC-2838', claimRef: 'SNR-CIT-2024-031', patient: 'Sundari Krishnan', amount: 95000, reason: 'Ghost patient suspected. Hospital records do not match Aadhaar verification.', assignedTo: 'reviewer.02@tpa.internal', priority: 'CRITICAL', age: 2 },
  { id: 'ESC-2837', claimRef: 'HOSP-INS-2024-118', patient: 'Vivek Choudhary', amount: 180000, reason: 'Non-PPN hospital claim submitted after policy lapse. Coverage verification required.', assignedTo: 'analyst.senior@tpa.internal', priority: 'MEDIUM', age: 5 },
];

export const DEMO_ADMIN_METRICS = {
  totalClaimsInQueue: 284,
  pendingReview: 48,
  escalated: 12,
  avgTAT: 2.4,
  slaBreach: 5,
  fraudAlerts: 7,
  ocrCorrectionQueue: 18,
  paymentsPending: 34,
  agentUtilization: 88.4,
  ruleEngineExecutions: 1284,
  kafkaLag: 42,
};

export const DEMO_FRAUD_CASES = [
  { id: 'FI-2024-0841', claimRef: 'CRIT-ILL-2024-004', hospital: 'Metro Hospital', patient: 'Unknown', amount: 420000, fraudType: 'Document Forgery', status: 'UNDER_INVESTIGATION', investigator: 'fraud.team@tpa.internal', openedDays: 6 },
  { id: 'FI-2024-0840', claimRef: 'HOSP-INS-2024-118', hospital: 'CareFirst Clinic', patient: 'Vivek Choudhary', amount: 180000, fraudType: 'Inflated Billing', status: 'CONFIRMED_FRAUD', investigator: 'fraud.team@tpa.internal', openedDays: 12 },
  { id: 'FI-2024-0839', claimRef: 'SNR-CIT-2024-031', hospital: 'Senior Care Multi', patient: 'Sundari Krishnan', amount: 95000, fraudType: 'Ghost Patient', status: 'UNDER_INVESTIGATION', investigator: 'claims.head@tpa.internal', openedDays: 3 },
  { id: 'FI-2024-0838', claimRef: 'CORP-GRP-2024-201', hospital: 'Sunrise Medical', patient: 'Ravi Sharma', amount: 72000, fraudType: 'Provider Collusion', status: 'WATCHLIST', investigator: 'analyst.senior@tpa.internal', openedDays: 18 },
];

export const DEMO_RULE_ENGINE_LOGS = [
  { id: 'RE-10284', rule: 'HIGH_VALUE_THRESHOLD', triggered: 'Claims > ₹3L auto-escalated', result: 'ESCALATED', claimRef: 'CRIT-ILL-004', ts: '09:42:18' },
  { id: 'RE-10283', rule: 'FRAUD_SCORE_THRESHOLD', triggered: 'FraudScore > 0.7 → Investigation Queue', result: 'FLAGGED', claimRef: 'HOSP-INS-118', ts: '09:38:05' },
  { id: 'RE-10282', rule: 'DUPLICATE_CLAIM_CHECK', triggered: 'Same policy + hospital within 30 days', result: 'BLOCKED', claimRef: 'FAM-FLT-088', ts: '09:31:44' },
  { id: 'RE-10281', rule: 'SLA_BREACH_NOTIFICATION', triggered: 'Claim > 48h unprocessed → Alert sent', result: 'NOTIFIED', claimRef: 'SNR-CIT-031', ts: '09:28:12' },
  { id: 'RE-10280', rule: 'AI_AUTO_APPROVE', triggered: 'AI Score < 0.15 + Value < ₹50K → Auto-approve', result: 'AUTO_APPROVED', claimRef: 'ACC-INS-091', ts: '09:22:58' },
  { id: 'RE-10279', rule: 'NON_PPN_PENALTY', triggered: 'Non-network hospital → 20% co-pay applied', result: 'MODIFIED', claimRef: 'HOSP-INS-214', ts: '09:18:30' },
];

export const DEMO_OCR_QUEUE = [
  { id: 'OCR-0284', docType: 'Discharge Summary', claimRef: 'CRIT-ILL-2024-004', confidence: 62, issues: ['Patient name mismatch', 'Date unclear'], status: 'CORRECTION_NEEDED' },
  { id: 'OCR-0283', docType: 'Hospital Invoice', claimRef: 'CANC-CARE-2024-011', confidence: 71, issues: ['Amount field partially obscured'], status: 'CORRECTION_NEEDED' },
  { id: 'OCR-0282', docType: 'Lab Report', claimRef: 'SNR-CIT-2024-031', confidence: 48, issues: ['Poor scan quality', 'ICD code not readable', 'Stamp unclear'], status: 'RE_UPLOAD_REQUIRED' },
  { id: 'OCR-0281', docType: 'Prescriptions', claimRef: 'HOSP-INS-2024-118', confidence: 84, issues: ['Doctor registration number unclear'], status: 'MINOR_CORRECTION' },
];
