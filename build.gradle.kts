plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
	id("org.sonarqube") version "5.1.0.4882"
}

group = "com.miniproject"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Monitoring & Metrics
	implementation("io.micrometer:micrometer-registry-prometheus")

	// Database
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	// Jackson for Java 8 Date/Time
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// API Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// JaCoCo Configuration
jacoco {
	toolVersion = "0.8.11"
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.50".toBigDecimal()
			}
		}
	}
}

// SonarQube Configuration
sonar {
	properties {
		property("sonar.projectKey", "ttambunan01-sudo_todolist")
		property("sonar.organization", "ttambunan01-sudo")
		property("sonar.host.url", "https://sonarcloud.io")

		// Code coverage from JaCoCo
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")

		// Source and test directories
		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")
		property("sonar.java.binaries", "build/classes/java/main")
		property("sonar.java.test.binaries", "build/classes/java/test")

		// Exclusions (config, DTOs, entities are low-value for code quality)
		property("sonar.exclusions", "**/config/**,**/dto/**,**/entity/**,**/enums/**")

		// Java version
		property("sonar.java.source", "21")
		property("sonar.java.target", "21")
	}
}
