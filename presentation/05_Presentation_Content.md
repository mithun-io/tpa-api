# 05 - Presentation Content

## One-Line Product Summary

This Spring Boot application manages a TPA insurance claim lifecycle from patient onboarding and claim submission through document validation, AI/rule review, admin and carrier approval, payment settlement, audit, notifications, fraud monitoring, analytics, and Kafka-based operational tracking.

## Architecture Explanation

The application is layered around standard Spring Boot patterns:

```text
Controller Layer
  -> validates HTTP shape and role access
  -> delegates to service interfaces

Service Layer
  -> contains business transitions
  -> coordinates repositories, audit, Kafka, payment, AI, file storage, notifications

Repository Layer
  -> Spring Data JPA repositories
  -> queries users, carriers, claims, documents, payments, rules, notifications, audit/event logs

Entity Layer
  -> User, Patient, Carrier, Claim, ClaimDocument, Payment, PaymentLedger, RuleConfig, RuleExecutionAudit, Notification, AuditLog, EventAuditLog

Security Layer
  -> JWT stateless auth
  -> role-based authorization using ADMIN, PATIENT, CARRIER, SPECIALIST

Integration Modules
  -> Razorpay payment
  -> Kafka lifecycle/event monitoring
  -> Redis OTP/pending registration
  -> file system storage
  -> AI claim/document assistant
  -> email notifications
```

## Key Entity Relationships

```text
User 1--1 Patient
User 1--1 Carrier
User 1--many RefreshToken
User 1--many Notification

Claim many--1 User
Claim many--1 Carrier
Claim 1--many ClaimDocument
Claim 1--many ClaimAudit
Claim 1--many ClaimQuery
Claim 1--many timeline/audit/payment-ledger references by claimId

Payment many--1 logical Claim by claimId
PaymentLedger many--1 logical Payment by paymentId

RuleConfig 1--many RuleExecutionAudit by ruleKey/version
EventAuditLog records Kafka pipeline events by claimId/eventId/stage
```

## Business Flow Explanation

### 1. Onboarding

The patient and carrier registration flows are OTP-based. Registration stores a pending request and sends OTP. Verification creates the real user record. Patient users can immediately create claims. Carrier users must be approved by an admin before they can be assigned claims.

### 2. Claim Intake

A patient submits a claim through `POST /api/v1/claims`. The service creates a `Claim`, links the patient, optionally links a carrier by company name, sets status `SUBMITTED`, and logs `CLAIM_CREATED`.

### 3. Document And AI Support

Documents are uploaded after a claim exists. The file module stores files, creates document metadata, and can participate in review/risk processing. AI endpoints can validate documents, validate raw claim data, analyze persisted claims, and generate summaries for patient/admin/carrier use.

### 4. Rules And Review

Rules are managed by admin users. Rule evaluation returns claim decisions and records execution audit. Direct REST rule evaluation works from `ClaimRequest`; Kafka-driven evaluation can include a `claimId` and update saved claim status through `ClaimServiceImpl.processClaimDecision`.

### 5. Admin Decision

Admin users review, approve, reject, and assign claims. Admin approval moves a claim to `ADMIN_APPROVED`. Assignment links an active carrier to the claim. Rejection moves the claim to terminal `REJECTED`.

### 6. Carrier Processing

Carriers see only assigned claims. A carrier can validate policy, add remarks, flag suspicious claims, run AI analysis, approve, or reject. Carrier approval requires status `ADMIN_APPROVED` and moves the claim to `CARRIER_APPROVED`.

### 7. Payment Settlement

Payment starts only after `ADMIN_APPROVED` or `CARRIER_APPROVED`. Creating a Razorpay order writes a payment record, payment ledger row, and moves claim to `PAYMENT_PENDING`. Successful signature verification marks payment `SUCCESS` and claim `SETTLED`.

### 8. Monitoring And Compliance

Notifications are generated during review and carrier actions. Audit APIs expose claim status history, hash-chain verification, event logs, and payment ledger. Analytics aggregates claim, payment, fraud, SLA, hospital, forecast, and carrier metrics. Kafka monitor APIs expose pipeline topics, health, pending events, DLQ, and retries.

## Demo Flow For Presentation

Use a short, real happy path:

```text
1. Login as admin and show admin dashboard/users.
2. Register or use existing patient.
3. Register or use existing carrier.
4. Admin approves carrier.
5. Patient logs in and creates a claim.
6. Patient uploads claim document.
7. Run AI document validation and AI claim summary.
8. Admin reviews and approves the claim.
9. Admin assigns the active carrier.
10. Carrier logs in and sees the assigned claim.
11. Carrier validates policy and approves the claim.
12. Patient/admin creates payment order.
13. Verify payment and show claim as SETTLED.
14. Show notifications, audit trail, payment ledger, fraud dashboard, analytics dashboard.
15. Show Kafka health/DLQ monitor for operational visibility.
```

## Client Demo Talking Points

- The system is role-driven: patient, admin, carrier, and specialist users see different operations.
- JWT makes the API stateless; refresh tokens support session continuity.
- OTP verification prevents unverified accounts from entering the business workflow.
- Carrier approval is controlled by admin before carriers can process claims.
- Claim status transitions are controlled by `ClaimStateMachine`, preventing invalid moves such as settled claims being modified.
- AI and medical validation support the claim decision process but are separated from final approval authority.
- Admin approval and carrier approval are distinct business checkpoints.
- Payment is protected by eligibility checks and signature verification.
- Audit logs and payment ledgers create compliance evidence.
- Notifications keep users and admins informed after important claim actions.
- Kafka/event audit gives operational transparency for asynchronous processing.
- Analytics converts claim/payment/fraud data into management views.

## Recommended Demo Assertions

Show these values on screen:

- JWT role from login response.
- Claim status after creation: `SUBMITTED`.
- Claim status after admin approval: `ADMIN_APPROVED`.
- Claim assignment visible in carrier queue.
- Claim status after carrier approval: `CARRIER_APPROVED`.
- Payment order id after create-order.
- Claim status after payment verification: `SETTLED`.
- Audit trail includes claim creation/admin approval/carrier approval/payment events.
- Notification unread count changes after status updates and mark-read.

## Known Implementation Notes To Be Transparent About

- Direct rule evaluation API accepts a claim request body and returns a decision. It does not directly update a persisted claim unless called internally with a claim id.
- Kafka consumers support rule-evaluated and lifecycle event handling, but automatic event publication from claim creation should be verified in runtime tests because the direct service path inspected for claim creation does not visibly publish the claim-created event.
- `APPROVED` exists in `ClaimStatus` and is used by bulk approval, but it is not part of the normal state-machine transition list.
- File upload depends on an existing `claimId`; document-first intake is not supported by the current controller/service design.
- The current code uses `ADMIN` for rules and Kafka monitor access.

## Slide Outline

1. Problem Statement: TPA claim processing needs controlled intake, validation, approval, payment, and audit.
2. User Roles: Patient, Admin, Carrier, Specialist.
3. System Architecture: Controller, service, repository, entity, security, integrations.
4. End-to-End Flow: Register -> verify -> login -> create claim -> upload docs -> review -> carrier -> payment -> audit.
5. Claim Lifecycle: status state machine and terminal states.
6. Validation Layer: medical validation, AI validation, rules.
7. Admin And Carrier Workflow: two-stage approval model.
8. Payment And Settlement: Razorpay order, signature verification, ledger.
9. Compliance And Monitoring: audit, notifications, analytics, Kafka monitor.
10. Demo Path: run the exact API sequence from `02_API_Testing_Sequence.md`.

