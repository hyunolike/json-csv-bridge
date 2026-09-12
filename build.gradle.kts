import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    `maven-publish`
    kotlin("jvm") version "1.9.24"
}

group = "com.jsoncsvbridge"
version = "2.0.0"

val jacksonVersion = "2.17.2"
val slf4jVersion = "2.0.13"
val springVersion = "6.1.11"
val junitVersion = "5.10.3"
val junitPlatformVersion = "1.10.3"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // 변환에 실제로 필요한 것만 런타임 의존성으로 둔다.
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    // @Component 애너테이션 용도로만 쓰므로 컴파일 시점에만 필요하다.
    // 라이브러리 사용자에게 Spring 을 강요하지 않는다.
    compileOnly("org.springframework:spring-context:$springVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "json-csv-bridge"
            version = project.version.toString()

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("json csv bridge")
                description.set("A library for converting JSON data to CSV format")
                url.set("https://github.com/hyunolike/json-csv-bridge")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("hyunolike")
                        name.set("Hyunho Jang")
                        email.set("hyunho.jang.dev@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/hyunolike/json-csv-bridge.git")
                    developerConnection.set("scm:git:ssh://github.com/hyunolike/json-csv-bridge.git")
                    url.set("https://github.com/hyunolike/json-csv-bridge")
                }
            }
        }
    }
}

// ktlint configuration
ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }

    // 와일드카드 임포트 규칙 비활성화
    disabledRules.set(setOf("no-wildcard-imports"))
}

// `check` 는 포맷을 검증만 한다. 빌드가 소스를 고쳐 쓰지 않도록 ktlintFormat 은 걸지 않는다.
tasks.named("check") {
    dependsOn("ktlintCheck")
}
