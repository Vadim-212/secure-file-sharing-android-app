package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentSendingFilesBinding
import com.vadim212.securityfilesharingapp.domain.utils.*
import com.vadim212.securityfilesharingapp.presentation.view.BaseView


class SendingFilesFragment : BaseFragment(), BaseView {
    private var _binding: FragmentSendingFilesBinding? = null
    private val binding get() = _binding!!
    private val args: SendingFilesFragmentArgs by navArgs()
    private lateinit var loadingMaterialDialog: MaterialDialog

    private lateinit var filesHelper: FilesHelper
    private lateinit var zipFilesHelper: ZipFilesHelper
    private lateinit var aesEncryptionHelper: AESEncryptionHelper
    private lateinit var aesEncryptionOptionsHelper: AESEncryptionOptionsHelper
    private lateinit var rsaEncryptionHelper: RSAEncryptionHelper
    private lateinit var asymmetricKeysHelper: AsymmetricKeysHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.tttt.text = "${args.userId}\n\n${args.filesUrisString.split("|").joinToString("\n")}"
    }

    override fun initializeListeners() {
        TODO("Not yet implemented")
    }

    override fun initializeViews() {
        TODO("Not yet implemented")
    }

    fun initializeHelpers() {
        filesHelper = FilesHelper(requireContext(), requireActivity().contentResolver)
        zipFilesHelper = ZipFilesHelper()
        aesEncryptionHelper = AESEncryptionHelper()
        aesEncryptionOptionsHelper = AESEncryptionOptionsHelper()
        rsaEncryptionHelper = RSAEncryptionHelper()
        asymmetricKeysHelper = AsymmetricKeysHelper()
    }

}