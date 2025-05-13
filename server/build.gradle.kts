plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("jacoco")
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.25"
}

group = "com.app"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // OAuth2 Client & Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Jasypt encryption
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")

    // jwt
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // json
    implementation("com.google.code.gson:gson:2.10.1")

    // Lombok
    compileOnly("org.projectlombok:lombok")

    // DB
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Test & Testcontainers
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")

    // Coroutines
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // GCP
    implementation("com.google.cloud:spring-cloud-gcp-storage:6.1.1")
    implementation("com.google.cloud.sql:mysql-socket-factory-connector-j-8:1.18.0")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation(platform("com.google.cloud:libraries-bom:26.59.0"))

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    systemProperty("junit.jupiter.execution.parallel.enabled", false)
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // 테스트 후에 JaCoCo 리포트 생성

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {
            enabled = true // 이 rule을 적용할 것이다.
            element = "CLASS" // class 단위로

            // 브랜치 커버리지 최소 60%
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = 0.00.toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = 0.00.toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = 300.toBigDecimal()
            }

            excludes = listOf(
                "*.ui.controller.*",
                "*.dto.*",
                "*.auth.*",
                "*.common.*",
                "*.core.*",
                "*.infra.*",
                "*.exception.*",
                "*.enums.*",
            )
        }
    }
}