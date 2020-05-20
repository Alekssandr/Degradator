package com.degradators.degradators.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.degradators.degradators.R
import com.degradators.degradators.databinding.ArticleItemBinding
import com.degradators.degradators.model.ArticleMessage

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ArticleMessagesAdapter : RecyclerView.Adapter<ArticleMessagesAdapter.ItemViewHolder>() {

    private var articleMessageList: List<ArticleMessage> = emptyList()
    private val publishSubjectItem = PublishSubject.create<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ArticleItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.article_item, parent, false)
        return ItemViewHolder(binding)
    }

    fun getClickItemObserver(): Observable<Int> {
        return publishSubjectItem
    }

    override fun getItemCount(): Int {
        return articleMessageList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(articleMessageList[position])
//        if(holder.itemView.buttonEng.isClickable) {
//            holder.itemView.buttonEng.setOnClickListener {
//                if (previousPosition == DEFAULT) {
//                    publishSubjectItem.onNext(stepsList.indexOf(stepsListShuffled[position]))
//                    previousPosition = position
//                }
//            }
//        }
    }

    fun update(items: List<ArticleMessage>) {
        this.articleMessageList = items
        notifyDataSetChanged()
    }

    class ItemViewHolder(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pairRusEng: ArticleMessage) {
//            this.binding.text = pairRusEng.rus
//            when {
//                pairRusEng.dayOfLearning == GREY -> {
//                    this.binding.buttonEng.setBackgroundResource(R.drawable.btn_idle_default)
//                    this.binding.buttonEng.isClickable = true
//                }
//                pairRusEng.dayOfLearning == GREEN -> {
//                    this.binding.buttonEng.setBackgroundResource(R.drawable.audio_idle)
//                    this.binding.buttonEng.isClickable = false
//                }
//                pairRusEng.dayOfLearning == RED -> {
//                    this.binding.buttonEng.setBackgroundResource(R.drawable.btn_idle_not_correct)
//
//                    this.binding.buttonEng.isClickable = false
//                }
//                else -> {
//                    this.binding.buttonEng.setBackgroundResource(R.drawable.btn_idle_choose)
//                    this.binding.buttonEng.isClickable = false
//                }
//            }
        }
    }
}
