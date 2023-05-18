package com.vadim212.securefilesharingapp.presentation.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.vadim212.securefilesharingapp.databinding.FragmentFileSelectionBinding
import com.vadim212.securefilesharingapp.domain.utils.FilePathHelper
import com.vadim212.securefilesharingapp.domain.utils.FilesHelper
import com.vadim212.securefilesharingapp.domain.utils.getFileName
import com.vadim212.securefilesharingapp.presentation.view.BaseView
import java.io.File
import java.io.FileInputStream


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
                    Log.d("FILE_PICKER_TEST", "uri.toString: ${uris[0].toString() ?: ""}")
                    Log.d("FILE_PICKER_TEST", "uri.path: ${uris[0].path ?: ""}")
                    Log.d("FILE_PICKER_TEST", "uri.encodedPath: ${uris[0].encodedPath ?: ""}")
                    Log.d("FILE_PICKER_TEST", "File(uri.path).path: ${File(uris[0].path!!).path ?: ""}")
                    Log.d("FILE_PICKER_TEST", "File(uri.path).canonicalPath: ${File(uris[0].path!!).canonicalPath ?: ""}")
                    Log.d("FILE_PICKER_TEST", "File(uri.path).absolutePath: ${File(uris[0].path!!).absolutePath ?: ""}")
                    Log.d("FILE_PICKER_TEST", "File(uri.toString).path: ${File(uris[0].toString()).path ?: ""}")
                    //Log.d("FILE_PICKER_TEST", "File(uri.toString).inputStream.readBytes: ${File(uris[0].path!!).inputStream().readBytes()[1] ?: ""}")
                    this.selectedFilesUris = uris

//                   // Log.d("FILE_PICKER_TEST", "getPath: ${FilePathHelper.getPath(uris[0], requireActivity().applicationContext)}")
//                    //Log.d("FILE_PICKER_TEST", "File(getPath).inputStream.readBytes: ${File(FilePathHelper.getPath(uris[0], requireActivity().applicationContext)).inputStream().readBytes()[1] ?: ""}")
//                    val uriInputStream = FileInputStream(requireContext().contentResolver.openFileDescriptor(uris[0], "r")?.fileDescriptor)
//                    val bytes = uriInputStream.readBytes()
//                    Log.d("FILE_PICKER_TEST", "contentResolver: ${bytes.size}")
//                    Log.d("FILE_PICKER_TEST", "contentResolver fileName: ${requireContext().contentResolver.getFileName(uris[0])}")



//                    FilesHelper.saveBytesToFileDir(bytes,
//                        requireContext().contentResolver.getFileName(uris[0]),
//                        File("/storage/emulated/0/Documents"))
//
//                    FilesHelper.saveBytesToFileDir(bytes,
//                        requireContext().contentResolver.getFileName(uris[0]),
//                        ContextWrapper(context)
//                            .getDir("encrypted_files", Context.MODE_PRIVATE))
//
//                    uriInputStream.close()

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

//        val permissionsStorage = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)
//        val requestExternalStorage = 1
//        val permission =
//            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), permissionsStorage, requestExternalStorage)
//        }


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
//                val filePathsString = selectedFilesUris!!.map { uri ->
//                    File(uri.path!!).path
//                }.joinToString("|")
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