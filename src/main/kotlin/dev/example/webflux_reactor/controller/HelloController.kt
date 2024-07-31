package dev.example.webflux_reactor.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class HelloController {

    @GetMapping("/hello")
    fun hello(@RequestParam name: String): Mono<String> {
        return Mono.just("Hello world $name")
    }

    @GetMapping("/")
    fun index(): String {
        return "main hello !"
    }




}