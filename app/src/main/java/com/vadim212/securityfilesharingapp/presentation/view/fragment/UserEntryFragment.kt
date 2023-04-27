package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentUserEntryBinding
import com.vadim212.securityfilesharingapp.presentation.di.HasComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.DaggerUserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.UserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.presenter.UserEntryFragmentPresenter
import com.vadim212.securityfilesharingapp.presentation.view.BaseView
import com.vadim212.securityfilesharingapp.presentation.view.LoadDataView
import com.vadim212.securityfilesharingapp.presentation.view.UserEntryView
import com.vadim212.securityfilesharingapp.presentation.view.activity.BaseActivity
import java.util.regex.Pattern
import javax.inject.Inject

class UserEntryFragment : BaseFragment(), UserEntryView, HasComponent<UserPublicKeyComponent> {
    private var _binding: FragmentUserEntryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userEntryFragmentPresenter: UserEntryFragmentPresenter
    private var userPublicKeyComponent: UserPublicKeyComponent? = null

    private lateinit var loadingMaterialDialog: MaterialDialog
    private lateinit var errorMaterialDialog: MaterialDialog

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
        initializeInjector()
        this.getComponent(UserPublicKeyComponent::class).inject(this)
        //DaggerUserPublicKeyComponent.builder().applicationComponent().activityModule().build().inject(this)
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

        initializeViews()
        initializeListeners()
    }

    override fun initializeViews() {
        this.userEntryFragmentPresenter.setView(this)

        loadingMaterialDialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_loading).noAutoDismiss()

        errorMaterialDialog = MaterialDialog(requireContext())
            .icon(R.drawable.baseline_error_outline_24).title(text = "Error").positiveButton(text = "OK") // TODO: string resources
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
                this.userEntryFragmentPresenter.initialize(userId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.userEntryFragmentPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        this.userEntryFragmentPresenter.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.userEntryFragmentPresenter.destroy()
    }

    override fun onRecipientPublicKeySaved() {
        val userId = binding.edittextFragmentUserEntryUserIdInput.text.toString()
        // TODO: add regex check for user id
        val action = UserEntryFragmentDirections.actionUserEntryFragmentToFileSelectionFragment(userId)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun showLoading() {
        Log.d("UserPublicKeyTest", "showLoading")
        loadingMaterialDialog.show()
    }

    override fun hideLoading() {
        Log.d("UserPublicKeyTest", "hideLoading")
        loadingMaterialDialog.dismiss()
    }

    override fun showRetry() {
        Log.d("UserPublicKeyTest", "showRetry")
    }

    override fun hideRetry() {
        Log.d("UserPublicKeyTest", "hideRetry")
    }

    override fun showError(message: String) {
        Log.d("UserPublicKeyTest", "showError: $message")
        this.showToastMessage("Error: $message")
        errorMaterialDialog.show {
            message(text = message)
        }
    }

    override fun context(): Context {
        return activity?.applicationContext!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun initializeInjector() {
        this.userPublicKeyComponent = DaggerUserPublicKeyComponent.builder()
            .applicationComponent((activity as BaseActivity).getApplicationComponent())
            .activityModule((activity as BaseActivity).getActivityModule())
            .build()
    }

    override fun getComponent(): UserPublicKeyComponent {
        return userPublicKeyComponent!!
    }
}