package dev.example.webflux_reactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxReactorApplication

fun main(args: Array<String>) {
	runApplication<WebfluxReactorApplication>(*args)
}
