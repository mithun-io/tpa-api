# Repository Test Fixes

## Problems

- Repository tests loaded shared Spring contexts against a single H2 memory database.
- H2 PostgreSQL compatibility mode rejected Hibernate-generated `tinyint` enum columns.
- Production seeders could alter repository test data.

## Fixes

- Applied `@ActiveProfiles("test")` consistently to repository tests.
- Mocked `AdminInitializer` and `EnterpriseDemoDataSeeder`.
- Switched test datasource to a unique H2 memory URL per context.
- Set the H2 dialect explicitly.
- Kept repository fixtures deterministic and cleaned related repositories in setup.

## Outcome

Repository tests pass without PostgreSQL, production seed data, or schema collisions.
