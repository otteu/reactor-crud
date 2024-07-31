package dev.example.webflux_reactor.service

import dev.example.webflux_reactor.exception.NoArticleException
import dev.example.webflux_reactor.model.Article
import dev.example.webflux_reactor.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ArticleService (
    private val repository: ArticleRepository
){

    @Transactional
    fun create(request: ReqCreate): Mono<Article> {
        return repository.save(request.toArticle())
    }

    fun get(id: Long): Mono<Article> {
        return repository.findById(id)
            .switchIfEmpty { throw NoArticleException("id: $id") }
    }

    fun getAll(title: String? = null): Flux<Article> {
        return if(title.isNullOrEmpty()) repository.findAll()
            else repository.findAllByTitleContains(title)

    }

    fun update(id: Long, request: ReqUpdate): Mono<Article> {
        return repository.findById(id)
            .switchIfEmpty { throw NoArticleException("id :$id") }
            .flatMap { article ->
                request.title?.let { article.title = it }
                request.body?.let { article.body = it }
                request.authorId?.let { article.authorId = it }
                repository.save(article)
            }

    }

    fun delete(id: Long): Mono<Void> {
        return repository.deleteById(id)
    }

}


data class ReqCreate(
    var title: String,
    var body: String? = null,
    var authorId: Long? = null,
) {

    fun toArticle(): Article{
        return Article(
            title = this.title,
            body = this.body,
            authorId = this.authorId,
        )
    }
}


data class ReqUpdate(
    val title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)