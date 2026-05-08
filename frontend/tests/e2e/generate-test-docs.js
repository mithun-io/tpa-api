/**
 * Test Document Generator — Zero external dependencies
 * Creates realistic test documents for Playwright E2E claim upload tests.
 *
 * Run: node tests/e2e/generate-test-docs.js
 * Output: tests/e2e/test-documents/
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const OUTPUT_DIR = path.join(__dirname, 'test-documents');

if (!fs.existsSync(OUTPUT_DIR)) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

// ── 1. Realistic Hospital Bill PDF ────────────────────────────────────────────

function generateHospitalBillPdf() {
  const lines = [
    'BT',
    '/F1 18 Tf  50 760 Td  (CITY GENERAL HOSPITAL) Tj',
    '/F1 10 Tf  0 -18 Td  (NABH Accredited | 123 Medical Avenue, Bangalore - 560001) Tj',
    '/F1 10 Tf  0 -18 Td  (Tel: +91-80-12345678 | billing@cityhospital.com) Tj',
    '0 -10 Td',
    '/F1 14 Tf  0 -22 Td  (HOSPITAL BILL & DISCHARGE SUMMARY) Tj',
    '0 -5 Td',
    '/F1 10 Tf  0 -18 Td  (------------------------------------------------------------) Tj',
    '0 -18 Td  (PATIENT DETAILS) Tj',
    '0 -16 Td  (Patient Name    : Rajesh Kumar Sharma) Tj',
    '0 -16 Td  (Age / Gender    : 45 Years / Male) Tj',
    '0 -16 Td  (Patient ID      : CGH-2024-08744) Tj',
    '0 -16 Td  (Policy Number   : STAR/2024/IND/001234) Tj',
    '0 -16 Td  (Admission Date  : 15-Mar-2024) Tj',
    '0 -16 Td  (Discharge Date  : 22-Mar-2024) Tj',
    '0 -16 Td  (Ward / Room     : Semi-Private Ward / Room 304) Tj',
    '0 -16 Td  (Bill Number     : CGH/BILL/2024/12456) Tj',
    '0 -10 Td',
    '0 -18 Td  (------------------------------------------------------------) Tj',
    '0 -18 Td  (DIAGNOSIS & TREATMENT) Tj',
    '0 -16 Td  (Primary Diagnosis : Essential (Primary) Hypertension) Tj',
    '0 -16 Td  (ICD-10 Code       : I10) Tj',
    '0 -16 Td  (Secondary Diagnosis: Type 2 Diabetes Mellitus (E11.9)) Tj',
    '0 -16 Td  (Treating Doctor   : Dr. Priya Nair, MD (DM Cardiology)) Tj',
    '0 -16 Td  (Procedure         : Coronary Angiography, Medication Management) Tj',
    '0 -10 Td',
    '0 -18 Td  (------------------------------------------------------------) Tj',
    '0 -18 Td  (BILL BREAKDOWN) Tj',
    '0 -16 Td  (Room Charges (7 days x Rs 3500)     : Rs  24,500) Tj',
    '0 -16 Td  (Doctor Consultation (8 visits)       : Rs   9,600) Tj',
    '0 -16 Td  (Nursing Charges                      : Rs   5,600) Tj',
    '0 -16 Td  (Cardiology Consultation (2)          : Rs   5,000) Tj',
    '0 -16 Td  (ECG & Monitoring (7 days)            : Rs   3,500) Tj',
    '0 -16 Td  (Medications & Injections             : Rs  12,400) Tj',
    '0 -16 Td  (Angiography Procedure                : Rs  18,000) Tj',
    '0 -16 Td  (Laboratory Tests                     : Rs   4,800) Tj',
    '0 -16 Td  (Radiology (X-Ray, Echo)              : Rs   3,000) Tj',
    '0 -16 Td  (Physiotherapy (3 sessions)           : Rs   2,100) Tj',
    '0 -16 Td  (Diet & Nutrition                     : Rs   2,800) Tj',
    '0 -16 Td  (Miscellaneous                        : Rs   1,200) Tj',
    '0 -10 Td',
    '0 -18 Td  (------------------------------------------------------------) Tj',
    '/F1 12 Tf  0 -20 Td  (TOTAL BILL AMOUNT               : Rs  92,500) Tj',
    '0 -10 Td',
    '/F1 10 Tf  0 -18 Td  (------------------------------------------------------------) Tj',
    '0 -18 Td  (INSURANCE / TPA DETAILS) Tj',
    '0 -16 Td  (TPA             : FMG ClaimSys Third Party Administrator) Tj',
    '0 -16 Td  (Insurer         : Star Health & Allied Insurance) Tj',
    '0 -16 Td  (Claimed Amount  : Rs 92,500) Tj',
    '0 -16 Td  (Pre-Auth Number : STAR/PA/2024/98765) Tj',
    '0 -10 Td',
    '0 -18 Td  (Authorized Signatory: ___________________  Date: 22-Mar-2024) Tj',
    '0 -16 Td  (Patient Signature  : ___________________  Date: 22-Mar-2024) Tj',
    'ET',
  ].join('\n');

  const streamLen = Buffer.byteLength(lines, 'utf8');

  const pdf = [
    '%PDF-1.4',
    '1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj',
    '2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj',
    `3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj`,
    `4 0 obj<</Length ${streamLen}>>stream`,
    lines,
    'endstream',
    'endobj',
    '5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj',
    'xref',
    '0 6',
    '0000000000 65535 f ',
    '0000000009 00000 n ',
    '0000000058 00000 n ',
    '0000000115 00000 n ',
    '0000000274 00000 n ',
    '0000000700 00000 n ',
    `trailer<</Size 6/Root 1 0 R>>`,
    'startxref',
    '780',
    '%%EOF',
  ].join('\n');

  return pdf;
}

// ── 2. Duplicate Bill PDF (same bill number — triggers fraud detection) ────────

function generateDuplicateBillPdf() {
  const lines = [
    'BT',
    '/F1 18 Tf  50 760 Td  (METRO MEDICAL CENTER) Tj',
    '/F1 10 Tf  0 -18 Td  (456 Healthcare Road, Bangalore - 560002) Tj',
    '/F1 14 Tf  0 -28 Td  (HOSPITAL BILL - DUPLICATE DETECTION TEST) Tj',
    '/F1 10 Tf  0 -20 Td  (Bill Number     : CGH/BILL/2024/12456) Tj',
    '0 -16 Td  (NOTE: Same bill number as hospital-bill.pdf - fraud test) Tj',
    '0 -16 Td  (Patient Name    : Rajesh Kumar Sharma) Tj',
    '0 -16 Td  (Claimed Amount  : Rs 92,500) Tj',
    'ET',
  ].join('\n');

  const streamLen = Buffer.byteLength(lines, 'utf8');
  return [
    '%PDF-1.4',
    '1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj',
    '2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj',
    `3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj`,
    `4 0 obj<</Length ${streamLen}>>stream`,
    lines,
    'endstream',
    'endobj',
    '5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj',
    'xref',
    '0 6',
    '0000000000 65535 f ',
    '0000000009 00000 n ',
    '0000000058 00000 n ',
    '0000000115 00000 n ',
    '0000000274 00000 n ',
    '0000000500 00000 n ',
    `trailer<</Size 6/Root 1 0 R>>`,
    'startxref',
    '580',
    '%%EOF',
  ].join('\n');
}

// ── 3. Discharge Summary PDF ──────────────────────────────────────────────────

function generateDischargeSummaryPdf() {
  const lines = [
    'BT',
    '/F1 16 Tf  50 760 Td  (CITY GENERAL HOSPITAL) Tj',
    '/F1 14 Tf  0 -25 Td  (DISCHARGE SUMMARY) Tj',
    '/F1 10 Tf  0 -22 Td  (Patient: Rajesh Kumar Sharma | DOB: 15-Aug-1979 | MRN: CGH-2024-08744) Tj',
    '0 -18 Td  (Admission: 15-Mar-2024 | Discharge: 22-Mar-2024 | Duration: 7 days) Tj',
    '0 -22 Td  (FINAL DIAGNOSIS) Tj',
    '0 -16 Td  (1. Essential Hypertension (I10) - Well controlled on medication) Tj',
    '0 -16 Td  (2. Type 2 Diabetes Mellitus (E11.9) - Managed with oral hypoglycemics) Tj',
    '0 -22 Td  (TREATMENT GIVEN) Tj',
    '0 -16 Td  (- IV Antihypertensives followed by oral medication titration) Tj',
    '0 -16 Td  (- Coronary Angiography: Normal coronary arteries, no intervention needed) Tj',
    '0 -16 Td  (- Diabetic diet counselling and glucose monitoring) Tj',
    '0 -16 Td  (- Lifestyle modification counselling) Tj',
    '0 -22 Td  (DISCHARGE MEDICATIONS) Tj',
    '0 -16 Td  (1. Tab Amlodipine 5mg - OD for 3 months) Tj',
    '0 -16 Td  (2. Tab Metformin 500mg - BD with meals) Tj',
    '0 -16 Td  (3. Tab Atorvastatin 20mg - HS) Tj',
    '0 -22 Td  (FOLLOW UP: After 4 weeks with Cardiologist & Diabetologist) Tj',
    '0 -22 Td  (Treating Physician: Dr. Priya Nair, MD, DM (Cardiology)) Tj',
    '0 -16 Td  (Reg No: KMC/DOC/2010/CR2345     Date: 22-Mar-2024) Tj',
    'ET',
  ].join('\n');

  const streamLen = Buffer.byteLength(lines, 'utf8');
  return [
    '%PDF-1.4',
    '1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj',
    '2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj',
    `3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj`,
    `4 0 obj<</Length ${streamLen}>>stream`,
    lines,
    'endstream',
    'endobj',
    '5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj',
    'xref',
    '0 6',
    '0000000000 65535 f ',
    '0000000009 00000 n ',
    '0000000058 00000 n ',
    '0000000115 00000 n ',
    '0000000274 00000 n ',
    '0000000650 00000 n ',
    `trailer<</Size 6/Root 1 0 R>>`,
    'startxref',
    '730',
    '%%EOF',
  ].join('\n');
}

// ── 4. Handwritten Claim Form (TXT simulation) ────────────────────────────────

function generateClaimFormTxt() {
  return `
╔══════════════════════════════════════════════════════════════════╗
║        FMG ClaimSys — INSURANCE CLAIM FORM                      ║
║        Form Ref: CLM/2024/TEST/001   Date: ${new Date().toLocaleDateString('en-IN')}        ║
╚══════════════════════════════════════════════════════════════════╝

SECTION A: POLICYHOLDER DETAILS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Policy Number       : STAR/2024/IND/001234
Policyholder Name   : Rajesh Kumar Sharma
Date of Birth       : 15-08-1979
Mobile Number       : 9876543210
Email Address       : rajesh.sharma@email.com
Address             : 45, 2nd Cross, Jayanagar
                      Bangalore - 560041, Karnataka

SECTION B: PATIENT DETAILS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Patient Name        : Rajesh Kumar Sharma
Relationship        : Self
Age / Gender        : 45 Years / Male

SECTION C: HOSPITALIZATION DETAILS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Hospital Name       : City General Hospital
Hospital Address    : 123 Medical Avenue, Bangalore - 560001
Hospital Reg No     : KAR/HOSP/2015/4521
Admission Date      : 15-03-2024
Discharge Date      : 22-03-2024
Total Days          : 7 Days
Type of Admission   : Emergency → Planned

SECTION D: DIAGNOSIS & TREATMENT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Primary Diagnosis   : Essential (Primary) Hypertension
ICD-10 Code         : I10
Secondary Diagnosis : Type 2 Diabetes Mellitus (E11.9)
Treating Doctor     : Dr. Priya Nair, MD (DM Cardiology)
Doctor Reg No       : KMC/DOC/2010/CR2345

SECTION E: CLAIM AMOUNT DETAILS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Total Hospital Bill : ₹ 92,500.00
Deductible Amount   : ₹      0.00
Claimed Amount      : ₹ 92,500.00
Bill Number         : CGH/BILL/2024/12456
Bill Date           : 22-03-2024

SECTION F: BANK DETAILS (Reimbursement)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Account Holder      : Rajesh Kumar Sharma
Bank Name           : State Bank of India
Branch              : Jayanagar, Bangalore
Account Number      : XXXX XXXX 4521
IFSC Code           : SBIN0010234

SECTION G: DOCUMENTS ATTACHED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[✓] Original Hospital Bill
[✓] Discharge Summary
[✓] Doctor Prescriptions
[✓] Lab Reports
[✓] Pre-Authorization Letter
[ ] Previous Policy Documents (Not Applicable)

DECLARATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
I hereby declare that all information provided is true and correct.

Signature: ___________________     Date: 22-03-2024
Name: Rajesh Kumar Sharma

FOR OFFICE USE ONLY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Received By: _______________   Date: __________
Claim ID   : _______________   Status: PENDING
`.trim();
}

// ── Generate all documents ────────────────────────────────────────────────────

const files = [
  {
    name: 'hospital-bill.pdf',
    content: generateHospitalBillPdf(),
    encoding: 'utf8',
    purpose: 'Main hospital bill - OCR + fraud detection test',
  },
  {
    name: 'discharge-summary.pdf',
    content: generateDischargeSummaryPdf(),
    encoding: 'utf8',
    purpose: 'Medical discharge summary for claim upload',
  },
  {
    name: 'duplicate-bill.pdf',
    content: generateDuplicateBillPdf(),
    encoding: 'utf8',
    purpose: 'Duplicate bill number - fraud detection test',
  },
  {
    name: 'claim-form.txt',
    content: generateClaimFormTxt(),
    encoding: 'utf8',
    purpose: 'Handwritten claim form simulation',
  },
];

files.forEach(({ name, content, encoding, purpose }) => {
  const filePath = path.join(OUTPUT_DIR, name);
  fs.writeFileSync(filePath, content, { encoding });
  const size = fs.statSync(filePath).size;
  console.log(`✅ ${name.padEnd(25)} ${(size / 1024).toFixed(1).padStart(6)} KB  — ${purpose}`);
});

// ── test-data.json (metadata used by E2E tests) ──────────────────────────────

const testData = {
  generated: new Date().toISOString(),
  documents: files.map(f => ({
    file: f.name,
    path: path.join(OUTPUT_DIR, f.name),
    purpose: f.purpose,
  })),
  claimData: {
    patientName: 'Rajesh Kumar Sharma',
    policyNumber: 'STAR/2024/IND/001234',
    hospitalName: 'City General Hospital',
    admissionDate: '2024-03-15',
    dischargeDate: '2024-03-22',
    icdCode: 'I10',
    diagnosis: 'Essential hypertension',
    billNumber: 'CGH/BILL/2024/12456',
    billDate: '2024-03-22',
    totalBillAmount: 92500,
    claimedAmount: 92500,
    claimType: 'HOSPITALIZATION',
  },
};

const metaPath = path.join(OUTPUT_DIR, 'test-data.json');
fs.writeFileSync(metaPath, JSON.stringify(testData, null, 2));
console.log(`✅ ${'test-data.json'.padEnd(25)} metadata`);
console.log('\n🎉 All test documents generated in: ' + OUTPUT_DIR);
