# Spring Context Fixes

## Problems

- Full contexts loaded production startup seeders.
- H2 schema creation failed for carrier enum columns in PostgreSQL compatibility mode.
- Kafka topics/listeners attempted real broker connections during tests.

## Fixes

- Replaced startup seeders with test mocks in full-context tests.
- Updated `application-test.yaml` to use a unique H2 database per context.
- Set the test JPA dialect to `org.hibernate.dialect.H2Dialect`.
- Removed PostgreSQL compatibility mode from the H2 test URL to support Hibernate enum DDL.
- Added `KafkaTestConfig` under test sources to disable Kafka listener auto-startup in the `test` profile.
- Disabled Kafka admin auto-creation/fail-fast behavior in test properties.

## Outcome

Full-context tests now load deterministically without production seed data, real Kafka, or shared H2 schema state.
