import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.xezard.devmc"
version = "1.0.0"

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
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

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

        implementation("org.springframework.boot:spring-boot-starter-webflux")

        implementation("io.projectreactor.addons:reactor-extra:3.5.1")
        implementation("org.mapstruct:mapstruct:1.5.5.Final")

        api("com.google.guava:guava:32.0.1-jre")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.0")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.0-Beta")

        annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    }
}

tasks {
    bootJar {
        enabled = false

        archiveFileName.set("DrazexBot-discord.jar")
    }

    jar {
        enabled = true
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
    }
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin")
    }

    test {
        java.srcDir("src/test/kotlin")
    }
}