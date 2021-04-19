package com.syftapp.codetest.posts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.*
import com.syftapp.codetest.R
import com.syftapp.codetest.data.model.domain.Post
import kotlinx.android.synthetic.main.view_post_list_item.view.*

class PostsAdapter(private val presenter: PostsPresenter) : RecyclerView.Adapter<PostViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private var previousList: List<Post> = listOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        this.layoutManager = recyclerView.layoutManager as LinearLayoutManager
    }

    private val diffCallback: DiffUtil.ItemCallback<Post> = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    }

    private val differ: AsyncListDiffer<Post> by lazy {
        AsyncListDiffer<Post>(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

        }, AsyncDifferConfig.Builder<Post>(diffCallback).build())
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = differ.currentList[position]
        holder.bind(post)

        if (position == differ.currentList.size - 1) presenter.getMoreItems(differ.currentList.size)
    }

    fun submitList(list: List<Post>) {
        previousList = differ.currentList
        differ.submitList(differ.currentList.plus(list))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.view_post_list_item, parent, false)

        return PostViewHolder(view, presenter)
    }
}

class PostViewHolder(private val view: View, private val presenter: PostsPresenter) :
    RecyclerView.ViewHolder(view) {

    fun bind(item: Post) {
        view.postTitle.text = item.title
        view.bodyPreview.text = item.body
        view.setOnClickListener { presenter.showDetails(item) }
    }
}

interface ContinuationListener {
    fun getMoreItems(nextIndex: Int)
}
