package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentFileSelectionBinding
import com.vadim212.securityfilesharingapp.databinding.FragmentHomeBinding
import com.vadim212.securityfilesharingapp.presentation.view.BaseView

class FileSelectionFragment : BaseFragment(), BaseView {
    private var _binding: FragmentFileSelectionBinding? = null
    private val binding get() = _binding!!
    private val args: FileSelectionFragmentArgs by navArgs()

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
        //initializeListeners()
        binding.ttttt.text = "User ID: ${args.userId}"
    }

    override fun initializeListeners() {
        TODO("Not yet implemented")
    }
}