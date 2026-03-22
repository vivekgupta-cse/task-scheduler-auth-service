# Task Scheduler — Auth Service

This repository contains a small Spring Boot-based authentication service used by the Task Scheduler project. It provides user registration and login endpoints and issues JWT tokens for authenticated users.

High-level checklist
- Overview and purpose
- How to build and run locally
- Required environment variables and defaults
- API endpoints and examples (register / login)
- Troubleshooting notes (including PasswordEncoder bean issue)

Project layout (important files)
- `src/main/java/com/taskscheduler/authservice/` — Java sources
  - `controller/AuthController.java` — REST endpoints (/api/auth)
  - `service/AuthService.java` — business logic (uses `PasswordEncoder`)
  - `service/JwtService.java` — lightweight JWT implementation
  - `security/JwtAuthenticationFilter.java` — authentication filter
  - `config/` — configuration classes (Flyway, JwtConfig, SecurityConfig)
- `src/main/resources/application.yaml` — default configuration

Requirements
- Java 17 or later
- Gradle (wrapper included: `./gradlew`)
- PostgreSQL when running with the default datasource (or adjust `spring.datasource.url` to use another DB).

Build

From the project root run:

```bash
./gradlew clean build
```

Run (development)

Run using the Gradle Spring Boot plugin:

```bash
./gradlew bootRun
```

Or run the packaged jar after building:

```bash
java -jar build/libs/*-SNAPSHOT.jar
```

Configuration / Environment variables
- `server.port` — default: `8085` (see `application.yaml`)
- Database (defaults are in `application.yaml`):
  - `spring.datasource.url` — default: `jdbc:postgresql://localhost:5432/auth_db`
  - `DB_USERNAME` — environment variable placeholder used in `application.yaml` (default: `docker`)
  - `DB_PASSWORD` — environment variable placeholder (default: `docker`)
- JWT
  - `JWT_SECRET` — default: `my-local-dev-secret-only` when not provided. In production override via env var or properties.
  - `jwt.expiry-seconds` — token TTL (must be set; check `application.yaml` or provide via env/properties).

Quick start (local, using defaults)

1. Ensure PostgreSQL is running and a database `auth_db` exists (or update `spring.datasource.url` to point at a DB you have).
2. Start the app:

```bash
# optionally export DB_USERNAME and DB_PASSWORD if you changed them
./gradlew bootRun
```

API

Base path: `/api/auth`

1) Register

- POST `/api/auth/register`
- Request JSON:

```json
{
  "username": "alice",
  "password": "s3cret"
}
```

- Example curl:

```bash
curl -sS -X POST http://localhost:8085/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"s3cret"}'
```

Response: 200 OK with a short success message or 4xx with error details.

2) Login

- POST `/api/auth/login`
- Request JSON same as register.
- Response: a JWT token string when credentials are valid.

Example:

```bash
curl -sS -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"s3cret"}'

# returns a token (string)
```

Using the token

Once you have a token, include it in the `Authorization` header as `Bearer <token>` for protected endpoints. The service includes `JwtAuthenticationFilter` which extracts and validates the token.

Troubleshooting

- Error: "Parameter 1 of constructor in ... AuthService required a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' that could not be found." 
  - Cause: Spring could not find any `PasswordEncoder` bean to inject into `AuthService`.
  - Fix: Add a `PasswordEncoder` bean to the application context. This project includes `SecurityConfig.java` which exposes a `BCryptPasswordEncoder` bean at `src/main/java/com/taskscheduler/authservice/config/SecurityConfig.java`. If you fork this project or remove that class, ensure you provide a bean like:

```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- If you see Flyway trying to run during unit tests and failing, set `spring.flyway.enabled=false` for the test profile or in your test properties.
- JWT issues: the default secret is set in `application.yaml` for local dev only. For production always set `JWT_SECRET` to a secure value and rotate secrets as needed.

Logging
- The app writes logs to `logs/auth-service.log` (see `logback-spring.xml`). Tail the log while running:

```bash
tail -f logs/auth-service.log
```

Development notes
- The project uses Lombok (`@RequiredArgsConstructor`, `@Builder`). Ensure your IDE has Lombok support enabled.
- The `JwtService` in this project implements a small JWT creation/verification mechanism; in production you may prefer a well-tested library like `jjwt` or `java-jwt`.

Contributing
- Contributions and fixes are welcome. Please add tests for new features and follow existing code style.

License
- This project is provided as-is for learning and development purposes.

