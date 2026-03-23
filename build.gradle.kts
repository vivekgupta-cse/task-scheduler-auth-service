group = "com.taskscheduler"
version = "1.0-SNAPSHOT"
description = "TaskScheduler API Auth Service"

plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.4"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Generate JaCoCo report after tests run
}

// Configure JaCoCo reporting
jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation") // @Valid, @NotBlank, @Size
    // --- Actuator ---
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("org.postgresql:postgresql")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.postgresql:postgresql") // PostgreSQL driver needed at test runtime

    // In-memory database for tests to avoid requiring a real Postgres instance
    testImplementation("com.h2database:h2")

    // Lombok in tests
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql") // Required for Postgres

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT Support
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0") // for JSON parsing

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
}

// Add manifest information for jars (author, license) so packaged artifacts carry metadata
val projectAuthor = "Vivek Gupta"
val projectAuthorEmail = "gvivek206@gmail.com"
val projectLicenseName = "Apache License, Version 2.0"
val projectLicenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"
val projectUrl = "https://github.com/your-org/task-scheduler-auth-service"

// Enrich produced JARs with metadata
// Add manifest attributes (author/license metadata) to both plain JAR and bootJar artifacts
tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to project.description,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to projectAuthor,
            "Implementation-Vendor-Email" to projectAuthorEmail,
            "Implementation-URL" to projectUrl,
            "Built-By" to projectAuthor,
            "Implementation-License" to projectLicenseName,
            "Implementation-License-URL" to projectLicenseUrl
        ))
    }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to project.description,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to projectAuthor,
            "Implementation-Vendor-Email" to projectAuthorEmail,
            "Implementation-URL" to projectUrl,
            "Built-By" to projectAuthor,
            "Implementation-License" to projectLicenseName,
            "Implementation-License-URL" to projectLicenseUrl
        ))
    }
}
