package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.data.repository.BlogRepository
import io.reactivex.Single
import org.koin.core.KoinComponent

class GetPostsUseCase(private val repository: BlogRepository) : KoinComponent {

    companion object {
        const val MAX_ITEMS_PER_PAGE = 5
    }

    fun execute(page: Int): Single<List<Post>> {
        // users must be available for the blog posts
        return repository.getUsers()
            .ignoreElement()
            .andThen(repository.getPosts(page, MAX_ITEMS_PER_PAGE))
    }

}