# Mockito Fixes Summary

## Changes

- Added missing `@MockBean` entries for production startup runners in Spring context tests:
  - `AdminInitializer`
  - `EnterpriseDemoDataSeeder`
- Added missing unit-test collaborators for `FraudDetectionServiceImpl`:
  - `ClaimDocumentRepository`
  - `MedicalValidationService`
  - `StorageProvider`
- Updated rule engine unit tests with mocked `RuleConfigRepository` and `RuleExecutionAuditRepository`.
- Used explicit Groovy `RuleConfig` fixtures to exercise the current DB-backed enterprise rule engine.
- Mocked `FileUploadService` in the upload integration path to keep the test focused on controller/security wiring.

## Notes

`lenient()` was used only where shared setup stubbing is intentionally not consumed by every test path.
