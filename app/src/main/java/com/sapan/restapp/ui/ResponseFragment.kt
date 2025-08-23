package com.sapan.restapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sapan.restapp.R
import com.sapan.restapp.databinding.FragmentResponseBinding
import com.sapan.restapp.viewmodel.ResponseViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class ResponseFragment: Fragment() {
    private val TAG = "ResponseFragment"
    private var _binding: FragmentResponseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResponseViewModel by viewModels()
    private lateinit var headersAdapter: HeadersAdapter

    // expandable sections
    private var isStatusExpanded = true
    private var isHeaderExpanded = true
    private var isBodyExpanded = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "viewModel hasCode=${viewModel.hashCode()}")
        _binding = FragmentResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeadersRecyclerView()
        setupExpandableSections()
        setupObservers()
    }

    private fun setupHeadersRecyclerView() {
        headersAdapter = HeadersAdapter()
        binding.rvHeaders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = headersAdapter
        }
    }

    private fun setupExpandableSections() {
        // status section
        binding.llStatusHeader.setOnClickListener {
            isStatusExpanded = !isStatusExpanded
            binding.llStatusContent.visibility = if (isStatusExpanded) View.VISIBLE else View.GONE
            updateDropDownIcon(binding.ivStatusDropdown, isStatusExpanded)
        }

        // headers section
        binding.llHeadersHeader.setOnClickListener {
            isHeaderExpanded = !isHeaderExpanded
            binding.rvHeaders.visibility = if (isHeaderExpanded) View.VISIBLE else View.GONE
            updateDropDownIcon(binding.ivHeadersDropdown, isHeaderExpanded)
        }

        // body section
        binding.llBodyHeader.setOnClickListener {
            isBodyExpanded = !isBodyExpanded
            binding.svBodyContent.visibility = if (isBodyExpanded) View.VISIBLE else View.GONE
            updateDropDownIcon(binding.ivBodyDropdown, isBodyExpanded)
        }
    }

    private fun updateDropDownIcon(imageView: android.widget.ImageView, isExpanded: Boolean) {
        val iconRes = if (isExpanded) {
            R.drawable.ic_arrow_drow_up // Use appropriate drawable
        } else {
            R.drawable.ic_arrow_drop_down // Use appropriate drawable
        }
        imageView.setImageResource(iconRes)
    }

    private fun setupObservers() {
        viewModel.responseCode.observe(viewLifecycleOwner) { code ->
            Log.d(TAG, "response code=$code")
            binding.tvResponseCode.text = code?.toString() ?: "--"

            val colorRes = when {
                code == null -> android.R.color.darker_gray
                code in 200..299 -> android.R.color.holo_green_dark
                code in 300..399 -> android.R.color.holo_blue_dark
                code in 400..499 -> android.R.color.holo_orange_dark
                code >= 500 -> android.R.color.holo_red_dark
                else -> android.R.color.darker_gray
            }

            binding.tvResponseBody.setTextColor(resources.getColor(colorRes, null))
        }

        viewModel.responseTime.observe(viewLifecycleOwner) { time ->
            binding.tvResponseTime.text = if (time != null) "${time} ms" else "--"
        }

        viewModel.responseBody.observe(viewLifecycleOwner) { body ->
            body?.let {
                try {
                    val json = JSONObject(it)
                    val formatJson = json.toString(4) // 4 space indentation
                    binding.tvResponseBody.text = formatJson
                } catch (e: JSONException) {
                    binding.tvResponseBody.text = it
                }
            } ?: run {
                binding.tvResponseBody.text = "No response body"
            }
        }

        viewModel.responseHeader.observe(viewLifecycleOwner) { headers ->
            if (!::headersAdapter.isInitialized) {
                setupHeadersRecyclerView()
            }

            headers?.let {
                val headerList = mutableListOf<Pair<String, String>>()

                // Convert Map<String, List<String>> to list of pairs for adapter
                it.forEach { (key, values) ->
                    values.forEach { value ->
                        headerList.add(Pair(key, value))
                    }
                }

                headersAdapter.submitList(headerList)

                // Update headers count in header section title
                val headersTitle = "Headers (${headerList.size})"
                if (binding.llHeadersHeader.childCount > 0) {
                    val firstChild = binding.llHeadersHeader.getChildAt(0)
                    if (firstChild is android.widget.TextView) {
                        firstChild.text = headersTitle
                    }
                }
            } ?: run {
                headersAdapter.submitList(emptyList())
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvResponseBody.text = "Error: $it"

                if (viewModel.responseCode.value == null) {
                    binding.tvResponseCode.text = "ERR"
                    binding.tvResponseCode.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                }

                viewModel.updateError(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}