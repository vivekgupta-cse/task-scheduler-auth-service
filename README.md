
# Task Scheduler — Auth Service

Production-grade, professional README for the Task Scheduler Authentication Service. This document describes the service purpose, architecture, how to build/run it, configuration and operational notes, API reference, troubleshooting guidance, author and licensing.

Table of contents
- Overview
- Architecture
- Quick start (build & run)
- Configuration (environment variables and properties)
- API (endpoints, payloads, examples)
- Operational notes (logging, health, monitoring)
- Troubleshooting
- Development & testing
- Author
- License

Overview
--------
The Auth Service is a small, focused Spring Boot application that provides user registration and authentication for the Task Scheduler system. It exposes REST endpoints for registering users and issuing JWTs for authenticated sessions. The service has a thin, well-defined responsibility: user lifecycle (register/login) and token issuance/verification.

Key responsibilities
- Securely store user credentials (BCrypt hashed passwords).
- Provide a /api/auth REST surface for register/login operations.
- Issue and validate JWT tokens used by downstream services.
- Apply database migrations via Flyway on startup (configurable).

Architecture
------------
This project uses a simple layered architecture:

- Controller layer: `controller/AuthController` — HTTP REST API, request validation.
- Service layer: `service/AuthService`, `service/JwtService` — business rules and token management.
- Security: `security/JwtAuthenticationFilter` and `config/SecurityConfig` — request-level authentication and PasswordEncoder bean.
- Persistence: Spring Data / JPA repositories, PostgreSQL (default) and Flyway for schema migrations.

ASCII diagram

  +------------+       +------------+       +-----------+
  |  Clients   | <---> | AuthController | <---> | AuthService |
  +------------+       +------------+       +-----------+
                                   |                 |
                                   v                 v
                           JwtService (create/verify)  Repository (User)
                                   |
                                   v
                           Downstream services verify JWTs

Quick start (build & run)
-------------------------
Prerequisites
- Java 17 or later (toolchain support in Gradle wrapper).
- Gradle (wrapper included: `./gradlew`).
- PostgreSQL when running with the default datasource (or update `spring.datasource.url`).

Build

```bash
./gradlew clean build
```

Run (development)

```bash
./gradlew bootRun
# or run packaged jar after build
java -jar build/libs/*-SNAPSHOT.jar
```

Configuration (important properties / env vars)
----------------------------------------------
- server.port — default: 8085 (see `src/main/resources/application.yaml`).
- Database (defaults are in `application.yaml`):
  - spring.datasource.url — default: `jdbc:postgresql://localhost:5432/auth_db`
  - DB_USERNAME, DB_PASSWORD — referenced by `application.yaml` (defaults in file: `docker`).
- JWT
  - JWT_SECRET — default local-dev fallback: `my-local-dev-secret-only`. ALWAYS override in production using environment variables or secure configuration management.
  - jwt.expiry-seconds — token TTL (configured in properties).

Set environment variables example (Linux/macOS):

```bash
export DB_USERNAME=docker
export DB_PASSWORD=docker
export JWT_SECRET="a-very-secret-value"
```

API (base path: /api/auth)
--------------------------------
1) Register

- POST /api/auth/register
- Request JSON

```json
{
  "username": "alice",
  "password": "s3cret"
}
```

- Response: 200 OK (success message) or 4xx with JSON error payload.

Example curl

```bash
curl -sS -X POST http://localhost:8085/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"s3cret"}'
```

2) Login

- POST /api/auth/login
- Request JSON same as register.
- Response: a JWT token string when credentials are valid.

Example curl

```bash
curl -sS -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"s3cret"}'
```

Using the token
---------------
Include the token in the HTTP Authorization header for protected endpoints:

Authorization: Bearer <token>

Operational notes
-----------------
- Logging: the service writes logs to `logs/auth-service.log` (see `logback-spring.xml`). Use `tail -f logs/auth-service.log` to follow logs.
- Health & metrics: Actuator is included. Expose and secure /actuator endpoints as appropriate in production.
- Flyway: database migrations are located in `src/main/resources/db/migration`.

Troubleshooting
---------------
- Missing PasswordEncoder bean error

  Error: "Parameter 1 of constructor in ... AuthService required a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' that could not be found."

  Cause: Spring could not find any `PasswordEncoder` bean to inject into `AuthService`.

  Fix: This project provides `SecurityConfig.java` which registers a `BCryptPasswordEncoder` bean. If you remove or change it in a fork, ensure you provide a bean like:

  ```java
  @Configuration
  public class SecurityConfig {
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
      }
  }
  ```

- Flyway running during tests

  If Flyway migrations run during unit tests and fail, disable Flyway for tests by setting `spring.flyway.enabled=false` for the test profile or via test properties.

- JWT issues

  The default secret in `application.yaml` is for local development only. In production supply a secure `JWT_SECRET` and rotate keys periodically.

Development & testing
---------------------
- Lombok: project uses Lombok annotations (`@RequiredArgsConstructor`, `@Builder`). Ensure your IDE has Lombok support enabled.
- Tests: an in-memory H2 database is included for tests to avoid needing a live Postgres instance.

Author
------
Vivek Gupta
Email: gvivek206@gmail.com

License
-------
This project is licensed under the Apache License

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/

You should have received a copy of the Apache License along with this project in the `LICENSE` file.

Contact & contribution guidelines
---------------------------------
If you find issues or want to contribute, please open a pull request or contact the author at gvivek206@gmail.com. For significant changes, include tests and update documentation.

