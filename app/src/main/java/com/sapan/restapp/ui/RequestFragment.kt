package com.sapan.restapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sapan.restapp.R
import com.sapan.restapp.databinding.FragmentRequestBinding
import com.sapan.restapp.viewmodel.RequestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment: Fragment() {
    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMethodSpinner()

        viewModel.requestUrl.observe(viewLifecycleOwner) { url ->
            binding.etRequestUri.setText(url)
        }

        binding.btnSend.setOnClickListener {
            val url = binding.etRequestUri.text.toString()
            val method = binding.spinnerMethod.selectedItem.toString()
            val bodyType = when (binding.radioGroupBody.checkedRadioButtonId) {
                R.id.radioText -> "Text"
                R.id.radioFile -> "File"
                R.id.radioForm -> "Form"
                else -> ""
            }
            val requestBody = binding.etJsonBody.text.toString()
            // TODO
        }
    }

    private fun setupMethodSpinner() {
        val methods = arrayOf(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "HEAD",
            "OPTIONS"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, methods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMethod.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}