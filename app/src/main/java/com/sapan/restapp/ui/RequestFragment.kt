package com.sapan.restapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sapan.restapp.R
import com.sapan.restapp.databinding.FragmentRequestBinding
import com.sapan.restapp.viewmodel.RequestViewModel
import com.sapan.restapp.viewmodel.ResponseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class RequestFragment: Fragment() {
    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private val requestViewModel: RequestViewModel by viewModels()
    private val responseViewModel: ResponseViewModel by viewModels()

    private var selectedFile: File? = null

    private val filePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedFile = File(uri.path)
                    binding.radioFile.isChecked = true
                    binding.etJsonBody.setText(uri.path)
                }
            }
        }

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
        setupObservers()
        setupClickListeners()
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

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    requestViewModel.requestUrl.collect { url ->
                        binding.etRequestUri.setText(url)
                    }
                }

                launch {
                    requestViewModel.selectedMethod.collect { method ->
                        val position = (binding.spinnerMethod.adapter as ArrayAdapter<String>).getPosition(method)
                        if (position >= 0) {
                            binding.spinnerMethod.setSelection(position)
                        }
                    }
                }

                launch {
                    requestViewModel.isLoading.collect { isLoading ->
                        binding.btnSend.isEnabled = !isLoading
                        binding.btnSend.text = if (isLoading) "Sending..." else "Send"
                    }
                }

                launch {
                    requestViewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            sendRequest()
        }

        binding.btnAddHeaders.setOnClickListener {
            showHeaderDialog()
        }

        binding.btnAddQueryParams.setOnClickListener {
            showQueryParamsDialog()
        }

        binding.btnPath.setOnClickListener {
            showPathDialog()
        }

        binding.radioFile.setOnClickListener {
            openFilePicker()
        }
    }

    private fun sendRequest() {
        val url = binding.etRequestUri.text.toString()
        if (url.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a URL", Toast.LENGTH_SHORT).show()
            return
        }

        val method = binding.spinnerMethod.selectedItem.toString()
        val bodyType = when (binding.radioGroupBody.checkedRadioButtonId) {
            R.id.radioText -> "Text"
            R.id.radioFile -> "File"
            R.id.radioForm -> "Form"
            else -> ""
        }
        val bodyContent = binding.etJsonBody.text.toString()

        requestViewModel.makeRequest(
            url = url,
            method = method,
            headers = requestViewModel.headers.value ?: emptyMap(),
            queryParams = requestViewModel.queryParams.value ?: emptyMap(),
            bodyType = bodyType,
            bodyContent = bodyContent,
            file = selectedFile
        ) { responseCode, responseTime, responseBody, responseHeaders ->
            // Convert Map<String, String> to Map<String, List<String>>
            val headersAsList = responseHeaders.mapValues { listOf(it.value) }

            responseViewModel.updateResponse(
                responseCode,
                responseTime,
                responseBody,
                headersAsList
            )

            // Switch to response tab using activity's viewpager
            (requireActivity() as? MainActivity)?.binding?.viewpager?.setCurrentItem(1, true)
        }
    }

    private fun showHeaderDialog() {
        Toast.makeText(requireContext(), "Header dialog will be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun showQueryParamsDialog() {
        Toast.makeText(requireContext(), "Query param dialog will be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun showPathDialog() {
        Toast.makeText(requireContext(), "Path dialog will be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        filePickerResult.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}