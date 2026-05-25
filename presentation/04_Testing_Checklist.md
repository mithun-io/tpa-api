# 04 - Testing Checklist

Use `[ ]` while planning, `[x]` after execution, and add defect IDs beside failed checks.

## Global Security Checks

- [ ] Call every non-auth endpoint without token and expect 401.
- [ ] Call admin endpoints with patient token and expect 403.
- [ ] Call carrier endpoints with patient/admin token and expect 403 unless explicitly allowed.
- [ ] Call patient-only endpoints with admin/carrier token and expect 403.
- [ ] Use expired/invalid JWT and expect 401.
- [ ] Confirm `/api/v1/auth/**`, Swagger/OpenAPI, and actuator endpoints follow `SecurityConfig` public access rules.
- [ ] Confirm rules and Kafka monitor APIs are ADMIN-protected in the current role model.

## AuthController - `/api/v1/auth`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/patient/register` | Valid patient body sends OTP | duplicate email, invalid email, invalid US phone, weak password, missing DOB |
| [ ] | PATCH | `/patient/verify` | Correct email + 6-digit OTP creates active patient | wrong OTP, expired OTP, non-6-digit OTP, unknown email |
| [ ] | POST | `/carrier/register` | Valid carrier body sends OTP | duplicate company/email, invalid phone, missing license/tax/registration |
| [ ] | PATCH | `/carrier/verify` | Correct OTP creates carrier user/entity | wrong/expired OTP, unknown email |
| [ ] | PATCH | `/resend-otp/{email}` | Resends OTP for pending user | unknown email, already verified email |
| [ ] | POST | `/login` | Valid credentials return access/refresh token | invalid credentials, unverified user, blocked user |
| [ ] | POST | `/refresh` | Valid refresh returns new token set | invalid/expired refresh token |
| [ ] | POST | `/logout` | Authenticated logout removes refresh tokens | no token, invalid token |
| [ ] | PATCH | `/forget-password/{email}` | Sends reset OTP | unknown email |
| [ ] | PATCH | `/password-reset` | Correct OTP changes password | weak password, wrong OTP |
| [ ] | PATCH | `/password-change` | Authenticated user changes password | no token/null principal, wrong old password, weak new password |

## UserController - `/api/v1/users`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/profile` | Current token returns profile | no token, deleted/blocked user |

## ClaimController - `/api/v1/claims`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/` | Patient creates claim; status `SUBMITTED` | non-patient role, missing policy fields, negative amount, invalid dates |
| [ ] | GET | `/` | Role gets visible claim page | no token, pagination boundary |
| [ ] | GET | `/{claimId}` | Authorized role fetches claim | unknown claim, wrong patient/carrier access |
| [ ] | GET | `/search` | Filter by status/date/amount | invalid status, invalid date, min > max |
| [ ] | GET | `/{claimId}/audits` | Fetch claim audit list | unknown claim, unauthorized role |
| [ ] | GET | `/{claimId}/timeline` | Fetch ordered timeline | unknown claim |
| [ ] | GET | `/{claimId}/export` | Download PDF report | unknown claim, unauthorized access |
| [ ] | PUT | `/{claimId}/carrier-approve` | Carrier approves assigned/admin-approved claim | before admin approval, unassigned carrier, terminal claim |
| [ ] | DELETE | `/{claimId}` | Patient/admin deletes `SUBMITTED` or `UNDER_REVIEW` claim | approved/settled/rejected claim, wrong patient |
| [ ] | POST | `/bulk-approve` | Admin/specialist bulk approves claim ids | non-admin/specialist, invalid ids, already paid claim |
| [ ] | GET | `/{claimId}/queries` | Fetch query thread | unknown claim, unauthorized access |
| [ ] | POST | `/{claimId}/queries` | Create claim query message | blank message, unauthorized access |
| [ ] | POST | `/broadcast/{claimId}` | Admin/specialist broadcasts status | missing status, unauthorized role |
| [ ] | POST | `/notify` | Admin/specialist sends user notification | unknown email, missing title/message |

## FileUploadController - `/api/v1/files`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/upload` | Patient uploads document for existing claim | no claim, invalid file type/size, non-patient |
| [ ] | POST | `/upload/multiple` | Patient uploads multiple documents | empty list, one invalid file, non-patient |
| [ ] | GET | `/{documentId}` | Fetch document metadata | unknown doc, unauthorized role |
| [ ] | GET | `/download/{documentId}` | Download stored document | missing file on disk, unauthorized role |
| [ ] | GET | `/claim/{claimId}` | List documents for claim | unknown claim, wrong owner |

## AiClaimAssistantController - `/api/v1/ai`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/validate-document` | Authenticated multipart validation | missing file, missing documentType, unsupported type |
| [ ] | POST | `/validate-claim` | Authenticated pre-claim AI validation | missing patient/hospital/policy/amount/dates |
| [ ] | POST | `/analyze/{claimId}` | Analyze existing claim | unknown claim, role not allowed |
| [ ] | POST | `/claims/{id}/generate-summary` | Generate summary | unknown claim, role not allowed |

## MedicalValidationController - `/api/v1/medical`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/codes/lookup` | Lookup known ICD code | missing/unknown code |
| [ ] | POST | `/validate` | Validate ICD/diagnosis/amount | blank ICD, huge/negative amount |
| [ ] | POST | `/validate/batch` | Admin/carrier validates list | empty list, patient token |
| [ ] | GET | `/upcoding/risk` | Admin/carrier checks risk | missing ICD, patient token |
| [ ] | GET | `/high-risk/codes` | Admin/carrier gets high-risk list | patient token |

## RuleEngineController - `/api/v1/rules`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/seed` | Admin seeds default rules | non-admin token |
| [ ] | GET | `/` | Admin lists rules | no token, non-admin |
| [ ] | GET | `/active` | Admin lists active rules | non-admin |
| [ ] | POST | `/` | Admin creates rule | duplicate ruleKey, invalid ruleType, missing priority |
| [ ] | GET | `/{id}` | Admin fetches rule | unknown id |
| [ ] | PUT | `/{id}` | Admin updates rule | unknown id, invalid script/type |
| [ ] | PATCH | `/{id}/activate` | Admin activates rule | unknown id, already active |
| [ ] | PATCH | `/{id}/deactivate` | Admin deactivates rule | unknown id, already inactive |
| [ ] | DELETE | `/{id}` | Admin deletes rule | unknown id |
| [ ] | POST | `/simulate` | Admin simulates claim decision | invalid claim payload |
| [ ] | POST | `/evaluate` | Admin evaluates claim request | invalid claim payload |
| [ ] | GET | `/audits/simulations` | Admin views simulations | no audits case |
| [ ] | GET | `/audits/rules/{ruleKey}` | Admin views rule audits | unknown ruleKey |
| [ ] | GET | `/audits/claims/{claimId}` | Admin views claim rule audits | claim with no audits |

## AdminController - `/api/v1/admin`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/users` | Admin lists users | non-admin, search boundary |
| [ ] | PATCH | `/users/{id}/block` | Admin blocks non-admin user | block admin user, unknown id |
| [ ] | PATCH | `/users/{id}/unblock` | Admin unblocks blocked user | unknown id |
| [ ] | GET | `/patients` | Admin lists patients | invalid sort field/page |
| [ ] | GET | `/carriers` | Admin lists carriers | invalid filters/page |
| [ ] | PATCH | `/carriers/{id}/approve` | Admin approves carrier | unknown carrier, already active |
| [ ] | PATCH | `/carriers/{id}/reject` | Admin rejects carrier | unknown carrier |
| [ ] | GET | `/claims` | Admin lists claims | invalid status/date/sort |
| [ ] | PATCH | `/claims/review` | Admin transitions claim using request status | invalid transition, missing notes |
| [ ] | PATCH | `/claims/{id}/approve` | Admin approves valid claim | terminal claim, invalid transition |
| [ ] | PATCH | `/claims/{id}/reject` | Admin rejects valid claim | missing reason, terminal claim |
| [ ] | PATCH | `/claims/{id}/assign-carrier` | Assign active carrier | inactive carrier, missing carrierId |
| [ ] | GET | `/claims/{id}/ai-summary` | Get claim AI summary | unknown claim |
| [ ] | POST | `/claims/{id}/ai-chat` | Ask AI about claim | unknown claim, missing prompt uses default |
| [ ] | GET | `/monitoring` | Admin system monitoring | non-admin |

## CarrierController - `/api/v1/carrier`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/claims` | Carrier lists assigned claims | no assignment |
| [ ] | GET | `/claims/{id}` | Carrier views assigned claim | unassigned claim, unknown claim |
| [ ] | GET | `/claims/{id}/policy-status` | Carrier gets policy status | unassigned claim |
| [ ] | PATCH | `/claims/{id}/validate` | Carrier validates policy | unassigned/terminal claim |
| [ ] | PATCH | `/claims/{id}/remark` | Carrier adds remark | blank remark, unassigned claim |
| [ ] | PATCH | `/claims/{id}/flag` | Carrier flags suspicious | unassigned/terminal claim |
| [ ] | POST | `/claims/{id}/ai-analyze` | Carrier AI analyzes assigned claim | unassigned claim |
| [ ] | PATCH | `/claims/{id}/approve` | Carrier approves `ADMIN_APPROVED` claim | before admin approval, terminal claim |
| [ ] | PATCH | `/claims/{id}/reject` | Carrier rejects assigned claim | terminal claim |

## PaymentController - `/api/v1/payments`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | POST | `/create-order` | Patient/admin creates order for approved claim | unapproved/rejected/settled claim, negative amount |
| [ ] | POST | `/verify` | Valid Razorpay signature settles claim | invalid signature, unknown order id |
| [ ] | GET | `/claim/{claimId}` | Patient/admin/carrier gets payment | no payment, wrong role |

## NotificationController - `/api/v1/notifications`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/` | User lists own notifications | no token |
| [ ] | GET | `/unread-count` | User gets unread count | no token |
| [ ] | PATCH | `/{id}/read` | User marks own notification read | another user's notification, unknown id |
| [ ] | POST | `/mark-read` | User marks all read | no token |

## FraudDetectionController - `/api/v1/fraud`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/admin/dashboard` | Admin fraud dashboard | carrier/patient token |
| [ ] | GET | `/carrier/dashboard` | Carrier fraud dashboard | admin/patient token where not allowed |
| [ ] | PATCH | `/admin/claims/{id}/safe` | Admin clears risk fields | unknown claim, non-admin |

## AuditLogController - `/api/v1/audit`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/claims/{claimId}` | Admin gets claim audit trail | non-admin, unknown claim |
| [ ] | GET | `/claims/{claimId}/action/{action}` | Filter by action | unknown action |
| [ ] | GET | `/range` | Query audit by ISO date range | invalid dates, from after to |
| [ ] | GET | `/claims/{claimId}/verify` | Verify hash chain | unknown claim |
| [ ] | GET | `/events/claim/{claimId}` | Event logs by claim | no events case |
| [ ] | GET | `/events/stage/{stage}` | Event logs by stage | unknown stage |
| [ ] | GET | `/events/unprocessed` | List unprocessed events | empty case |
| [ ] | GET | `/payments/claim/{claimId}` | Ledger by claim | no payment |
| [ ] | GET | `/payments/payment/{paymentId}` | Ledger by payment id | unknown payment |
| [ ] | GET | `/payments/event/{eventType}` | Ledger by event type | invalid enum |
| [ ] | GET | `/payments/reconcile` | Reconcile payments | no payments case |

## AnalyticsController - `/api/v1/analytics`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/dashboard` | Admin/carrier dashboard | patient token |
| [ ] | GET | `/fraud/trends` | Fraud trends | patient token |
| [ ] | GET | `/sla/performance` | SLA performance | patient token |
| [ ] | GET | `/leakage` | Claim leakage | patient token |
| [ ] | GET | `/hospitals` | Hospital analytics | patient token |
| [ ] | GET | `/forecast` | Forecast | patient token |
| [ ] | GET | `/payments/summary` | Payment summary | patient token |
| [ ] | GET | `/loss-ratio` | Loss ratio | patient token |
| [ ] | GET | `/carrier/{carrierName}/summary` | Carrier summary | unknown carrier, encoded names |

## KafkaMonitorController - `/api/v1/admin/kafka`

| Done | Method | Endpoint | Positive Test | Negative / Boundary / Security Test |
|---|---|---|---|---|
| [ ] | GET | `/topics` | Admin gets topic list | non-admin |
| [ ] | GET | `/health` | Admin gets pipeline health | non-admin |
| [ ] | GET | `/dlq` | Admin gets DLQ messages | page/size boundaries |
| [ ] | GET | `/pending` | Admin gets pending events | no pending events |
| [ ] | POST | `/dlq/{eventId}/retry` | Admin retries DLQ event | unknown eventId, non-DLQ event |

