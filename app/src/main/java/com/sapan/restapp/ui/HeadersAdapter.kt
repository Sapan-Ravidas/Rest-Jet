package com.sapan.restapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapan.restapp.databinding.ItemHeaderBinding

class HeadersAdapter : ListAdapter<Pair<String, String>, HeadersAdapter.HeaderViewHolder>(HeaderDiffCallback()) {

    class HeaderViewHolder(private val binding: ItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, value: String) {
            binding.tvHeaderName.text = name
            binding.tvHeaderValue.text = value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val (key, value) = getItem(position)
        holder.bind(key, value)
    }
}

class HeaderDiffCallback: DiffUtil.ItemCallback<Pair<String, String>>() {
    override fun areItemsTheSame(
        oldItem: Pair<String, String>,
        newItem: Pair<String, String>
    ): Boolean {
        return oldItem.first == newItem.first && oldItem.second == newItem.second
    }

    override fun areContentsTheSame(
        oldItem: Pair<String, String>,
        newItem: Pair<String, String>
    ): Boolean {
       return oldItem == newItem
    }

}