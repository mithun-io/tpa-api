// Enterprise Demo Claims — Rich customer claim histories pre-populated for instant UI

const now = new Date();
const sub = (days) => { const d = new Date(now); d.setDate(d.getDate() - days); return d; };
const add = (days) => { const d = new Date(now); d.setDate(d.getDate() + days); return d; };
const ymd = (d) => d.toISOString().slice(0, 10);
const ts = (d) => d.toISOString().slice(0, 19);
const disp = (d) => d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });

export const DEMO_CUSTOMER_CLAIMS = [
  {
    id: 10001,
    policyNumber: 'FAM-FLT-2024-001-0001',
    policyName: 'Family Floater Plan',
    status: 'SETTLED',
    amount: 82400,
    totalBillAmount: 103000,
    patientName: 'Aerica Pancake',
    hospitalName: 'Apollo Hospitals',
    diagnosis: 'Acute Appendicitis — Laparoscopic Surgery',
    icdCode: 'K35.89',
    claimType: 'Surgical',
    admissionDate: ymd(sub(85)),
    dischargeDate: ymd(sub(82)),
    createdDate: ts(sub(86)),
    processedDate: ts(sub(80)),
    riskLevel: 'LOW',
    fraudScore: 0.07,
    aiSummary: 'Clean claim (score: 0.07). Emergency surgical admission verified. All billing codes align with procedure. Fast-track approval recommended.',
    timeline: [
      { status: 'SUBMITTED', label: 'Claim Submitted', ts: ts(sub(86)), actor: 'Customer Portal' },
      { status: 'AI_VALIDATED', label: 'AI Validation Passed', ts: ts(sub(86)), actor: 'AI Engine v3.2' },
      { status: 'UNDER_REVIEW', label: 'Assigned to Medical Reviewer', ts: ts(sub(85)), actor: 'reviewer.01@tpa.internal' },
      { status: 'ADMIN_APPROVED', label: 'Admin Approved', ts: ts(sub(83)), actor: 'mithun-io@outlook.com' },
      { status: 'CARRIER_APPROVED', label: 'Carrier Approved', ts: ts(sub(82)), actor: 'pwgcy57804@minitts.net' },
      { status: 'SETTLED', label: 'Payment Settled — ₹82,400 credited', ts: ts(sub(80)), actor: 'Payment Gateway' },
    ]
  },
  {
    id: 10002,
    policyNumber: 'FAM-FLT-2024-001-0042',
    policyName: 'Family Floater Plan',
    status: 'UNDER_REVIEW',
    amount: 145000,
    totalBillAmount: 181250,
    patientName: 'Aerica Pancake (Spouse)',
    hospitalName: 'Fortis Healthcare',
    diagnosis: 'Coronary Artery Disease — CABG Procedure',
    icdCode: 'I25.10',
    claimType: 'Cardiac Surgery',
    admissionDate: ymd(sub(12)),
    dischargeDate: ymd(sub(6)),
    createdDate: ts(sub(13)),
    riskLevel: 'MEDIUM',
    fraudScore: 0.38,
    aiSummary: 'Moderate risk (score: 0.38). High-value cardiac procedure. Billing 18% above regional benchmark. Senior medical review recommended.',
    timeline: [
      { status: 'SUBMITTED', label: 'Claim Submitted', ts: ts(sub(13)), actor: 'Customer Portal' },
      { status: 'AI_VALIDATED', label: 'AI Validation — Moderate Risk Flagged', ts: ts(sub(13)), actor: 'AI Engine v3.2' },
      { status: 'UNDER_REVIEW', label: 'Escalated to Senior Medical Reviewer', ts: ts(sub(12)), actor: 'medical.officer@tpa.internal' },
    ]
  },
  {
    id: 10003,
    policyNumber: 'MAT-COV-2024-007-0018',
    policyName: 'Maternity Insurance',
    status: 'APPROVED',
    amount: 62000,
    totalBillAmount: 62000,
    patientName: 'Aerica Pancake',
    hospitalName: 'Kokilaben Dhirubhai Ambani Hospital',
    diagnosis: 'Normal Vaginal Delivery',
    icdCode: 'Z37.0',
    claimType: 'Maternity',
    admissionDate: ymd(sub(42)),
    dischargeDate: ymd(sub(41)),
    createdDate: ts(sub(43)),
    processedDate: ts(sub(38)),
    riskLevel: 'LOW',
    fraudScore: 0.04,
    aiSummary: 'Clean maternity claim (score: 0.04). Standard delivery procedure. Pre-authorization verified 3 months prior. Approve.',
    timeline: [
      { status: 'SUBMITTED', label: 'Claim Submitted', ts: ts(sub(43)), actor: 'Customer Portal' },
      { status: 'AI_VALIDATED', label: 'AI Validation Passed', ts: ts(sub(43)), actor: 'AI Engine v3.2' },
      { status: 'UNDER_REVIEW', label: 'Assigned to Reviewer', ts: ts(sub(42)), actor: 'reviewer.02@tpa.internal' },
      { status: 'ADMIN_APPROVED', label: 'Admin Approved', ts: ts(sub(40)), actor: 'mithun-io@outlook.com' },
      { status: 'APPROVED', label: 'Carrier Approved — Payment Scheduled', ts: ts(sub(38)), actor: 'pwgcy57804@minitts.net' },
    ]
  },
  {
    id: 10004,
    policyNumber: 'CRIT-ILL-2024-004-0007',
    policyName: 'Critical Illness Insurance',
    status: 'REJECTED',
    amount: 0,
    totalBillAmount: 980000,
    patientName: 'Aerica Pancake (Father)',
    hospitalName: 'AIIMS Delhi',
    diagnosis: 'Malignant Neoplasm — Hepatocellular Carcinoma',
    icdCode: 'C22.0',
    claimType: 'Oncology',
    admissionDate: ymd(sub(120)),
    dischargeDate: ymd(sub(108)),
    createdDate: ts(sub(122)),
    processedDate: ts(sub(105)),
    rejectionReason: 'Pre-existing critical illness diagnosis confirmed within waiting period of 90 days. ICD-10 records show prior consultation for hepatic condition 45 days before policy inception.',
    riskLevel: 'HIGH',
    fraudScore: 0.72,
    aiSummary: 'High risk (score: 0.72). Pre-existing condition detected. Medical records indicate hepatic consultation 45 days before policy start. Recommend rejection per policy terms.',
    timeline: [
      { status: 'SUBMITTED', label: 'Claim Submitted', ts: ts(sub(122)), actor: 'Customer Portal' },
      { status: 'AI_VALIDATED', label: 'AI Flagged Pre-existing Condition', ts: ts(sub(122)), actor: 'AI Engine v3.2' },
      { status: 'UNDER_REVIEW', label: 'Fraud Investigation Opened', ts: ts(sub(120)), actor: 'claims.head@tpa.internal' },
      { status: 'REJECTED', label: 'Claim Rejected — Pre-existing Condition', ts: ts(sub(105)), actor: 'mithun-io@outlook.com' },
    ]
  },
  {
    id: 10005,
    policyNumber: 'HOSP-INS-2024-003-0091',
    policyName: 'Hospitalization Insurance',
    status: 'PAYMENT_PENDING',
    amount: 48500,
    totalBillAmount: 60625,
    patientName: 'Aerica Pancake (Son)',
    hospitalName: 'Manipal Hospital',
    diagnosis: 'Dengue Fever with Thrombocytopenia',
    icdCode: 'A90',
    claimType: 'Infectious Disease',
    admissionDate: ymd(sub(5)),
    dischargeDate: ymd(sub(3)),
    createdDate: ts(sub(6)),
    riskLevel: 'LOW',
    fraudScore: 0.09,
    aiSummary: 'Clean claim (score: 0.09). Seasonal dengue admission. Hospital billing matches procedure. Payment initiation pending carrier final sign-off.',
    timeline: [
      { status: 'SUBMITTED', label: 'Claim Submitted', ts: ts(sub(6)), actor: 'Customer Portal' },
      { status: 'AI_VALIDATED', label: 'AI Validation Passed', ts: ts(sub(6)), actor: 'AI Engine v3.2' },
      { status: 'UNDER_REVIEW', label: 'Assigned to Reviewer', ts: ts(sub(5)), actor: 'reviewer.01@tpa.internal' },
      { status: 'ADMIN_APPROVED', label: 'Admin Approved', ts: ts(sub(4)), actor: 'mithun-io@outlook.com' },
      { status: 'CARRIER_APPROVED', label: 'Carrier Approved', ts: ts(sub(3)), actor: 'pwgcy57804@minitts.net' },
      { status: 'PAYMENT_PENDING', label: 'Payment Processing (2–3 days)', ts: ts(sub(2)), actor: 'Payment Gateway' },
    ]
  }
];

export const DEMO_CUSTOMER_NOTIFICATIONS = [
  { id: 1, title: '💰 Payment Received', message: '₹82,400 has been credited to your account for claim FAM-FLT-2024-001-0001.', isRead: false, createdAt: ts(sub(80)) },
  { id: 2, title: '⚠️ Review In Progress', message: 'Your CABG claim (₹1,45,000) is under senior medical review. ETA: 2 business days.', isRead: false, createdAt: ts(sub(12)) },
  { id: 3, title: '✅ Maternity Claim Approved', message: 'Claim MAT-COV-2024-007-0018 approved. ₹62,000 payment scheduled.', isRead: true, createdAt: ts(sub(38)) },
  { id: 4, title: '❌ Claim Rejected', message: 'Claim CRIT-ILL-2024-004-0007 rejected: Pre-existing condition within waiting period. Contact support to appeal.', isRead: true, createdAt: ts(sub(105)) },
  { id: 5, title: '🔔 Premium Due Reminder', message: 'Family Floater Plan premium of ₹14,400 is due in 15 days. Auto-pay enabled.', isRead: false, createdAt: ts(sub(1)) },
  { id: 6, title: '🏥 Hospital Network Update', message: '3 new hospitals added to your PPN: KIMS Hyderabad, Aster Medcity, Yashoda Hospitals.', isRead: true, createdAt: ts(sub(30)) },
  { id: 7, title: '📋 Document Required', message: 'Please upload final discharge summary for claim HOSP-INS-2024-003-0091 to complete processing.', isRead: false, createdAt: ts(sub(3)) },
];

export const DEMO_CUSTOMER_STATS = {
  totalClaims: 5,
  settledClaims: 1,
  approvedClaims: 1,
  rejectedClaims: 1,
  pendingClaims: 2,
  totalReimbursed: 82400,
  totalPending: 193500,
  coverageUtilization: 29.8,
  walletBalance: 82400,
  activePlans: ['Family Floater Plan', 'Maternity Insurance', 'Hospitalization Insurance', 'Critical Illness Insurance'],
  nextPremiumDue: disp(add(15)),
  nextPremiumAmount: 14400,
};
