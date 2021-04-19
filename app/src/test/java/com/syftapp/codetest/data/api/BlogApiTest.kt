package com.syftapp.codetest.data.api

import com.syftapp.codetest.data.model.api.Address
import com.syftapp.codetest.data.model.api.Company
import com.syftapp.codetest.data.model.domain.Comment
import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.data.model.domain.User
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BlogApiTest {

    private val blogService = mockk<BlogService>(relaxed = true)
    private val sut = BlogApi(blogService)

    private val mockAddress = mockk<Address>(relaxed = true)
    private val mockCompany = mockk<Company>(relaxed = true)

    @Test
    fun `get users contains correct domain models`() {
        val mockUsers = listOf(
            com.syftapp.codetest.data.model.api.User(
                id = 1,
                name = "Steve",
                username = "Jobs",
                email = "steve@jobs.com",
                address = mockAddress,
                company = mockCompany,
                phone = "123",
                website = "website"
            ),
            com.syftapp.codetest.data.model.api.User(
                id = 2,
                name = "Steve",
                username = "Jobs 2",
                email = "steve2@jobs.com",
                address = mockAddress,
                company = mockCompany,
                phone = "1234",
                website = "website2"
            )
        )

        every { blogService.getUsers() } returns Single.just(mockUsers)
        val apiUser = rxValue(blogService.getUsers())[0]
        val users = rxValue(sut.getUsers())

        assertThat(users)
            .hasSize(2)
            .contains(
                User(
                    id = apiUser.id,
                    name = apiUser.name,
                    username = apiUser.username,
                    email = apiUser.email
                )
            )
    }

    @Test
    fun `get posts contains correct domain models`() {
        val mockPosts = listOf(
            com.syftapp.codetest.data.model.api.Post(
                userId = 1,
                id = 1,
                title = "This is the title",
                body = "this is a post"
            ),
            com.syftapp.codetest.data.model.api.Post(
                userId = 1,
                id = 2,
                title = "This is the title 2",
                body = "this is another post"
            ),
            com.syftapp.codetest.data.model.api.Post(
                userId = 4,
                id = 3,
                title = "This is the title 3",
                body = "this is a post number 3"
            )
        )

        every { blogService.getPosts() } returns Single.just(mockPosts)
        val apiPost = rxValue(blogService.getPosts())[0]
        val posts = rxValue(sut.getPosts())

        assertThat(posts)
            .hasSize(3)
            .contains(
                Post(
                    id = apiPost.id,
                    userId = apiPost.userId,
                    title = apiPost.title,
                    body = apiPost.body
                )
            )
    }

    @Test
    fun `get comments contains correct domain models`() {
        val mockComments = listOf(
            com.syftapp.codetest.data.model.api.Comment(
                postId = 1,
                id = 1,
                name = "Steve",
                email = "steve@jobs.com",
                body = "this is a comment"
            ),
            com.syftapp.codetest.data.model.api.Comment(
                postId = 1,
                id = 1,
                name = "Steve",
                email = "steve@jobs.com",
                body = "this is a comment"
            ),
            com.syftapp.codetest.data.model.api.Comment(
                postId = 1,
                id = 1,
                name = "Steve",
                email = "steve@jobs.com",
                body = "this is a comment"
            )
        )

        every { blogService.getComments() } returns Single.just(mockComments)
        val apiComment = rxValue(blogService.getComments())[0]
        val comments = rxValue(sut.getComments())

        assertThat(comments)
            .hasSize(3)
            .contains(
                Comment(
                    postId = apiComment.postId,
                    id = apiComment.id,
                    name = apiComment.name,
                    email = apiComment.email,
                    body = apiComment.body
                )
            )
    }

    private fun <T> rxValue(apiItem: Single<T>): T = apiItem.test().values()[0]
}