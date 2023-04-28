package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.vadim212.securityfilesharingapp.R
import com.vadim212.securityfilesharingapp.databinding.FragmentHomeBinding
import com.vadim212.securityfilesharingapp.domain.utils.AsymmetricKeysHelper
import com.vadim212.securityfilesharingapp.domain.utils.UserIdHelper
import com.vadim212.securityfilesharingapp.presentation.di.HasComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.DaggerUserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.di.components.UserPublicKeyComponent
import com.vadim212.securityfilesharingapp.presentation.model.UserPublicKeyModel
import com.vadim212.securityfilesharingapp.presentation.presenter.HomeFragmentPresenter
import com.vadim212.securityfilesharingapp.presentation.view.BaseView
import com.vadim212.securityfilesharingapp.presentation.view.HomeView
import com.vadim212.securityfilesharingapp.presentation.view.activity.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeView, HasComponent<UserPublicKeyComponent> {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userIdHelper: UserIdHelper
    private var asymmetricKeysHelper: AsymmetricKeysHelper = AsymmetricKeysHelper()
    private lateinit var loadingMaterialDialog: MaterialDialog
    private lateinit var errorMaterialDialog: MaterialDialog

    @Inject
    lateinit var homeFragmentPresenter: HomeFragmentPresenter
    private var userPublicKeyComponent: UserPublicKeyComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
        this.getComponent(UserPublicKeyComponent::class).inject(this)
        userIdHelper = UserIdHelper(requireContext())
        loadingMaterialDialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_loading).noAutoDismiss()
        errorMaterialDialog = MaterialDialog(requireContext())
            .icon(R.drawable.baseline_error_outline_24)
            .title(text = "Error").positiveButton(text = "OK") // TODO: string resources
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        initializeListeners()

        this.showLoading()
        checkFirstRun()
        this.hideLoading()
    }

    override fun initializeListeners() {
        binding.buttonFragmentMainSendFiles.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_userEntryFragment)
        }

        binding.buttonFragmentMainReceiveFiles.setOnClickListener {

        }
    }

    override fun initializeViews() {
        this.homeFragmentPresenter.setView(this)
    }

    private fun checkFirstRun() {
        // TODO: add check if public key saved on server (maybe add boolean var to data store, that will set to true after request in presenter is success)
        CoroutineScope(Dispatchers.Main).launch {
            //Looper.prepare()
            //Looper.loop()
            // check user id in datastore
            var userId: String = ""
//            this@HomeFragment.userIdHelper.getUserIdFromDataStore().onEach {
//                userId = it
//                Log.d("CHECK_FIRST_RUN", "userId in onEach: $userId")
//            }.launchIn(this)
            userId = this@HomeFragment.userIdHelper.getUserIdFromDataStore().first()
            Log.d("CHECK_FIRST_RUN", "userId after helper: $userId")

            // check keypair in keystore
            val keyPair = this@HomeFragment.asymmetricKeysHelper.getAsymmetricKeyPair()
            Log.d("CHECK_FIRST_RUN", "keyPair: $keyPair")

            // if any of them null - create new id and keys and send them to server
            if (userId.isEmpty() || keyPair == null) {
                // create new user id and keypair
                userId = UUID.randomUUID().toString()
                val newKeyPair = this@HomeFragment.asymmetricKeysHelper.createAsymmetricKeyPair()

                // save new user id to data store
                this@HomeFragment.userIdHelper.saveUserIdToDataStore(userId)

                // send new user id and public key to server
                val publicKeyBase64 = Base64.encodeToString(newKeyPair.public.encoded, Base64.DEFAULT)
                this@HomeFragment.homeFragmentPresenter.initialize(UserPublicKeyModel(userId, publicKeyBase64)) // TODO: if there is ERROR with POST request then DON'T save user id to data store
                //Looper.myLooper()?.quit()

            }
        }
    }

    private fun initializePostUserPublicKey(userPublicKeyModel: UserPublicKeyModel) {
        this.homeFragmentPresenter.initialize(userPublicKeyModel)
    }

    override fun onPause() {
        super.onPause()
        this.homeFragmentPresenter.pause()
    }

    override fun onResume() {
        super.onResume()
        this.homeFragmentPresenter.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.homeFragmentPresenter.destroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showLoading() {
        Log.d("HomeFragmentTest", "showLoading")
        loadingMaterialDialog.show()
    }

    override fun hideLoading() {
        Log.d("HomeFragmentTest", "hideLoading")
        loadingMaterialDialog.dismiss()
    }

    override fun showRetry() {
        Log.d("HomeFragmentTest", "showRetry")
    }

    override fun hideRetry() {
        Log.d("HomeFragmentTest", "hideRetry")
    }

    override fun showError(message: String) {
        Log.d("HomeFragmentTest", "showError: $message")
        this.showToastMessage("Error: $message")
        errorMaterialDialog.show {
            message(text = message)
        }
    }

    override fun context(): Context {
        return activity?.applicationContext!!
    }

    private fun initializeInjector() {
        this.userPublicKeyComponent = DaggerUserPublicKeyComponent.builder()
            .applicationComponent((activity as BaseActivity).getApplicationComponent())
            .activityModule((activity as BaseActivity).getActivityModule())
            .build()
    }

    override fun getComponent(): UserPublicKeyComponent {
        return userPublicKeyComponent!!
    }
}