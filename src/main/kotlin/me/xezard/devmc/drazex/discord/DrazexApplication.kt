package me.xezard.devmc.drazex.discord

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DrazexApplication

fun main(args: Array<String>) {
    runApplication<DrazexApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}