package dev.example.webflux_reactor

import dev.example.webflux_reactor.model.Article
import dev.example.webflux_reactor.repository.ArticleRepository
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


private val logger = KotlinLogging.logger {}

@SpringBootTest
class WebfluxReactorApplicationTests(
	@Autowired private val repository: ArticleRepository,
) {

	@Test
	fun contextLoads() {
		val preCount = repository.count().block() ?: 0
		repository.save(Article(title = "title1", body = "body")).block()
		val articles = repository.findAll().collectList().block()
		articles?.forEach { logger.debug { it } }

		val currCount = repository.count().block()
		Assertions.assertEquals(preCount + 1, currCount)

	}

}
