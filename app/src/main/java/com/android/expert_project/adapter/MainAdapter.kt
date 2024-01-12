package com.android.expert_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.expert_project.Items
import com.android.expert_project.checkHeart
import com.android.expert_project.data.Item
import com.android.expert_project.databinding.MainRecyclerViewBinding
import java.text.DecimalFormat

class MainAdapter(val item: MutableList<Item>) : RecyclerView.Adapter<MainAdapter.Holder>() {
    var itemClick: ItemClick? = null
    var itemLongClick: ItemLongClick? = null

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    interface ItemLongClick {
        fun onLongClick(item: Item, position: Int)
    }

    inner class Holder(val binding: MainRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val thumbnail = binding.ivThumbnail
        val title = binding.tvItemTitle
        val address = binding.tvAddress
        val price = binding.tvPrice
        val heartCount = binding.tvHeartCount
        val chatCount = binding.tvChatCount
        val ivHeart = binding.ivHeart
        val layoutItem = binding.layoutItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MainRecyclerViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var item = item[position]

        with(holder) {
            thumbnail.setImageResource(item.image)
            title.text = item.title
            address.text = item.address
            price.text = item.price.decimal()
            heartCount.text = item.heartCount.toString()
            chatCount.text = item.chatCount.toString()
            checkHeart(item, ivHeart)
            itemView.setOnLongClickListener {
                itemLongClick?.onLongClick(item, position)
                true
            }
            itemView.setOnClickListener {
                itemClick?.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return Items.itemList.size
    }
}

fun Int.decimal(): String {
    val decimal = DecimalFormat("#,###")
    return "${decimal.format(this)}원"
}