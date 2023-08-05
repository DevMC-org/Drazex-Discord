plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":common"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks {
    bootJar {
        enabled = false
    }

    jar {
        enabled = true
    }
}