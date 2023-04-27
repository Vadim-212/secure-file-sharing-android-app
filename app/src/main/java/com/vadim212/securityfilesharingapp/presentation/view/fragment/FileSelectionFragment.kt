package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentFileSelectionBinding
import com.vadim212.securityfilesharingapp.databinding.FragmentHomeBinding
import com.vadim212.securityfilesharingapp.presentation.view.BaseView
import java.io.File

class FileSelectionFragment : BaseFragment(), BaseView {
    private var _binding: FragmentFileSelectionBinding? = null
    private val binding get() = _binding!!
    private val args: FileSelectionFragmentArgs by navArgs()
    private var selectedFilesUris: List<Uri>? = null

    private val filePickerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (data.clipData != null) {
                    val count = data.clipData!!.itemCount
                    val uris: ArrayList<Uri> = arrayListOf()
                    for (i in 0 until count) {
                        val currentUri = data.clipData?.getItemAt(i)?.uri
                        if (currentUri != null) {
                            uris.add(currentUri)
                        }
                    }
                    this.selectedFilesUris = uris

                    if (uris.isNotEmpty()) {
//                        binding?.textviewFragmentMainFilesUriList?.text =
//                                //uris.joinToString { str -> str.toFile().name }
//                            uris.joinToString { str -> str.path.toString() } +
//                                    File(uris[0].path).length().toString()
                        binding.textviewFragmentFileSelectionFilesList.text =
                            uris.joinToString("\n") { uri -> File(uri.path).name }

                    }

                }
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
        _binding = FragmentFileSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        initializeListeners()
    }

    override fun initializeViews() {
        binding.textviewFragmentFileSelectionUserValue.text = args.userId

        openFilePicker()
    }

    override fun initializeListeners() {
        binding.buttonFragmentFileSelectionFilePicker.setOnClickListener {
            openFilePicker()
        }

        binding.buttonFragmentFileSelectionNextButton.setOnClickListener {
            if (selectedFilesUris != null && selectedFilesUris?.isNotEmpty() == true) {

                // Navigation
                val userId = args.userId
                val filesUrisString = selectedFilesUris!!.joinToString("|")
                val action = FileSelectionFragmentDirections.actionFileSelectionFragmentToSendingFilesFragment(userId, filesUrisString)
                Navigation.findNavController(binding.root).navigate(action)

            } else {
                this.showToastMessage("Error! Please, select files")
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }
}