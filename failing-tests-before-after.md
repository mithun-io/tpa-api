# Failing Tests Before/After

## Before

Observed failure categories:

- `ApplicationContext` startup failures from enterprise demo seeding and H2 schema creation.
- H2 DDL incompatibility for enum `tinyint` columns under PostgreSQL compatibility mode.
- Kafka admin/listener attempts to connect to `localhost:9092`.
- Controller assertion mismatch: `/api/v1/claims` now returns a `Page`, not a raw list.
- Mockito constructor gaps after enterprise service dependencies were added.
- Integration upload test hit external AI behavior and lazy JPA response serialization.

Representative failing classes:

- `ClaimControllerTest`
- `ClaimControllerIntegrationTest`
- `ClaimProcessingIntegrationTest`
- `SecurityIntegrationTest`
- `CarrierRepositoryTest`
- `ClaimRepositoryTest`
- `FraudDetectionServiceImplTest`
- `RuleEngineServiceImplTest`

## After

All reported test classes pass.

Verified totals from Surefire reports:

- Tests run: 202
- Failures: 0
- Errors: 0
- Skipped: 0

Final Maven result:

```bash
BUILD SUCCESS
```
