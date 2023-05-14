package com.vadim212.securefilesharingapp.presentation.presenter

import android.content.Context
import android.content.ContextWrapper
import android.util.Base64
import android.util.Log
import com.vadim212.securefilesharingapp.domain.base.DefaultObserver
import com.vadim212.securefilesharingapp.domain.usecase.ShareFile
import com.vadim212.securefilesharingapp.domain.utils.*
import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.model.ShareFileModel
import com.vadim212.securefilesharingapp.presentation.view.SendingFilesView
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

@PerActivity
class SendingFilesFragmentPresenter @Inject constructor(private val shareFileUseCase: ShareFile): Presenter {
    private var sendingFilesView: SendingFilesView? = null

    fun setView(sendingFilesView: SendingFilesView) {
        this.sendingFilesView = sendingFilesView
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
        val zipArchiveBytes = FilesHelper.getBytesFromFile(zipArchivePath)
        // create secret key
        val secretKey = AESEncryptionOptionsHelper.createSymmetricKey()
        // create IV
        val initializationVector = AESEncryptionOptionsHelper.generateRandomInitializationVector(AESEncryptionHelper.getBlockSize())
        // encrypt
        val encodedArchive = AESEncryptionHelper.encrypt(zipArchiveBytes, secretKey, initializationVector)
        // generate file name
        val filename = FilesHelper.generateFileNameWithUUIDAndInitializationVector(initializationVector)
        // save file
        FilesHelper.saveBytesToFileDir(encodedArchive, filename, fileToWriteDir)

        // get bytes from secret key
        val secretKeyBytes = secretKey.encoded
        // get recipient public key from data store
        val recipientPublicKeyBase64String = RecipientUserPublicKeyHelper.getRecipientPublicKeyFromDataStore(this.sendingFilesView?.context()!!).first()
        val recipientPublicKey = AsymmetricKeysHelper.base64StringToPublicKey(recipientPublicKeyBase64String)
        // encrypt
        val encodedSecretKey = RSAEncryptionHelper.encrypt(secretKeyBytes, recipientPublicKey)
        // secret key to base64 string
        val fileKey = Base64.encodeToString(encodedSecretKey, Base64.DEFAULT)

        // get sender user id
        val senderUserId = UserIdHelper.getUserIdFromDataStore(this.sendingFilesView?.context()!!).first()
        val shareFileModel = ShareFileModel(senderUserId, recipientUserId, File(fileToWriteDir, filename), fileKey)
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
        ZipFilesHelper.zipFiles(filesPaths, archiveSavePath)
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