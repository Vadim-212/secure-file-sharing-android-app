package com.vadim212.securefilesharingapp.presentation.presenter

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.vadim212.securefilesharingapp.domain.DownloadedFile
import com.vadim212.securefilesharingapp.domain.FileKey
import com.vadim212.securefilesharingapp.domain.base.DefaultObserver
import com.vadim212.securefilesharingapp.domain.usecase.DownloadFile
import com.vadim212.securefilesharingapp.domain.usecase.GetFileKey
import com.vadim212.securefilesharingapp.domain.utils.*
import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.model.ShareFileModel
import com.vadim212.securefilesharingapp.presentation.view.ReceivingFilesView
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

@PerActivity
class ReceivingFilesFragmentPresenter @Inject constructor(private val downloadFileUseCase: DownloadFile, private val getFileKeyUseCase: GetFileKey): Presenter {
    private var receivingFilesView: ReceivingFilesView? = null
    private var downloadedFileName: String? = null
    private var senderUserId: String? = null
    private var recipientUserId: String? = null

    fun setView(receivingFilesView: ReceivingFilesView) {
        this.receivingFilesView = receivingFilesView
    }

    override fun resume() { }

    override fun pause() { }

    override fun destroy() {
        this.downloadFileUseCase.dispose()
        this.receivingFilesView = null
    }

    suspend fun initialize(senderUserId: String) {
        this.hideViewRetry()
        this.showViewLoading()

        val recipientUserId = UserIdHelper.getUserIdFromDataStore(this.receivingFilesView?.context()!!).first()
        this.senderUserId = senderUserId
        this.recipientUserId = recipientUserId
        this.downloadFile(senderUserId, recipientUserId)
    }

    fun saveFilesToExternalDir(saveDirUri: Uri) {
        val decryptedFileDir = ContextWrapper(this.receivingFilesView?.context()!!)
            .getDir("decrypted_files", Context.MODE_PRIVATE)
        val decryptedArchivePath = File(decryptedFileDir,"archive.zip")
        ZipFilesHelper.unzipFilesToUri(decryptedArchivePath, saveDirUri, this.receivingFilesView?.context()!!)
        this.onFilesSaved()
    }

    private fun downloadFile(senderUserId: String, recipientUserId: String) {
        this.downloadFileUseCase.execute(DownloadFileObserver(),
            DownloadFile.Companion.Params.forDownloadFile(senderUserId, recipientUserId))
    }

    private fun getFileKey(senderUserId: String, recipientUserId: String) {
        this.getFileKeyUseCase.execute(GetFileKeyObserver(),
            GetFileKey.Companion.Params.forFileKey(senderUserId, recipientUserId))
    }

    private fun showViewLoading() {
        this.receivingFilesView?.showLoading() // TODO: add null check - ?: throw Exception("")
    }

    private fun hideViewLoading() {
        this.receivingFilesView?.hideLoading()
    }

    private fun showViewRetry() {
        this.receivingFilesView?.showRetry()
    }

    private fun hideViewRetry() {
        this.receivingFilesView?.hideRetry()
    }

    private fun showErrorMessage(e: Exception) {
        this.receivingFilesView?.showError(e.message ?: e.javaClass.simpleName)
    }

    private fun saveDownloadedArchive(downloadedFile: DownloadedFile) {
        val fileToWriteDir = ContextWrapper(this.receivingFilesView?.context()!!)
            .getDir("downloaded_files", Context.MODE_PRIVATE)
        val zipArchivePath = File(fileToWriteDir,"archive.zip")
        if (zipArchivePath.exists()) {
            zipArchivePath.delete()
        }
//        zipArchivePath.outputStream().apply {
//            write(downloadedFile.body.bytes())
//            flush()
//            close()
//        }
        //Log.d("PRESENTER", "body.bytes: ${downloadedFile.body.bytes().size}")
        FilesHelper.saveBytesToFileDir(downloadedFile.body.bytes(), "archive.zip", fileToWriteDir)

        val patternMatcher = Regex(".+filename=\"(.+?)\".*").toPattern().matcher(downloadedFile.headers["Content-Disposition"].toString())
        patternMatcher.find()
        this.downloadedFileName = patternMatcher.group(1)

        this.getFileKey(this.senderUserId!!, this.recipientUserId!!)
    }

    private fun decryptFile(fileKey: FileKey) {
        val filename = this.downloadedFileName
        if (filename.isNullOrEmpty()) {
            // TODO: add show error, show retry if filename is null?
            //this.showErrorMessage()
            return
        }


        // base64 string to secret key
        val encodedSecretKeyBytes = Base64.decode(fileKey.fileKey, Base64.NO_WRAP)
        // get keypair
        val keyPair = AsymmetricKeysHelper.getAsymmetricKeyPair()
        if (keyPair == null) {
            // TODO: show error
        }
        // decrypt secret key with private key
        val secretKeyBytes = RSAEncryptionHelper.decrypt(encodedSecretKeyBytes, keyPair?.private!!)
        val secretKey = AESEncryptionOptionsHelper.bytesToSecretKey(secretKeyBytes)
        // get IV from filename
        val initializationVectorBytes =
            FilesHelper.parseInitializationVectorFromGeneratedFileName(this.downloadedFileName!!)
        // get encrypted archive bytes
        val downloadedFileDir = ContextWrapper(this.receivingFilesView?.context()!!)
            .getDir("downloaded_files", Context.MODE_PRIVATE)
        val downloadedArchivePath = File(downloadedFileDir,"archive.zip")
        val archiveBytes = FilesHelper.getBytesFromFile(downloadedArchivePath)
        // decrypt archive
        val decryptedArchiveBytes = AESEncryptionHelper.decrypt(archiveBytes, secretKey, initializationVectorBytes)
        // save decrypted archive
        val decryptedFileDir = ContextWrapper(this.receivingFilesView?.context()!!)
            .getDir("decrypted_files", Context.MODE_PRIVATE)
        //val decryptedArchivePath = File(decryptedFileDir,"archive.zip")
        FilesHelper.saveBytesToFileDir(decryptedArchiveBytes, "archive.zip", decryptedFileDir)

        this.onFileDecrypted() // TODO: call from another place?

    }

    private fun onFileDownloaded() {
        this.receivingFilesView?.onFileDownloaded()
    }

    private fun onFileDecrypted() {
        this.receivingFilesView?.onFileDecrypted()
        this.receivingFilesView?.hideLoading()
    }

    private fun onFilesSaved() {
        this.receivingFilesView?.onFilesSaved()
    }

    private inner class DownloadFileObserver: DefaultObserver<DownloadedFile>() {
        override fun onNext(t: DownloadedFile) {
            this@ReceivingFilesFragmentPresenter.saveDownloadedArchive(t)
        }

        override fun onError(e: Throwable) {
            this@ReceivingFilesFragmentPresenter.hideViewLoading()
            this@ReceivingFilesFragmentPresenter.showErrorMessage(e as Exception)
            this@ReceivingFilesFragmentPresenter.showViewRetry()
        }

        override fun onComplete() {
//            this@ReceivingFilesFragmentPresenter.hideViewLoading()
//            this@ReceivingFilesFragmentPresenter.onFileDownloaded()
        }
    }

    private inner class GetFileKeyObserver: DefaultObserver<FileKey>() {
        override fun onNext(t: FileKey) {
            this@ReceivingFilesFragmentPresenter.onFileDownloaded()
            this@ReceivingFilesFragmentPresenter.decryptFile(t)
        }

        override fun onError(e: Throwable) {
            this@ReceivingFilesFragmentPresenter.hideViewLoading()
            this@ReceivingFilesFragmentPresenter.showErrorMessage(e as Exception)
            this@ReceivingFilesFragmentPresenter.showViewRetry()
        }

        override fun onComplete() {
            //this@ReceivingFilesFragmentPresenter.hideViewLoading()
            //this@ReceivingFilesFragmentPresenter.onFileDownloaded()
        }
    }
}