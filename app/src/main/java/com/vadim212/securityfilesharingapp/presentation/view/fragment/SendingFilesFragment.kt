package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentSendingFilesBinding
import com.vadim212.securityfilesharingapp.domain.utils.*
import com.vadim212.securityfilesharingapp.presentation.di.HasComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.DaggerShareFileComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.DaggerUserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.ShareFileComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.UserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.presenter.SendingFilesFragmentPresenter
import com.vadim212.securityfilesharingapp.presentation.view.SendingFilesView
import com.vadim212.securityfilesharingapp.presentation.view.activity.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SendingFilesFragment : BaseFragment(), SendingFilesView, HasComponent<ShareFileComponent> {
    private var _binding: FragmentSendingFilesBinding? = null
    private val binding get() = _binding!!
    private val args: SendingFilesFragmentArgs by navArgs()
    private lateinit var loadingMaterialDialog: MaterialDialog
    private lateinit var errorMaterialDialog: MaterialDialog

    @Inject
    lateinit var sendingFilesFragmentPresenter: SendingFilesFragmentPresenter
    private var shareFileComponent: ShareFileComponent? = null

    private lateinit var filesHelper: FilesHelper
    private lateinit var zipFilesHelper: ZipFilesHelper
    private lateinit var aesEncryptionHelper: AESEncryptionHelper
    private lateinit var aesEncryptionOptionsHelper: AESEncryptionOptionsHelper
    private lateinit var rsaEncryptionHelper: RSAEncryptionHelper
    private lateinit var asymmetricKeysHelper: AsymmetricKeysHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
        this.getComponent(ShareFileComponent::class).inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendingFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeHelpers()

        initializeViews()

        //binding.tttt.text = "${args.userId}\n\n${args.filesUrisString.split("|").joinToString("\n")}"

        val filesPathsList = args.filesPathsString.split("|")
        CoroutineScope(Dispatchers.Main).launch {
            this@SendingFilesFragment.sendingFilesFragmentPresenter.initialize(filesPathsList, args.userId)
        }
    }

    override fun initializeViews() {
        this.sendingFilesFragmentPresenter.setView(this)
        this.loadingMaterialDialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_loading).noAutoDismiss()
        this.errorMaterialDialog = MaterialDialog(requireContext())
            .icon(R.drawable.baseline_error_outline_24).title(text = "Error").positiveButton(text = "OK") // TODO: string resources

    }

    override fun initializeListeners() {
        TODO("Not yet implemented")
    }

    override fun onFileSended() {
        Log.d("SendingFilesFragmentTest", "onFileSended")
        this.showToastMessage("File sended!")
    }

    override fun showLoading() {
        Log.d("SendingFilesFragmentTest", "showLoading")
        this.loadingMaterialDialog.show()
    }

    override fun hideLoading() {
        Log.d("SendingFilesFragmentTest", "hideLoading")
        this.loadingMaterialDialog.dismiss()
    }

    override fun showRetry() {
        Log.d("SendingFilesFragmentTest", "showRetry")
    }

    override fun hideRetry() {
        Log.d("SendingFilesFragmentTest", "hideRetry")
    }

    override fun showError(message: String) {
        Log.d("SendingFilesFragmentTest", "showError: $message")
        this.showToastMessage("Error: $message")
        errorMaterialDialog.show {
            message(text = message)
        }
    }

    override fun context(): Context {
        return activity?.applicationContext!!
    }

    private fun initializeHelpers() {
        filesHelper = FilesHelper(requireContext(), requireActivity().contentResolver)
        zipFilesHelper = ZipFilesHelper()
        aesEncryptionHelper = AESEncryptionHelper()
        aesEncryptionOptionsHelper = AESEncryptionOptionsHelper()
        rsaEncryptionHelper = RSAEncryptionHelper()
        asymmetricKeysHelper = AsymmetricKeysHelper()
    }

    private fun initializeInjector() {
        this.shareFileComponent = DaggerShareFileComponent.builder()
            .applicationComponent((activity as BaseActivity).getApplicationComponent())
            .activityModule((activity as BaseActivity).getActivityModule())
            .build()
    }

    override fun getComponent(): ShareFileComponent {
        return this.shareFileComponent!!
    }

}