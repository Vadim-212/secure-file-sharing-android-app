package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.Navigation
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentUserEntryBinding
import com.vadim212.securityfilesharingapp.presentation.view.BaseView
import java.util.regex.Pattern

class UserEntryFragment : BaseFragment(), BaseView {
    private var _binding: FragmentUserEntryBinding? = null
    private val binding get() = _binding!!

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            this.showToastMessage("Cancelled")
        } else {
            this.showToastMessage("Scanned: " + result.contents)

            // TODO: put in a separate class
            val UUID_PATTERN_STRING = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}"
            val uuidPattern = Pattern.compile(UUID_PATTERN_STRING)
            val uuidPatternMatcher = uuidPattern.matcher(result.contents)
            if (uuidPatternMatcher.matches()) {
                binding.edittextFragmentUserEntryUserIdInput.setText(result.contents)
            } else {
                this.showToastMessage("Error! It's not UUID")
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListeners()
    }

    override fun initializeListeners() {
        binding.buttonFragmentUserEntryScanQr.setOnClickListener {
            val scanOptions = ScanOptions() // TODO: move options initialization outside the onclick listener
            scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scanOptions.setBeepEnabled(false)
            barcodeLauncher.launch(scanOptions)
        }

        binding.buttonFragmentUserEntryNextButton.setOnClickListener {
            val userId = binding.edittextFragmentUserEntryUserIdInput.text.toString()
            if (userId.isEmpty()) {
                this.showToastMessage("Error! User ID is empty")
            } else {
                // TODO: add regex check for user id
                val action = UserEntryFragmentDirections.actionUserEntryFragmentToFileSelectionFragment(userId)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}