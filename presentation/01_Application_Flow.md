# 01 - Application Flow

## Source-Code Basis

This document is derived from the actual Spring Boot source code under `src/main/java/com/tpa`, especially:

- Controllers: `AuthController`, `ClaimController`, `AdminController`, `CarrierController`, `PaymentController`, `RuleEngineController`, `FileUploadController`, `AiClaimAssistantController`, `MedicalValidationController`, `NotificationController`, `AuditLogController`, `AnalyticsController`, `FraudDetectionController`, `KafkaMonitorController`, `UserController`.
- Services: `AuthServiceImpl`, `ClaimServiceImpl`, `AdminServiceImpl`, `CarrierServiceImpl`, `PaymentServiceImpl`, `RuleEngineServiceImpl`, `FileUploadServiceImpl`, `AiClaimAssistantServiceImpl`, `MedicalValidationServiceImpl`, `NotificationServiceImpl`, `AuditLogServiceImpl`, `FraudDetectionServiceImpl`, `KafkaMonitorServiceImpl`.
- Security: `SecurityConfig`, `JwtAuthFilter`, `JwtUtil`, `CustomUserDetails`, `CustomUserDetailsService`, `TenantFilter`.
- Persistence: repositories and entities for user, patient, carrier, claim, documents, rules, payments, notifications, audit logs, and Kafka event audit logs.

Where a workflow is not directly connected by service calls, it is marked as an API-supported or optional flow instead of being treated as guaranteed automation.

## Application Start Point

The business start point is authentication and user onboarding through `AuthController`.

The technical request start point is `SecurityConfig`:

1. `/api/v1/auth/**`, `/actuator/**`, Swagger and OpenAPI endpoints are public.
2. Every other endpoint requires JWT authentication.
3. `TenantFilter` runs before `JwtAuthFilter`.
4. `JwtAuthFilter` extracts the bearer token, validates it through `JwtUtil`, loads the user through `CustomUserDetailsService`, and creates Spring Security authorities using `ROLE_` + `UserRole`.

Available roles in the current code are:

- `ADMIN`
- `PATIENT`
- `CARRIER`
- `SPECIALIST`

Rules and Kafka monitor APIs are currently protected by `ADMIN`, not `FMG_ADMIN`.

## Primary User Journey

### 1. Patient Onboarding

Controller path: `/api/v1/auth`

Implementation path:

`AuthController.patientRegistration` -> `AuthServiceImpl.patientRegistration` -> Redis pending patient + OTP -> email OTP.

Then:

`AuthController.verifyPatientOtp` -> `AuthServiceImpl.verifyPatientOtp` -> creates `User` with role `PATIENT` and patient profile data -> deletes pending Redis data.

Then:

`AuthController.login` -> `AuthServiceImpl.login` -> `AuthenticationManager` -> JWT generation -> refresh token creation.

Generated data:

- JWT access token.
- Refresh token.
- Patient `User`.
- Patient profile.

### 2. Carrier Onboarding

Controller path: `/api/v1/auth`

Implementation path:

`AuthController.carrierRegistration` -> `AuthServiceImpl.carrierRegistration` -> Redis pending carrier + OTP -> email OTP.

Then:

`AuthController.verifyCarrierOtp` -> `AuthServiceImpl.verifyCarrierOtp` -> creates `User` with role `CARRIER` and `Carrier` entity.

Admin approval is required before the carrier can participate in assignment:

`AdminController.approveCarrier` -> `AdminServiceImpl.approveCarrier` -> sets carrier user status to `ACTIVE` -> sends approval email -> publishes carrier-approved Kafka event.

Generated data:

- Carrier `User`.
- `Carrier` entity and `carrierId`.
- Carrier access token after login.

### 3. Claim Submission

Controller path: `/api/v1/claims`

Implementation path:

`ClaimController.createClaim` -> `ClaimServiceImpl.createClaim`.

Important implementation behavior:

- Only `PATIENT` can create a claim.
- The claim is mapped from `ClaimRequest`.
- If `carrierName` matches an existing carrier company name, the claim is linked to that `Carrier`.
- Claim status is set to `SUBMITTED`.
- Audit action `CLAIM_CREATED` is logged.
- The method returns `ClaimResponse`, including `id`, which becomes the central `claimId`.

Generated data:

- `claimId`.
- Initial claim status: `SUBMITTED`.
- Claim audit entry.

### 4. Document Upload

Controller path: `/api/v1/files`

Implementation path:

`FileUploadController.uploadDocument` -> `FileUploadServiceImpl.uploadDocument`.

Important implementation behavior:

- Upload requires an existing `claimId`.
- Only `PATIENT` can upload.
- `StorageProvider` validates and stores the physical file.
- `ClaimDocument` is saved against the `Claim`.
- If enough document/business validation passes inside file upload processing, the claim can be moved to `UNDER_REVIEW`.
- Fraud/health/risk calculation can be invoked from the file upload service path.

Generated data:

- `documentId`.
- Stored file path.
- Claim document metadata.
- Possible claim status/risk updates depending on validation result.

Uncertainty:

- The code supports upload-driven validation/risk updates, but the exact required document set is not fully enforced at the controller contract level.

### 5. AI And Medical Validation

AI controller path: `/api/v1/ai`

Medical controller path: `/api/v1/medical`

Implementation behavior:

- `validate-document` accepts multipart file and document type. It validates document content/metadata and returns `DocumentValidationResponse`.
- `validate-claim` accepts `AiValidationRequest`; it is pre-claim style validation and does not require a persisted claim.
- `analyze/{claimId}` and `claims/{id}/generate-summary` require an existing claim.
- Medical validation accepts ICD code, diagnosis, and amount. It returns issue/risk data but does not directly mutate a claim from the controller path.

Dependency:

- Pre-claim AI/medical validation can be done before claim creation.
- Claim AI summary and analysis require `claimId`.

### 6. Rule Engine Flow

Controller path: `/api/v1/rules`

Implementation path:

`RuleEngineController` -> `RuleEngineServiceImpl`.

Important behavior:

- Current code protects rules with `ADMIN`.
- `seed` creates default dynamic rules.
- `evaluate` and `simulate` accept `ClaimRequest`, not just `claimId`.
- `RuleEngineServiceImpl.evaluateClaim(claimRequest, claimId, simulationMode)` loads active `RuleConfig` rows. If none exist, it falls back to Drools.
- Dynamic rules are evaluated by priority.
- Simple rule keys handled in code include high/max amount and inactive policy style checks.
- Rule execution writes `RuleExecutionAudit`.

Claim mutation:

- Direct REST `POST /api/v1/rules/evaluate` returns a decision but does not itself update an existing persisted claim because no `claimId` is provided.
- Kafka consumer flow can evaluate a claim event and call `ClaimServiceImpl.processClaimDecision`, which mutates the claim status through the claim state machine.

Uncertainty:

- `ClaimEventProducer.publishClaimCreatedEvent` exists, and `ClaimEventConsumer` can process claim-created events, but `ClaimServiceImpl.createClaim` in the inspected code logs audit and returns without visibly publishing the claim-created event. Therefore, automatic rule processing after claim creation is not guaranteed from the REST create-claim path.

### 7. Admin Review And Carrier Assignment

Controller path: `/api/v1/admin`

Implementation path:

- `AdminController.approveClaim` -> `AdminServiceImpl.approveClaim`.
- `AdminController.rejectClaim` -> `AdminServiceImpl.rejectClaim`.
- `AdminController.reviewClaim` -> `AdminServiceImpl.reviewClaim`.
- `AdminController.assignClaimToCarrier` -> `AdminServiceImpl.assignClaimToCarrier`.

Important behavior:

- Admin claim review uses `ClaimStateMachine.validateTransition`.
- Admin approval moves a claim to `ADMIN_APPROVED`.
- Admin rejection moves a claim to `REJECTED`.
- Admin assignment requires an existing active carrier. The service rejects inactive carriers.
- Assignment links `Claim.carrier` to the selected `Carrier`.

Generated/updated data:

- Claim status.
- `reviewedBy`, `reviewedAt`, `processedDate`, notes/reasons.
- Claim-to-carrier relationship.
- Notifications and Kafka claim notification events.
- Audit log entries.

### 8. Carrier Processing

Controller path: `/api/v1/carrier`

Implementation path:

- `CarrierController.getAssignedClaims` -> `CarrierServiceImpl.getAssignedClaims`.
- `CarrierController.validatePolicy` -> `CarrierServiceImpl.validatePolicy`.
- `CarrierController.approveClaim` -> `CarrierServiceImpl.approveClaim`.
- `CarrierController.rejectClaim` -> `CarrierServiceImpl.rejectClaim`.
- `CarrierController.flagSuspicious` -> `CarrierServiceImpl.flagSuspicious`.
- `CarrierController.addRemark` -> `CarrierServiceImpl.addRemark`.

Important behavior:

- The controller has class-level `@PreAuthorize("hasRole('CARRIER')")`.
- Carrier can only access claims assigned to its `Carrier` entity.
- Carrier approval is allowed only when current claim status is `ADMIN_APPROVED`.
- Carrier approval moves claim to `CARRIER_APPROVED`.
- Carrier rejection moves claim to `REJECTED`.
- Carrier flagging increases risk score and adds risk flags.
- Policy validation and remarks append review notes but do not complete the claim.

### 9. Payment Flow

Controller path: `/api/v1/payments`

Implementation path:

- `PaymentController.createOrder` -> `PaymentServiceImpl.createOrder`.
- `PaymentController.verifyPayment` -> `PaymentServiceImpl.verifyPayment`.
- `PaymentController.getPaymentByClaimId` -> `PaymentServiceImpl.getPaymentByClaimId`.

Important behavior:

- Create order is allowed for `PATIENT` and `ADMIN`.
- Get payment is allowed for `PATIENT`, `ADMIN`, and `CARRIER`.
- Payment eligibility requires claim status `ADMIN_APPROVED` or `CARRIER_APPROVED`.
- Duplicate successful payment is blocked.
- Razorpay order creation saves `Payment` with status `CREATED`.
- Claim moves to `PAYMENT_PENDING` after order creation.
- Payment verification validates HMAC signature.
- On valid signature, payment becomes `SUCCESS` and claim becomes `SETTLED`.
- Payment ledger entries are written for creation and verification.

Generated data:

- `razorpay_order_id`.
- `paymentId`.
- `PaymentLedger` rows.
- Final claim status `SETTLED`.

### 10. Notifications, Audit, Analytics, Fraud, Kafka Monitoring

Notifications:

- Created from admin and carrier actions through `NotificationServiceImpl`.
- Patient/admin/carrier can fetch own notifications.
- Mark-read APIs update notification read state.

Audit:

- `AuditLogServiceImpl` stores claim audit actions with hash chaining.
- Audit APIs are `ADMIN` only.
- Payment ledger queries are exposed through audit APIs.

Analytics:

- Uses repositories to aggregate claims, payments, fraud, SLA, leakage, hospitals, forecast, and carrier summary.
- Protected by `ADMIN` and `CARRIER`.

Fraud:

- Carrier flagging changes claim risk fields.
- Fraud service calculates health/risk using claim data, documents, metadata, and medical validation.
- Admin can mark a claim safe.

Kafka:

- `ClaimEventPipelineProducer` supports lifecycle topics.
- `ClaimEventPipelineConsumer` writes `EventAuditLog` rows for received/processed/DLQ events.
- `KafkaMonitorController` is under `/api/v1/admin/kafka` and protected by `ADMIN`.

## Claim Lifecycle From Code

The state machine allows:

```text
SUBMITTED -> AI_VALIDATED
SUBMITTED -> ADMIN_APPROVED
SUBMITTED -> REJECTED

AI_VALIDATED -> UNDER_REVIEW
AI_VALIDATED -> ADMIN_APPROVED
AI_VALIDATED -> REJECTED

UNDER_REVIEW -> ADMIN_APPROVED
UNDER_REVIEW -> REJECTED

ADMIN_APPROVED -> CARRIER_APPROVED
ADMIN_APPROVED -> REJECTED

CARRIER_APPROVED -> PAYMENT_PENDING
CARRIER_APPROVED -> SETTLED

PAYMENT_PENDING -> SETTLED

REJECTED and SETTLED are terminal
```

Observed implementation caveat:

- `ClaimServiceImpl.processBulkApproval` sets status `APPROVED`, but `APPROVED` is not included as a valid transition target in `ClaimStateMachine`. Treat bulk approval as an alternate settlement path, not the main lifecycle path.

## Recommended Realistic Happy Path

```text
Patient register
-> Patient verify OTP
-> Patient login
-> Carrier register
-> Carrier verify OTP
-> Admin login
-> Admin approve carrier
-> Carrier login
-> Patient optionally validates medical/AI data
-> Patient creates claim
-> Patient uploads documents
-> Admin reviews claim
-> Admin approves claim
-> Admin assigns active carrier
-> Carrier validates policy
-> Carrier optionally remarks/flags/analyzes
-> Carrier approves claim
-> Patient or admin creates payment order
-> Patient or admin verifies payment
-> Claim becomes SETTLED
-> Notifications, audit, fraud, analytics, Kafka monitoring are verified
```

