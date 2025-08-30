package com.sapan.restapp.collections.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapan.restapp.collections.models.Request
import com.sapan.restapp.databinding.ItemRequestBinding

class RequestAdapter(
    private val onRequestClick: (Request) -> Unit
) : ListAdapter<Request, RequestAdapter.RequestViewHolder>(RequestDiffUtilCallback()){

    class RequestViewHolder(private val itemBinding: ItemRequestBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(request: Request, onClick: () -> Unit) {
            itemBinding.tvRequestName.text = request.name
            itemBinding.tvRequestUrl.text = request.url
            itemBinding.tvRequestMethod.text = request.method

            val methodColor = when (request.method.uppercase()) {
                "GET" -> android.R.color.holo_green_dark
                "POST" -> android.R.color.holo_blue_dark
                "PUT" -> android.R.color.holo_orange_dark
                "DELETE" -> android.R.color.holo_red_dark
                else -> android.R.color.darker_gray
            }

            itemBinding.tvRequestMethod.setBackgroundResource(methodColor)
            itemBinding.root.setOnClickListener{ onClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.bind(request, { onRequestClick(request) })
    }
}

class RequestDiffUtilCallback: DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean =
        oldItem == newItem

}