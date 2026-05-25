# 03 - API Dependency Map

## Dependency Legend

- Mandatory: required for the normal end-to-end business flow.
- Optional: useful for verification, support, analytics, or alternate flows.
- Generated dependency: an ID/token produced by one API and consumed by later APIs.

## Authentication Dependency

```text
POST /api/v1/auth/patient/register
  -> PATCH /api/v1/auth/patient/verify
    -> POST /api/v1/auth/login
      -> access token
      -> refresh token
      -> all secured patient APIs

POST /api/v1/auth/carrier/register
  -> PATCH /api/v1/auth/carrier/verify
    -> GET /api/v1/admin/carriers
      -> PATCH /api/v1/admin/carriers/{id}/approve
        -> POST /api/v1/auth/login as carrier
          -> all secured carrier APIs

POST /api/v1/auth/login as admin
  -> admin APIs
  -> rules APIs
  -> audit APIs
  -> kafka monitor APIs
```

## Claim Dependency

```text
POST /api/v1/claims
  -> claimId
  -> GET /api/v1/claims/{claimId}
  -> POST /api/v1/files/upload
      -> documentId
      -> GET /api/v1/files/{documentId}
      -> GET /api/v1/files/download/{documentId}
      -> GET /api/v1/files/claim/{claimId}
  -> POST /api/v1/ai/analyze/{claimId}
  -> POST /api/v1/ai/claims/{claimId}/generate-summary
  -> GET /api/v1/admin/claims/{claimId}/ai-summary
  -> POST /api/v1/admin/claims/{claimId}/ai-chat
  -> PATCH /api/v1/admin/claims/{claimId}/approve
  -> PATCH /api/v1/admin/claims/{claimId}/reject
  -> PATCH /api/v1/admin/claims/{claimId}/assign-carrier
  -> GET /api/v1/carrier/claims/{claimId}
  -> PATCH /api/v1/carrier/claims/{claimId}/validate
  -> PATCH /api/v1/carrier/claims/{claimId}/approve
  -> PATCH /api/v1/carrier/claims/{claimId}/reject
  -> POST /api/v1/payments/create-order
  -> GET /api/v1/payments/claim/{claimId}
  -> audit, analytics, fraud, notification verification
```

## Carrier Dependency

```text
Carrier registration + verification
  -> carrier entity
  -> admin approves carrier
  -> carrier status ACTIVE
  -> claim can be assigned to carrier
  -> carrier can see assigned claim
  -> carrier can validate/approve/reject/flag/remark
```

`AdminServiceImpl.assignClaimToCarrier` explicitly rejects inactive carriers.

## Payment Dependency

```text
Claim status ADMIN_APPROVED or CARRIER_APPROVED
  -> POST /api/v1/payments/create-order
    -> Payment status CREATED
    -> Claim status PAYMENT_PENDING
    -> razorpay_order_id
      -> POST /api/v1/payments/verify
        -> Payment status SUCCESS
        -> Claim status SETTLED
        -> PaymentLedger PAYMENT_VERIFIED
```

Payment service blocks:

- Unknown claim.
- Claim not in `ADMIN_APPROVED` or `CARRIER_APPROVED`.
- Duplicate successful/paid payment.
- Invalid Razorpay signature.

## Rule Engine Dependency

```text
POST /api/v1/rules/seed
  -> default RuleConfig rows
  -> GET /api/v1/rules/active
  -> POST /api/v1/rules/simulate
  -> POST /api/v1/rules/evaluate
  -> RuleExecutionAudit rows
  -> GET /api/v1/rules/audits/simulations
  -> GET /api/v1/rules/audits/rules/{ruleKey}
  -> GET /api/v1/rules/audits/claims/{claimId}
```

Important implementation note:

- Rule REST APIs accept `ClaimRequest`.
- Direct `/rules/evaluate` does not take a `claimId`, so it returns a decision and writes rule audit with null claim id unless called internally with a claim id.
- Kafka claim-created consumer can call `evaluateClaim(claimRequest, claimId, false)` and then `ClaimServiceImpl.processClaimDecision`.
- Automatic claim-created event publication from `ClaimServiceImpl.createClaim` was not visible in the inspected code.

## File Upload Dependency

```text
POST /api/v1/claims
  -> claimId
  -> POST /api/v1/files/upload or /upload/multiple
    -> ClaimDocument rows
    -> documentId
    -> storage path
    -> optional claim review/risk changes inside service
```

Documents cannot be uploaded before claim creation because `FileUploadServiceImpl` loads the claim by `claimId`.

## AI Dependency

```text
Pre-claim:
POST /api/v1/ai/validate-claim
POST /api/v1/ai/validate-document

Post-claim:
POST /api/v1/ai/analyze/{claimId}
POST /api/v1/ai/claims/{claimId}/generate-summary
GET /api/v1/admin/claims/{claimId}/ai-summary
POST /api/v1/admin/claims/{claimId}/ai-chat
POST /api/v1/carrier/claims/{claimId}/ai-analyze
```

Pre-claim APIs support validation before persistence. Post-claim APIs depend on `claimId`.

## Medical Dependency

```text
GET /api/v1/medical/codes/lookup
POST /api/v1/medical/validate
POST /api/v1/medical/validate/batch
GET /api/v1/medical/upcoding/risk
GET /api/v1/medical/high-risk/codes
```

Medical validation returns validation/risk information. The controller path does not directly persist changes to a claim.

## Notification Dependency

```text
Admin review / admin approve / admin reject / carrier validate / carrier approve / carrier reject
  -> NotificationServiceImpl.createNotification or notifyAllAdmins
  -> GET /api/v1/notifications
  -> GET /api/v1/notifications/unread-count
  -> PATCH /api/v1/notifications/{id}/read
  -> POST /api/v1/notifications/mark-read
```

Notifications are user-scoped through the authenticated principal.

## Audit Dependency

```text
Claim create/admin review/admin approve/admin reject/carrier approve/payment
  -> AuditLog and PaymentLedger rows
  -> GET /api/v1/audit/claims/{claimId}
  -> GET /api/v1/audit/claims/{claimId}/verify
  -> GET /api/v1/audit/payments/claim/{claimId}
```

Audit APIs are admin-only.

## Mandatory API Groups For Happy Path

- Patient register, verify, login.
- Carrier register, verify, admin approve, carrier login.
- Admin login.
- Claim create.
- File upload.
- Admin approve claim.
- Admin assign carrier.
- Carrier get assigned claim.
- Carrier approve claim.
- Payment create order.
- Payment verify.
- Claim/payment/audit verification.

## Optional API Groups

- Password reset/change/logout/refresh.
- AI pre-validation.
- Medical validation.
- Rules seed/evaluate/simulate.
- Carrier remark/flag/AI analyze.
- Notifications read state.
- Fraud dashboards.
- Analytics dashboards.
- Kafka monitor.
- Export PDF.
- Bulk approval.
- Broadcast/notify.

## Parent-Child API Table

| Parent API | Child API | Dependency |
|---|---|---|
| `POST /auth/login` | all secured APIs | bearer token |
| `POST /claims` | `/files/upload` | `claimId` |
| `POST /files/upload` | `/files/{documentId}`, `/files/download/{documentId}` | `documentId` |
| `POST /claims` | `/ai/analyze/{claimId}` | `claimId` |
| `POST /claims` | `/admin/claims/{id}/approve` | `claimId` |
| `GET /admin/carriers` | `/admin/claims/{id}/assign-carrier` | `carrierId` |
| `/admin/claims/{id}/assign-carrier` | `/carrier/claims/{id}` | assigned claim |
| `/admin/claims/{id}/approve` | `/carrier/claims/{id}/approve` | status must be `ADMIN_APPROVED` |
| `/carrier/claims/{id}/approve` | `/payments/create-order` | status must be `CARRIER_APPROVED` |
| `/payments/create-order` | `/payments/verify` | `razorpay_order_id` |
| `/payments/verify` | `/audit/payments/claim/{claimId}` | payment ledger |
| claim status changes | `/notifications` | notification records |
| claim/payment data | `/analytics/*` | aggregate data |
| Kafka consumers | `/admin/kafka/*` | event audit/DLQ rows |

