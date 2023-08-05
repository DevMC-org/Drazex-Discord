plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    kotlin("kapt")
}

tasks {
    bootJar {
        enabled = false
    }

    jar {
        enabled = true
    }
}