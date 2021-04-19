package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.posts.GetPostsUseCase.Companion.MAX_ITEMS_PER_PAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent

class PostsPresenter(private val getPostsUseCase: GetPostsUseCase) : ContinuationListener, KoinComponent {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: PostsView

    fun bind(view: PostsView) {
        this.view = view
        compositeDisposable.add(loadPosts(1))
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun showDetails(post: Post) {
        view.render(PostScreenState.PostSelected(post))
    }

    override fun getMoreItems(nextIndex: Int) {
        val nextPage = (nextIndex / MAX_ITEMS_PER_PAGE) + 1
        compositeDisposable.add(loadPosts(nextPage))
    }

    private fun loadPosts(page: Int) = getPostsUseCase.execute(page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { view.render(PostScreenState.Loading) }
        .doAfterTerminate { view.render(PostScreenState.FinishedLoading) }
        .subscribe(
            { view.render(PostScreenState.DataAvailable(it)) },
            { view.render(PostScreenState.Error(it)) }
        )
}