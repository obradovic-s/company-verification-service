# Company Verification Service

Spring Boot backend assignment implementation with:

- Mock FREE third-party endpoint (`/free-third-party`)
- Mock PREMIUM third-party endpoint (`/premium-third-party`)
- Orchestrating backend endpoint with fallback (`/backend-service`)
- Verification storage + retrieval (`/verifications/{verificationId}`)

## Tech stack

- Java 21
- Spring Boot 3.2.5
- Maven
- H2 + Spring Data JPA
- RestClient

## Run

```bash
mvn spring-boot:run
```

Server runs on `http://localhost:8080`.

## Test

```bash
mvn test
```

## API

### Verify company

`GET /backend-service?verificationId={uuid}&query={text}`

Behavior:

- Tries FREE first
- Falls back to PREMIUM if FREE is unavailable or has no active results
- Filters inactive companies
- Returns first active match as `result`, rest as `otherResults`
- Treats `verificationId` as an idempotency key (repeated ID returns stored result)
- If stored status is `THIRD_PARTIES_DOWN`, retry with a new `verificationId` to force a fresh verification

Status values in response:

- `FOUND`
- `NO_RESULTS`
- `THIRD_PARTIES_DOWN`

### Read stored verification

`GET /verifications/{verificationId}`

Returns stored verification response with metadata.
