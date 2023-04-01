package me.xezard.devmc.drazex.discord

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class DrazexApplication

fun main(args: Array<String>) {
    runApplication<DrazexApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}