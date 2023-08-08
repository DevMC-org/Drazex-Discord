plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":ai"))
    implementation(project(":paste"))

    implementation("com.discord4j:discord4j-core:3.3.0-M2")

    api("io.projectreactor:reactor-core:3.5.4")

    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "WARN")
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