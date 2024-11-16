plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	war
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "sdmed"
version = "1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-integration")
	implementation("org.springframework.integration:spring-integration-mqtt")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	implementation("dev.akkinoc.util:yaml-resource-bundle:2.12.3")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")

	implementation("io.jsonwebtoken:jjwt-api:0.12.5")

	implementation("com.google.code.gson:gson:2.11.0")

	implementation("org.json:json:20240303")

	implementation("io.projectreactor:reactor-core:3.6.6")

	implementation("org.apache.poi:poi-ooxml:5.2.2")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.5")
	runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}