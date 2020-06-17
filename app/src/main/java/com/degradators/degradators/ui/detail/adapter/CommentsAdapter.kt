package com.degradators.degradators.ui.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.model.comment.CommentList
import com.degradators.degradators.model.comment.Expanded
import kotlinx.android.synthetic.main.comment.view.*

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CommentItemViewHolder>() {

    private var actionLock: Boolean = false
    private var openWriteBlock: Boolean = false

    private var commentList: MutableList<CommentList> = mutableListOf()
    private var oldCommentList: MutableList<CommentList> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        return CommentItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        holder.bind(commentList[position])
        if (commentList[position].isExpandedItem == Expanded.Open) {
            holder.itemView.toggle_btn.background =
                ContextCompat.getDrawable(
                    holder.itemView.toggle_btn.context,
                    R.drawable.ic_remove_circle_outline_black_24dp
                )
        } else if (commentList[position].isExpandedItem == Expanded.Close) {
            holder.itemView.toggle_btn.background =
                ContextCompat.getDrawable(
                    holder.itemView.toggle_btn.context,
                    R.drawable.ic_control_point_black_24dp
                )
        }

        holder.itemView.toggle_btn.setOnClickListener {
            if (!actionLock) {
                actionLock = true
                if (commentList[position].isExpandedItem == Expanded.Open) {
                    commentList[position].isExpandedItem = Expanded.Close
                    collapse(position)
                } else if (commentList[position].isExpandedItem == Expanded.Close) {
                    expand(position)
                    commentList[position].isExpandedItem = Expanded.Open
                }
            }
        }

        holder.itemView.answer.setOnClickListener {
            if(openWriteBlock){
                openWriteBlock = false
                holder.itemView.addComment.visibility = View.GONE
            } else {
                openWriteBlock = true
                holder.itemView.addComment.visibility = View.VISIBLE
            }
        }
    }

    private fun collapse(position: Int) {
        val depthParent = commentList[position].depth
        val nextPosition = position + 1

        outerloop@ while (true) {
            if (nextPosition == commentList.size || (commentList[nextPosition].depth <= depthParent)) {
                break@outerloop
            }
            commentList.removeAt(nextPosition)
        }

        notifyDataSetChanged()

        actionLock = false
    }

    private fun expand(position: Int) {

        val row = commentList[position]


        /**
         * add element just below of clicked row
         */
        val indexStart = oldCommentList.indexOf(row)
        val indexFinish = oldCommentList.indexOf(commentList[position + 1])
        oldCommentList.forEachIndexed { index, commentListItem ->
            if (index in (indexStart + 1) until indexFinish) {
                if (commentListItem.isExpandedItem == Expanded.Close) {
                    commentListItem.isExpandedItem = Expanded.Open
                }
                commentList.add(index, commentListItem)
            }
        }

        notifyDataSetChanged()


        actionLock = false
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun update(items: MutableList<CommentList>) {
        this.commentList = items
        oldCommentList.addAll(items)
        notifyDataSetChanged()
    }

    class CommentItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(
            commentList: CommentList
        ) {
            if (commentList.depth > 0) {
                val param = itemView.itemComment.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(commentList.depth * 20, 0, 0, 0)
                itemView.itemComment.layoutParams = param
            }
            if (commentList.isEmptyComments) {
                itemView.toggle_btn.visibility = View.GONE
            }


            itemView.comment.text = commentList.content[0].text
            itemView.comment.setHasTransientState(true)
        }


    }


}
