package dev.example.webflux_reactor.controller

import dev.example.webflux_reactor.model.Article
import dev.example.webflux_reactor.service.ArticleService
import dev.example.webflux_reactor.service.ReqCreate
import dev.example.webflux_reactor.service.ReqUpdate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/article")
class ArticleController (
    private val service: ArticleService
){


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ReqCreate): Mono<Article> {
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Mono<Article> {
        return service.get(id)
    }

    @GetMapping("/all")
    fun getAll(title: String?): Flux<Article> {
        return service.getAll(title)
    }

    @PutMapping("/{id}")
    fun update(id: Long, request: ReqUpdate): Mono<Article> {
        return service.update(id, request)
    }

    @DeleteMapping("/{id}")
    fun delete(id: Long): Mono<Void> {
        return service.delete(id)
    }

}