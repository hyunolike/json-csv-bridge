plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
repositories {
    mavenCentral()
}

dependencies {
    // 오픈소스 프로젝트 추가
    // Add open-source project
    implementation("io.github.hyunolike:json-csv-bridge:2.2.0")

    // Spring Boot 라면 스타터를 쓰는 방법도 있다. 변환기가 빈으로 자동 등록되어
    // generateCsv(...) 대신 CsvCreator 를 주입받아 쓸 수 있다.
    // implementation("io.github.hyunolike:json-csv-bridge-spring-boot-starter:2.2.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
