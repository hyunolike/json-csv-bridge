import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint")
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm")
}

val jacksonVersion: String by project
val springBootVersion: String by project
val junitVersion: String by project
val junitPlatformVersion: String by project
val assertjVersion: String by project
val centralRepositoryUrl: String by project

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 코어는 루트 프로젝트다. 사용자가 별도로 추가하지 않아도 되도록 api 로 노출한다.
    api(project(":"))

    // 스타터를 쓰는 쪽은 이미 Spring Boot 애플리케이션이다.
    implementation("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")

    testImplementation("org.springframework.boot:spring-boot-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot:$springBootVersion")
    // ApplicationContextRunner 의 반환 타입이 AssertJ 를 요구한다.
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    // 메타데이터 JSON 을 읽어 코드와 대조하는 테스트에 쓴다. 코어에서는 implementation 이라 전이되지 않는다.
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
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
    repositories {
        maven {
            name = "mavenCentral"
            url = uri(centralRepositoryUrl)
            credentials {
                username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull
                password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("json csv bridge spring boot starter")
                description.set("Spring Boot auto-configuration for json-csv-bridge")
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

// Maven Central 은 모든 아티팩트에 PGP 서명을 요구한다.
// SIGNING_KEY 가 없으면 서명을 건너뛰므로, 평소 빌드와 JitPack 빌드는 키 없이도 돌아간다.
signing {
    val signingKey = providers.environmentVariable("SIGNING_KEY").orNull
    val signingPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull

    isRequired = signingKey != null
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    filter {
        exclude("**/generated/**")
        exclude { it.file.path.contains("${File.separator}build${File.separator}") }
        include("**/kotlin/**")
    }
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}
