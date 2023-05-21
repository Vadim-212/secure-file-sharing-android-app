package com.vadim212.securefilesharingapp.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.vadim212.securefilesharingapp.R
import com.vadim212.securefilesharingapp.databinding.FragmentMyIdBinding
import com.vadim212.securefilesharingapp.domain.utils.UserIdHelper
import com.vadim212.securefilesharingapp.presentation.view.BaseView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyIDFragment : BaseFragment(), BaseView {
    private var _binding: FragmentMyIdBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        initializeListeners()
    }

    override fun initializeListeners() {
        binding.buttonFragmentMyIdBackButton.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    override fun initializeViews() {
        CoroutineScope(Dispatchers.Main).launch {
            val userId = UserIdHelper.getUserIdFromDataStore(this@MyIDFragment.requireContext()).first()
            binding.textviewFragmentMyIdValue.text = userId

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(userId, BarcodeFormat.QR_CODE, 250, 250)
            binding.imageviewFragmentMyIdQr.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}