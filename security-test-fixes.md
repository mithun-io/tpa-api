# Security Test Fixes

## Changes

- Preserved `@WithMockUser` coverage for:
  - `FMG_ADMIN`
  - `CARRIER_USER`
  - `CUSTOMER`
- Kept integration tests on `@ActiveProfiles("test")`.
- Stabilized security integration contexts by mocking production seeders and disabling external Kafka startup.
- Kept unauthenticated assertions flexible where the security chain may return either `401` or `403`.

## Outcome

Security integration tests pass with current RBAC behavior.
