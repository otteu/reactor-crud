package dev.example.webflux_reactor.service

import dev.example.webflux_reactor.model.Article
import dev.example.webflux_reactor.repository.ArticleRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
// @Transactional   지원안함
// @Rollback
class ArticleServiceTest (
    @Autowired private val service: ArticleService,
    @Autowired private val respository: ArticleRepository,
){

    @Test
    fun creatAndGet() {
        val prevCnt = respository.count().block() ?: 0
        val article = service.create(ReqCreate("title", body = "body")).block()!!
        val currCnt = respository.count().block() ?: 0
        Assertions.assertEquals(prevCnt + 1, currCnt)
        val readArticle = service.get(article.id).block()!!
        assertEquals(article.id ,readArticle.id)
        assertEquals(article.title ,readArticle.title)
        assertEquals(article.body ,readArticle.body)
    }

    @Test
    fun getAll() {
        service.create(ReqCreate("title1", body = "1")).block()!!
        service.create(ReqCreate("title2", body = "2")).block()!!
        service.create(ReqCreate("title matched", body = "3")).block()!!

        assertEquals(3, service.getAll().collectList().block()!!.size)
    }

    @Test
    fun update() {
        val new = service.create(ReqCreate("title", body = "body", authorId = 1234)).block()!!
        val request = ReqUpdate(
            title = "updated !",
            body = "update body !",
        )
        service.update(new.id, request).block()
        service.get(new.id).block()!!.let { article ->
            assertEquals(request.title, article.title)
            assertEquals(request.body, article.body)
            assertEquals(request.authorId, article.authorId)
        }
    }


    @Test
    fun delete() {
        val prevCnt = respository.count().block() ?: 0
        val article = service.create(ReqCreate("title", body = "body")).block()!!
        service.delete(article.id).block()
        val currCnt = respository.count().block() ?: 0
        assertEquals(prevCnt, currCnt)


    }

}