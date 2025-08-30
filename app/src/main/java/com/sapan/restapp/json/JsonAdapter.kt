package com.sapan.restapp.json

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapan.restapp.R
import com.sapan.restapp.databinding.ItemJsonNodeBinding

class JsonAdapter: ListAdapter<JsonNode, JsonAdapter.JsonViewHolder>(JsonNodeDiffUtil()) {

    var onItemClick: ((JsonNode) -> Unit) ?= null

    class JsonViewHolder(private val binding: ItemJsonNodeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(node: JsonNode, onClick: (() -> Unit)? = null) {
            val paddingStart = node.level * 24
            binding.root.setPaddingRelative(paddingStart, 0, 0, 0)

            // Hide key for root level empty keys
            if (node.key.isEmpty() && node.level == 0) {
                binding.tvKey.visibility = View.GONE
                binding.tvColon.visibility = View.GONE
            } else {
                binding.tvKey.visibility = View.VISIBLE
                binding.tvKey.text = node.key
                binding.tvColon.visibility = if (node.value != null) View.VISIBLE else View.GONE
            }

            when {
                node.isObject -> {
                    binding.tvValue.text = "{...}"
                    binding.ivExpandCollapse.visibility = View.VISIBLE
                    binding.ivExpandCollapse.setImageResource(
                        if (node.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
                    )
                }
                node.isArray -> {
                    binding.tvValue.text = "[...]"
                    binding.ivExpandCollapse.visibility = View.VISIBLE
                    binding.ivExpandCollapse.setImageResource(
                        if (node.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
                    )
                }
                else -> {
                    binding.tvValue.text = node.value?.toString() ?: "null"
                    binding.ivExpandCollapse.visibility = View.GONE
                }
            }

            // Set click listeners for expand/collapse
            binding.ivExpandCollapse.setOnClickListener { onClick?.invoke() }
            binding.root.setOnClickListener {
                if (node.isObject || node.isArray) {
                    onClick?.invoke()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonViewHolder {
        val binding = ItemJsonNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JsonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JsonViewHolder, position: Int) {
        val node = getItem(position)
        holder.bind(node) {
            onItemClick?.invoke(node)
        }
    }
}

class JsonNodeDiffUtil: DiffUtil.ItemCallback<JsonNode>() {
    override fun areItemsTheSame(oldItem: JsonNode, newItem: JsonNode): Boolean {
        return oldItem.key == newItem.key && oldItem.level == newItem.level
    }

    override fun areContentsTheSame(oldItem: JsonNode, newItem: JsonNode): Boolean {
        return oldItem == newItem
    }

}