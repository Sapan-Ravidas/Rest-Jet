package com.sapan.restapp.collections.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sapan.restapp.collections.models.Collection
import com.sapan.restapp.collections.viewmodels.CollectionViewModel
import com.sapan.restapp.databinding.DialogCreateCollectionBinding
import com.sapan.restapp.databinding.FragmentCollectionsBinding
import com.sapan.restapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionsFragment: Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectionViewModel by viewModels()
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var requestAdapter: RequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        collectionAdapter = CollectionAdapter(
            onCollectionClick = { collection ->
                viewModel.setCurrentCollection(collection)
                binding.toolbar.title = collection.name
            },
            onCollectionLongClick = { collection ->
                showCollectionOptionsDialog(collection)
            }
        )

        requestAdapter = RequestAdapter(
            onRequestClick = { request ->
                // TODO: load-requests
                (requireActivity() as? MainActivity)?.binding?.viewpager?.setCurrentItem(1, true)
            }
        )

        binding.rvCollections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = collectionAdapter
        }

        binding.rvRequests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = requestAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collections.collect { collections ->
                    collectionAdapter.submitList(collections)
                    binding.tvEmpty.visibility = if (collections.isEmpty()) View.VISIBLE else View.GONE
                }
            }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requests.collect { requests ->
                    requestAdapter.submitList(requests)
                    binding.rvRequests.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
                }
            }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentCollection.collect {
                    if (it == null) {
                        binding.toolbar.title = "Collections"
                        viewModel.loadRootCollections()
                    } else {
                        viewModel.loadRequests(collectionId = it.id)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddCollection.setOnClickListener {
            showCreateCollectionDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            viewModel.setCurrentCollection(null)
        }
    }

    private fun showCollectionDialog(
        title: String,
        positiveButtonText: String,
        initialName: String = "",
        initialDescription: String = "",
        onPositiveClick: (String, String) -> Unit
    ) {
        val dialogBinding = DialogCreateCollectionBinding.inflate(layoutInflater)
        dialogBinding.etCollectionName.setText(initialName)
        dialogBinding.etCollectionDescription.setText(initialDescription)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                val name = dialogBinding.etCollectionName.text.toString().trim()
                val description = dialogBinding.etCollectionDescription.text.toString().trim()
                if (name.isNotEmpty()) {
                    onPositiveClick(name, description)
                } else {
                    dialogBinding.etCollectionName.error = "Collection name is required"
                    return@setPositiveButton
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun showCreateCollectionDialog(parentId: String? = null) {
        showCollectionDialog(
            title = if (parentId == null) "Create Collection" else "Create sub-collection",
            positiveButtonText = "Create",
            onPositiveClick = { name, description ->
                viewModel.createCollection(name, description, parentId)
            }
        )
    }

    private fun showRenameCollectionDialog(collection: Collection) {
        showCollectionDialog(
            title = "Rename Collection",
            positiveButtonText = "Save",
            initialName = collection.name,
            initialDescription = collection.description,
            onPositiveClick = { name, description ->
                val updatedCollection = collection.copy(
                    name = name,
                    description = description,
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.updateCollection(updatedCollection)
            }
        )
    }

    private fun showDeleteCollectionDialog(collection: Collection) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Collection")
            .setMessage("Are you sure you want to delete '${collection.name}?. This will also delete all requests in this collection.")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteCollection(collection)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showCreateSubCollectionDialog(parentCollection: Collection) {
        showCreateCollectionDialog(parentCollection.id)
    }

    private fun showCollectionOptionsDialog(collection: Collection) {
        val options = arrayOf("Rename", "Delete", "Add Sub-Collections")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(collection.name)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showRenameCollectionDialog(collection)
                    1 -> showDeleteCollectionDialog(collection)
                    2 -> showCreateSubCollectionDialog(collection)
                }
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}