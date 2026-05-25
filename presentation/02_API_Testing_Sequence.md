# 02 - API Testing Sequence

## Test Data To Prepare

Create separate Postman variables:

```text
baseUrl = http://localhost:8080
patientEmail
patientPassword
carrierEmail
carrierPassword
adminEmail
adminPassword
patientAccessToken
patientRefreshToken
carrierAccessToken
adminAccessToken
patientOtp
carrierOtp
claimId
documentId
carrierId
ruleId
ruleKey
razorpay_order_id
razorpay_payment_id
razorpay_signature
paymentId
notificationId
eventId
```

OTP values are delivered through the configured email/Redis flow. If testing locally without email access, inspect the Redis value or use test seed/demo users where available.

## Exact Happy-Path API Execution Order

| Step | API | Auth | Required Data | Saves/Generates | Next Dependent APIs |
|---:|---|---|---|---|---|
| 1 | `POST /api/v1/auth/patient/register` | Public | patient registration body | pending patient + OTP | patient verify |
| 2 | `PATCH /api/v1/auth/patient/verify` | Public | `patientEmail`, `patientOtp` | active patient user | patient login |
| 3 | `POST /api/v1/auth/login` | Public | patient credentials | `patientAccessToken`, `patientRefreshToken` | claim, files, payment |
| 4 | `GET /api/v1/users/profile` | Patient | bearer token | patient profile | sanity check |
| 5 | `POST /api/v1/auth/carrier/register` | Public | carrier registration body | pending carrier + OTP | carrier verify |
| 6 | `PATCH /api/v1/auth/carrier/verify` | Public | `carrierEmail`, `carrierOtp` | carrier user + carrier entity | admin carrier approval |
| 7 | `POST /api/v1/auth/login` | Public | admin credentials | `adminAccessToken` | admin APIs |
| 8 | `GET /api/v1/admin/carriers` | Admin | optional filters | `carrierId` | approve carrier, assign carrier |
| 9 | `PATCH /api/v1/admin/carriers/{carrierId}/approve` | Admin | `carrierId` | active carrier | carrier login, assignment |
| 10 | `POST /api/v1/auth/login` | Public | carrier credentials | `carrierAccessToken` | carrier claim APIs |
| 11 | `GET /api/v1/medical/codes/lookup?code={icd}` | Any authenticated | ICD code | code metadata | optional validation |
| 12 | `POST /api/v1/medical/validate` | Any authenticated | ICD, diagnosis, amount | medical validation result | claim payload confidence |
| 13 | `POST /api/v1/ai/validate-claim` | Any authenticated | AI validation body | AI result | claim payload confidence |
| 14 | `POST /api/v1/ai/validate-document` | Any authenticated | multipart file + type | document validation result | document upload confidence |
| 15 | `POST /api/v1/claims` | Patient | `ClaimRequest` | `claimId`, status `SUBMITTED` | file upload, admin review, claim reads |
| 16 | `GET /api/v1/claims/{claimId}` | Patient/Admin/etc. | `claimId` | claim detail | verify created state |
| 17 | `POST /api/v1/files/upload` | Patient | `claimId`, file, documentType | `documentId` | get/download/list docs |
| 18 | `GET /api/v1/files/claim/{claimId}` | Patient/Admin/Specialist | `claimId` | document list | document assertions |
| 19 | `GET /api/v1/files/{documentId}` | Patient/Admin/Specialist | `documentId` | metadata | download |
| 20 | `GET /api/v1/files/download/{documentId}` | Patient/Admin/Specialist | `documentId` | file bytes | file verification |
| 21 | `POST /api/v1/ai/analyze/{claimId}` | Admin/Specialist/Patient | `claimId`, optional prompt | AI analysis | admin decision support |
| 22 | `POST /api/v1/ai/claims/{claimId}/generate-summary` | Admin/Carrier/Patient | `claimId` | summary | presentation/demo |
| 23 | `POST /api/v1/rules/seed` | Admin | none | default rules | evaluate/simulate |
| 24 | `GET /api/v1/rules/active` | Admin | none | active rule list | rule audit/evaluation |
| 25 | `POST /api/v1/rules/simulate` | Admin | `ClaimRequest` | simulated decision + audit | simulation audit |
| 26 | `POST /api/v1/rules/evaluate` | Admin | `ClaimRequest` | decision + audit | manual/admin decision support |
| 27 | `GET /api/v1/rules/audits/simulations` | Admin | none | simulation audit | audit validation |
| 28 | `GET /api/v1/admin/claims` | Admin | optional filters | claim queue | admin review |
| 29 | `PATCH /api/v1/admin/claims/{claimId}/approve` | Admin | `claimId`, optional reason | status `ADMIN_APPROVED` | carrier approval/payment |
| 30 | `PATCH /api/v1/admin/claims/{claimId}/assign-carrier` | Admin | body `{ "carrierId": n }` | claim linked to carrier | carrier queue |
| 31 | `GET /api/v1/carrier/claims` | Carrier | carrier token | assigned claims | carrier detail |
| 32 | `GET /api/v1/carrier/claims/{claimId}` | Carrier | assigned `claimId` | carrier claim detail | validate/approve |
| 33 | `GET /api/v1/carrier/claims/{claimId}/policy-status` | Carrier | assigned `claimId` | policy status | validate policy |
| 34 | `PATCH /api/v1/carrier/claims/{claimId}/validate` | Carrier | assigned `claimId` | validation note | approve/reject |
| 35 | `PATCH /api/v1/carrier/claims/{claimId}/remark` | Carrier | body `{ "remark": "..." }` | review note | optional |
| 36 | `POST /api/v1/carrier/claims/{claimId}/ai-analyze` | Carrier | optional prompt | AI analysis | optional |
| 37 | `PATCH /api/v1/carrier/claims/{claimId}/approve` | Carrier | claim status must be `ADMIN_APPROVED` | status `CARRIER_APPROVED` | payment |
| 38 | `GET /api/v1/claims/{claimId}/timeline` | Any claim role | `claimId` | lifecycle events | audit evidence |
| 39 | `GET /api/v1/claims/{claimId}/audits` | Any claim role | `claimId` | claim audits | audit evidence |
| 40 | `POST /api/v1/payments/create-order` | Patient/Admin | `claimId`, amount | `razorpay_order_id`, payment row, status `PAYMENT_PENDING` | verify payment |
| 41 | `POST /api/v1/payments/verify` | Patient/Admin | Razorpay order/payment/signature | `paymentId`, status `SUCCESS`, claim `SETTLED` | payment/audit/analytics |
| 42 | `GET /api/v1/payments/claim/{claimId}` | Patient/Admin/Carrier | `claimId` | payment detail | payment assertions |
| 43 | `GET /api/v1/notifications` | Any authenticated | token | notification list + IDs | read notification |
| 44 | `GET /api/v1/notifications/unread-count` | Any authenticated | token | unread count | mark read |
| 45 | `PATCH /api/v1/notifications/{notificationId}/read` | Any authenticated | notification id | one notification read | read assertions |
| 46 | `POST /api/v1/notifications/mark-read` | Any authenticated | token | all notifications read | read assertions |
| 47 | `GET /api/v1/audit/claims/{claimId}` | Admin | `claimId` | forensic audit | compliance evidence |
| 48 | `GET /api/v1/audit/claims/{claimId}/verify` | Admin | `claimId` | hash-chain integrity result | audit evidence |
| 49 | `GET /api/v1/audit/payments/claim/{claimId}` | Admin | `claimId` | payment ledger | payment evidence |
| 50 | `GET /api/v1/audit/payments/reconcile` | Admin | none | reconciliation result | finance evidence |
| 51 | `GET /api/v1/fraud/admin/dashboard` | Admin | claims/risk data | fraud metrics | fraud evidence |
| 52 | `GET /api/v1/fraud/carrier/dashboard` | Carrier | assigned claims | carrier fraud view | carrier evidence |
| 53 | `GET /api/v1/analytics/dashboard` | Admin/Carrier | data exists | dashboard metrics | analytics evidence |
| 54 | `GET /api/v1/admin/kafka/health` | Admin | event audit data | pipeline status | operations evidence |
| 55 | `POST /api/v1/auth/logout` | Authenticated | bearer token | refresh token removed | session close |

## Alternate And Negative Business Paths

### Admin Rejects Claim

```text
Create claim
-> optional upload/AI/rules
-> PATCH /api/v1/admin/claims/{claimId}/reject?reason=...
-> status REJECTED
-> payment create-order must fail
-> claim mutation after terminal state should fail
```

### Carrier Rejects Claim

```text
Create claim
-> admin approve
-> admin assign carrier
-> carrier reject
-> status REJECTED
-> payment create-order must fail
```

### Carrier Flags Claim As Suspicious

```text
Create claim
-> admin approve
-> admin assign carrier
-> PATCH /api/v1/carrier/claims/{claimId}/flag
-> risk score/risk flags updated
-> fraud dashboards should reflect risk
-> admin can call PATCH /api/v1/fraud/admin/claims/{claimId}/safe
```

### Bulk Approval Alternate Flow

```text
Create one or more claims
-> POST /api/v1/claims/bulk-approve with [claimId]
-> service sets status APPROVED
-> service calls initiateInstantPayout
-> payment row is mocked SUCCESS
-> claim becomes SETTLED
```

Important caveat: `APPROVED` is not a normal transition in `ClaimStateMachine`. Treat this as a special operational shortcut, not the standard claim lifecycle.

## APIs That Generate IDs Or Tokens

| API | Generated Value | Used By |
|---|---|---|
| `POST /api/v1/auth/login` | access token, refresh token | all secured APIs, refresh/logout |
| `GET /api/v1/users/profile` | current user id/email/role/status | sanity checks |
| `GET /api/v1/admin/carriers` | `carrierId` | approve/reject carrier, assign claim |
| `POST /api/v1/claims` | `claimId` | files, AI claim analysis, admin, carrier, payment, audit |
| `POST /api/v1/files/upload` | `documentId` | document get/download |
| `POST /api/v1/rules` | `ruleId`, `ruleKey` | update/delete/activate/deactivate/audits |
| `POST /api/v1/payments/create-order` | `razorpay_order_id` | payment verification |
| `POST /api/v1/payments/verify` | payment id/status | payment ledger audit |
| `GET /api/v1/notifications` | notification id | mark one read |
| `GET /api/v1/admin/kafka/dlq` | event id | DLQ retry |

## Required Assertions Per Major Step

- Login returns token and role expected for the user.
- Unauthorized calls return 401.
- Wrong-role calls return 403.
- Claim creation returns `claimStatus = SUBMITTED`.
- Admin approval returns or subsequently shows `claimStatus = ADMIN_APPROVED`.
- Carrier approval requires `ADMIN_APPROVED` and returns or subsequently shows `CARRIER_APPROVED`.
- Payment create-order fails before approval and succeeds after approval.
- Payment verify changes claim to `SETTLED`.
- Audit and timeline show expected state changes.
- Notifications are created after admin/carrier status changes.

