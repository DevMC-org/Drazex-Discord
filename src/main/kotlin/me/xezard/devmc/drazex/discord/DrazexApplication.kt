package me.xezard.devmc.drazex.discord

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["me.xezard.devmc.drazex.discord"])
class DrazexApplication

fun main(args: Array<String>) {
    runApplication<DrazexApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}