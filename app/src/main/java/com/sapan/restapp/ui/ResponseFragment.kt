package com.sapan.restapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sapan.restapp.databinding.FragmentResponseBinding
import com.sapan.restapp.viewmodel.ResponseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResponseFragment: Fragment() {
    private var _binding: FragmentResponseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResponseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.responseCode.observe(viewLifecycleOwner) { code ->
            binding.tvResponseCode.text = code?.toString() ?: "--"
        }

        viewModel.responseTime.observe(viewLifecycleOwner) { time ->
            binding.tvResponseTime.text = if (time != null) "${time} ms" else "--"
        }

        viewModel.responseBody.observe(viewLifecycleOwner) { body ->
            // TODO: display in response body
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // TODO: display error message
            }
        }

        viewModel.responseHeader.observe(viewLifecycleOwner) { headers ->
            // TODO: display headers in recycler view or textview
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}