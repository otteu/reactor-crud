package dev.example.webflux_reactor.controller

import dev.example.webflux_reactor.model.Article
import dev.example.webflux_reactor.service.ReqCreate
import dev.example.webflux_reactor.service.ReqUpdate
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.temporal.ChronoUnit

@SpringBootTest
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext
) {

    val client = WebTestClient.bindToApplicationContext(context).build()

    @Test
    fun create() {
        val request = ReqCreate("title 1", "is is r2dbc demo test", 2332)
        client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title)
            .jsonPath("body").isEqualTo(request.body!!)
    }

    @Test
    fun get() {
        val request = ReqCreate("title 1", "it is r2dbc demo test", 2342)
        val created = client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectBody(Article::class.java).returnResult().responseBody!!
        val read = client.get().uri("/article/${created.id}").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody(Article::class.java).returnResult().responseBody!!
        assertEquals(created.title, read.title)
        assertEquals(created.body, read.body)
        assertEquals(created.authorId, read.authorId)
        assertEquals(created.createdAt?.truncatedTo(ChronoUnit.MILLIS), read.createdAt?.truncatedTo(ChronoUnit.MILLIS))
        assertEquals(created.updatedAt?.truncatedTo(ChronoUnit.MILLIS), read.updatedAt?.truncatedTo(ChronoUnit.MILLIS))
    }

    @Test
    fun getAll() {
        repeat(5) { i ->
            val request = ReqCreate("title $i", "it is r2dbc demo test", i.toLong())
            client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
        }
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(
            ReqCreate("title matched", "it is r2dbc demo test")
        ).exchange()

        val cnt = client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody(List::class.java)
            .returnResult().responseBody?.size ?: 0

        assertTrue(cnt > 0)

        client.get().uri("/article/all?title=matched").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    @Test
    fun update() {
        val created = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("title 1", "it is r2dbc demo test", 2323))
            .exchange()
            .expectBody(Article::class.java).returnResult().responseBody!!
        client.put().uri("/article/${created.id}").accept(APPLICATION_JSON)
            .bodyValue(
                ReqUpdate(
                authorId = 777
            )
            ).exchange().expectBody()
            .jsonPath("authorId").isEqualTo(777)
    }

    @Test
    fun delete() {
        val prevCnt = getArticleSize()
        val created = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("title 1", "it is r2dbc demo test", 4553))
            .exchange()
            .expectBody(Article::class.java).returnResult().responseBody!!
        client.delete().uri("/article/${created.id}").accept(APPLICATION_JSON).exchange()
        val currCnt = getArticleSize()
        assertEquals(prevCnt, currCnt)
    }

    private fun getArticleSize(): Int {
        return client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody(List::class.java).returnResult().responseBody?.size ?: 0
    }
}