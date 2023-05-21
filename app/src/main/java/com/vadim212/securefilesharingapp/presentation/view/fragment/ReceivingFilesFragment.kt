package com.vadim212.securefilesharingapp.presentation.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.vadim212.securefilesharingapp.R
import com.vadim212.securefilesharingapp.databinding.FragmentReceivingFilesBinding
import com.vadim212.securefilesharingapp.presentation.di.HasComponent
import com.vadim212.securefilesharingapp.presentation.di.components.DaggerDownloadFileComponent
import com.vadim212.securefilesharingapp.presentation.di.components.DownloadFileComponent
import com.vadim212.securefilesharingapp.presentation.presenter.ReceivingFilesFragmentPresenter
import com.vadim212.securefilesharingapp.presentation.view.ReceivingFilesView
import com.vadim212.securefilesharingapp.presentation.view.activity.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.DocumentFragment
import javax.inject.Inject

class ReceivingFilesFragment : BaseFragment(), ReceivingFilesView, HasComponent<DownloadFileComponent> {
    private var _binding: FragmentReceivingFilesBinding? = null
    private val binding get() = _binding!!
    private val args: ReceivingFilesFragmentArgs by navArgs()

    @Inject
    lateinit var receivingFilesFragmentPresenter: ReceivingFilesFragmentPresenter
    private var downloadFileComponent: DownloadFileComponent? = null

    private val directoryPicker: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (data.data != null) {
                    Log.d("DIR_PICKER_TEST", "Uri: ${data.data}")
                    this.receivingFilesFragmentPresenter.saveFilesToExternalDir(data.data!!)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceivingFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeInjector()
        this.getComponent(DownloadFileComponent::class).inject(this)

        initializeViews()
        initializeListeners()

        CoroutineScope(Dispatchers.Main).launch {
            this@ReceivingFilesFragment.receivingFilesFragmentPresenter.initialize(args.senderUserId)
        }
        //this.onFileDecrypted()
    }

    override fun initializeListeners() {
        binding.buttonFragmentReceivingFilesSaveButton.setOnClickListener {
            this.openDirPicker()
        }
    }

    override fun initializeViews() {
        this.receivingFilesFragmentPresenter.setView(this)
        binding.textviewFragmentReceivingFilesSenderValue.text = args.senderUserId
        binding.textviewFragmentReceivingFilesStatus.text = "Downloading files..."
    }

    override fun onFileDownloaded() {
        binding.textviewFragmentReceivingFilesStatus.text = "Decrypting files..."
    }

    override fun onFileDecrypted() {
        binding.buttonFragmentReceivingFilesSaveButton.isEnabled = true
        binding.textviewFragmentReceivingFilesStatus.text = "Files downloaded!"
    }

    override fun onFilesSaved() {
        this.showToastMessage("Files saved!")
        Navigation.findNavController(binding.root).navigate(R.id.action_receivingFilesFragment_to_homeFragment)
    }

    private fun openDirPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        directoryPicker.launch(intent)
    }

    override fun showLoading() {
        Log.d("ReceivingFilesFragmentTest", "showLoading")
        //binding.textviewFragmentReceivingFilesStatus.visibility = View.VISIBLE
        binding.progressbarFragmentReceivingFilesProgress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Log.d("ReceivingFilesFragmentTest", "hideLoading")
        binding.progressbarFragmentReceivingFilesProgress.visibility = View.INVISIBLE
    }

    override fun showRetry() {
        Log.d("ReceivingFilesFragmentTest", "showRetry")
    }

    override fun hideRetry() {
        Log.d("ReceivingFilesFragmentTest", "hideRetry")
    }

    override fun showError(message: String) {
        Log.d("ReceivingFilesFragmentTest", "showError")
        this.showToastMessage("Error: $message")
//        errorMaterialDialog.show {
//            message(text = message)
//        }
    }

    private fun initializeInjector() {
        this.downloadFileComponent = DaggerDownloadFileComponent.builder()
            .applicationComponent((activity as BaseActivity).getApplicationComponent())
            .activityModule((activity as BaseActivity).getActivityModule())
            .build()
    }

    override fun getComponent(): DownloadFileComponent {
        return this.downloadFileComponent!!
    }

    override fun context(): Context {
        return activity?.applicationContext!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}