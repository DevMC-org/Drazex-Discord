import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("kapt") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
}

group = "me.xezard.devmc"
version = "1.0.0"

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
    }

    test {
        java.srcDir("src/test/kotlin")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-rsocket:3.1.2")

    implementation("com.discord4j:discord4j-core:3.3.0-M2")

    implementation("io.rsocket:rsocket-core:1.1.3")
    implementation("io.rsocket:rsocket-transport-netty:1.1.3")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.0-Beta")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.20-RC")

    api("com.google.guava:guava:31.1-jre")
    api("com.google.code.gson:gson:2.10.1")
    api("io.projectreactor:reactor-core:3.5.4")

    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.20-RC")
}

tasks {
    bootJar {
        archiveFileName.set("DrazexBot-discord.jar")
    }

    jar {
        manifest {
            attributes["Main-Class"] = "me.xezard.devmc.drazex.discord.DrazexApplication"
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(18)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    test {
        useJUnitPlatform()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}