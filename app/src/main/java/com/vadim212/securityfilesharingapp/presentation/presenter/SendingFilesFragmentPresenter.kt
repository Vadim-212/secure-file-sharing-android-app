package com.vadim212.securityfilesharingapp.presentation.presenter

import android.content.Context
import android.content.ContextWrapper
import android.util.Base64
import android.util.Log
import com.vadim212.securityfilesharingapp.domain.base.DefaultObserver
import com.vadim212.securityfilesharingapp.domain.usecase.ShareFile
import com.vadim212.securityfilesharingapp.domain.utils.*
import com.vadim212.securityfilesharingapp.presentation.di.PerActivity
import com.vadim212.securityfilesharingapp.presentation.model.ShareFileModel
import com.vadim212.securityfilesharingapp.presentation.view.SendingFilesView
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

@PerActivity
class SendingFilesFragmentPresenter @Inject constructor(private val shareFileUseCase: ShareFile): Presenter {
    private var sendingFilesView: SendingFilesView? = null
    private var zipFilesHelper: ZipFilesHelper? = null
    private var filesHelper: FilesHelper? = null
    private var aesEncryptionHelper: AESEncryptionHelper? = null
    private var aesEncryptionOptionsHelper: AESEncryptionOptionsHelper? = null
    private var rsaEncryptionHelper: RSAEncryptionHelper? = null
    private var asymmetricKeysHelper: AsymmetricKeysHelper? = null
    private var recipientUserPublicKeyHelper: RecipientUserPublicKeyHelper? = null
    private var userIdHelper: UserIdHelper? = null

    fun setView(sendingFilesView: SendingFilesView) {
        this.sendingFilesView = sendingFilesView
        this.zipFilesHelper = ZipFilesHelper()
        this.filesHelper = FilesHelper(this.sendingFilesView?.context()!!, this.sendingFilesView?.context()!!.contentResolver)
        this.aesEncryptionHelper = AESEncryptionHelper()
        this.aesEncryptionOptionsHelper = AESEncryptionOptionsHelper()
        this.rsaEncryptionHelper = RSAEncryptionHelper()
        this.asymmetricKeysHelper = AsymmetricKeysHelper()
        this.recipientUserPublicKeyHelper = RecipientUserPublicKeyHelper(this.sendingFilesView?.context()!!)
        this.userIdHelper = UserIdHelper(this.sendingFilesView?.context()!!)
    }

    override fun resume() { }

    override fun pause() { }

    override fun destroy() {
        this.shareFileUseCase.dispose()
        this.sendingFilesView = null
    }

    suspend fun initialize(filesPaths: List<String>, recipientUserId: String) {
        this.hideViewRetry()
        this.showViewLoading()

        val fileToWriteDir = ContextWrapper(sendingFilesView?.context()!!)
            .getDir("encrypted_files", Context.MODE_PRIVATE)
        val zipArchivePath = File(fileToWriteDir,"archive.zip")
        this.createZipArchive(filesPaths, zipArchivePath)

        // get bytes from file
        val zipArchiveBytes = this.filesHelper?.getBytesFromFile(zipArchivePath)!!
        // create secret key
        val secretKey = this.aesEncryptionOptionsHelper?.createSymmetricKey()!!
        // create IV
        val initializationVector = this.aesEncryptionOptionsHelper?.generateRandomInitializationVector(this.aesEncryptionHelper?.getBlockSize()!!)!!
        // encrypt
        val encodedArchive = this.aesEncryptionHelper?.encrypt(zipArchiveBytes, secretKey, initializationVector)
        // generate file name
        val filename = this.filesHelper?.generateFileNameWithUUIDAndInitializationVector(initializationVector)
        // save file
        this.filesHelper?.saveBytesToFileDir(encodedArchive!!, filename!!, fileToWriteDir)

        // get bytes from secret key
        val secretKeyBytes = secretKey.encoded
        // get recipient public key from data store
        val recipientPublicKeyBase64String = this.recipientUserPublicKeyHelper?.getRecipientPublicKeyFromDataStore()?.first()
        val recipientPublicKey = this.asymmetricKeysHelper?.base64StringToPublicKey(recipientPublicKeyBase64String!!)
        // encrypt
        val encodedSecretKey = this.rsaEncryptionHelper?.encrypt(secretKeyBytes, recipientPublicKey!!)
        // secret key to base64 string
        val fileKey = Base64.encodeToString(encodedSecretKey!!, Base64.DEFAULT)

        // get sender user id
        val senderUserId = this.userIdHelper?.getUserIdFromDataStore()?.first()
        val shareFileModel = ShareFileModel(senderUserId!!, recipientUserId, File(fileToWriteDir, filename!!), fileKey)
        // send file
        this.sendFile(shareFileModel)
    }

    private fun sendFile(shareFileModel: ShareFileModel) {
        val senderIdRequestBody = this.shareFileUseCase.strToRequestBody(shareFileModel.senderUserId)
        val recipientIdRequestBody = this.shareFileUseCase.strToRequestBody(shareFileModel.recipientUserId)
        val fileMultipart = this.shareFileUseCase.fileToMultipartBodyPart(shareFileModel.encryptedFile)
        val fileKeyRequestBody = this.shareFileUseCase.strToRequestBody(shareFileModel.encryptedFileKey)

        this.shareFileUseCase.execute(ShareFileObserver(), ShareFile.Companion.Params.forShareFile(
            senderIdRequestBody, recipientIdRequestBody, fileMultipart, fileKeyRequestBody
        ))
    }

    private fun showViewLoading() {
        this.sendingFilesView?.showLoading() // TODO: add null check - ?: throw Exception("")
    }

    private fun hideViewLoading() {
        this.sendingFilesView?.hideLoading()
    }

    private fun showViewRetry() {
        this.sendingFilesView?.showRetry()
    }

    private fun hideViewRetry() {
        this.sendingFilesView?.hideRetry()
    }

    private fun showErrorMessage(e: Exception) {
        this.sendingFilesView?.showError(e.message ?: e.javaClass.simpleName)
    }

    private fun createZipArchive(filesPaths: List<String>, archiveSavePath: File) {
        //val savePath = File(archiveSaveDir, archiveFileName)
        zipFilesHelper?.zipFiles(filesPaths, archiveSavePath)
    }

    private fun encryptFile(filepath: String) {
        //val bytes = this.filesHelper?.readBytesFromUri()
        //aesEncryptionHelper?.encrypt()
    }

    private fun onFileSended() {
        this.sendingFilesView?.onFileSended()
    }

    private inner class ShareFileObserver: DefaultObserver<ResponseBody>() {
        override fun onNext(t: ResponseBody) {
            super.onNext(t)
            Log.d("ShareFileObserver", "onNext, $t, ${t.contentLength()}")
        }

        override fun onError(e: Throwable) {
            this@SendingFilesFragmentPresenter.hideViewLoading()
            this@SendingFilesFragmentPresenter.showErrorMessage(e as Exception)
            this@SendingFilesFragmentPresenter.showViewRetry()
        }

        override fun onComplete() {
            this@SendingFilesFragmentPresenter.hideViewLoading()
            this@SendingFilesFragmentPresenter.onFileSended()
        }
    }
}