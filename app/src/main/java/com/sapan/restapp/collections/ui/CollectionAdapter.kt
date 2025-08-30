package com.sapan.restapp.collections.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapan.restapp.collections.models.Collection
import com.sapan.restapp.databinding.ItemCollectionBinding

class CollectionAdapter(
    private val onCollectionClick: (Collection) -> Unit,
    private val onCollectionLongClick: (Collection) -> Unit
): ListAdapter<Collection, CollectionAdapter.CollectionViewHolder>(CollectionDiffUtilCallback()) {

    class CollectionViewHolder(private val itemBinding: ItemCollectionBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(collection: Collection, onClick: () -> Unit, onLongClick: () -> Unit) {
            itemBinding.tvCollectionName.text = collection.name
            itemBinding.root.setOnClickListener{ onClick() }
            itemBinding.root.setOnLongClickListener{
                onLongClick()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = getItem(position)
        holder.bind(collection, { onCollectionClick(collection) }, { onCollectionLongClick(collection) })
    }
}

class CollectionDiffUtilCallback: DiffUtil.ItemCallback<Collection>() {
    override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean =
        oldItem == newItem

}