package com.vadim212.securityfilesharingapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.vadim212.securityfilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securityfilesharingapp.data.entity.UserPublicKeyEntity
import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.data.repository.UserPublicKeyRepository
import com.vadim212.securityfilesharingapp.databinding.FragmentMainBinding
import com.vadim212.securityfilesharingapp.domain.base.DefaultObserver
import com.vadim212.securityfilesharingapp.domain.usecase.DownloadFile
import com.vadim212.securityfilesharingapp.domain.usecase.GetFileKey
import com.vadim212.securityfilesharingapp.domain.usecase.GetUserPublicKey
import com.vadim212.securityfilesharingapp.domain.usecase.ShareFile
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*
import java.security.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


class MainFragment : Fragment() {
    var binding: FragmentMainBinding? = null
    var filePickerUri: Uri? = null
    var contentResolver: ContentResolver? = null
    var openFilePicker: ActivityResultLauncher<Intent>? = null


    var currentEncryptedFileName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        openFilePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        val uris: ArrayList<Uri> = arrayListOf()
                        for (i in 0 until count) {
                            val currentUri = data.clipData?.getItemAt(i)?.uri
                            if (currentUri != null) {
                                uris.add(currentUri)
                            }
                        }

                        if (uris.isNotEmpty()) {
                            binding?.textviewFragmentMainFilesUriList?.text =
                                //uris.joinToString { str -> str.toFile().name }
                                uris.joinToString { str -> str.path.toString() } +
                                        File(uris[0].path).length().toString()
                        }

                    }
                }
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentResolver = requireContext().contentResolver

        binding?.buttonFragmentMainChooseFile?.setOnClickListener {
            openFile(Uri.parse("/"))
//            Log.d("GET_SECURITY_PROVIDERS", Security.getProviders().joinToString(";\n"))
//            Log.d("GET_SECURITY_ALGORITHMS_1", Security.getAlgorithms("Cipher").joinToString(";\n"))
//            Log.d("GET_SECURITY_ALGORITHMS_2", Security.getAlgorithms("KeyStore").joinToString(";\n"))

        }

        binding?.buttonFragmentMainFileEncrypt?.setOnClickListener {
            if (filePickerUri != null) {
                //encryptFile(filePickerUri.toString())

                //encryptFile(filePickerUri!!)

                var keyPair = getAsymmetricKeyPair()
                if (keyPair == null) {
                    Toast.makeText(context, "KeyPair is not exist. Creating new...", Toast.LENGTH_SHORT).show()
                    keyPair = createAsymmetricKeyPair()
                }
                //encryptFile(filePickerUri!!, keyPair.public)
                val symmetricSecretKey = createSymmetricKey()
                encryptFile(filePickerUri!!, symmetricSecretKey)
                val encodedKey = asymmetricEncryption(symmetricSecretKey.encoded, keyPair.public)
                binding?.edittextFragmentMainEncodedKey?.setText(encodedKey)

            }
            else {
                Toast.makeText(context, "File is not picked", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.buttonFragmentMainFileDecrypt?.setOnClickListener {
            //decryptFile()

            val keyPair = getAsymmetricKeyPair()
            if (keyPair == null) {
                Toast.makeText(context, "Error! KeyPair is not exist. Cannot decrypt file!", Toast.LENGTH_SHORT).show()
            } else {
                //decryptFile(keyPair.private)
                val decodedKey = asymmetricDecryption(binding?.edittextFragmentMainEncodedKey?.text?.toString()!!, keyPair.private)
                decryptFile(SecretKeySpec(decodedKey, "AES"))
            }
        }






        val fileSharingRepository = FileSharingRepository()
        val getFileKey = GetFileKey(fileSharingRepository)

        val userPublicKeyRepository = UserPublicKeyRepository()
        val getUserPublicKey = GetUserPublicKey(userPublicKeyRepository)

        val shareFile = ShareFile(fileSharingRepository)
        val downloadFile = DownloadFile(fileSharingRepository)

        val fileToWriteDir = ContextWrapper(context)
            .getDir("encryptedFiles", Context.MODE_PRIVATE)
        val encryptedFiles = fileToWriteDir.listFiles()?.map { element -> element.path }
        val zipPath = File(fileToWriteDir,"encryptedFiles.zip")
        val unzipDirPath = File(fileToWriteDir, "unzippedFiles" + File.separator)

        val downloadedFilesDir = ContextWrapper(context)
            .getDir("downloadedFiles", Context.MODE_PRIVATE)

        binding?.buttonFragmentMainTestApi?.setOnClickListener {
            //-//getFileKey.execute(GetFileKeyObserver(), GetFileKey.Companion.Params.forFileKey("123test","345test"))
//            getUserPublicKey.execute(GetUserPublicKeyObserver(), GetUserPublicKey.Companion.Params.forUserPublicKey("123test"))


            val multipart = shareFile.fileToMultipartBodyPart(if (zipPath.exists())
            {
                zipPath
            } else {
                File(encryptedFiles?.get(0)!!)
            })
            shareFile.execute(ShareFileObserver(), ShareFile.Companion.Params.forShareFile(
                shareFile.strToRequestBody("123test"),
                shareFile.strToRequestBody("345test"),
                multipart,
                shareFile.strToRequestBody("asdfasfsadfsdf")))

            //-//downloadFile.execute(DownloadFileObserver(downloadedFilesDir), DownloadFile.Companion.Params.forDownloadFile("123test","345test"))
        }


        binding?.buttonFragmentMainZipFiles?.setOnClickListener {
            if (encryptedFiles != null) {
                if (zipPath.exists()) {
                    zipPath.delete()
                }
                zipFiles(encryptedFiles, zipPath)
            }

        }
        binding?.buttonFragmentMainUnzipFiles?.setOnClickListener {
            if (unzipDirPath.exists()) {
                unzipDirPath.deleteRecursively()
            }
            unzipFiles(File(fileToWriteDir,"encryptedFiles.zip"), unzipDirPath.path)
        }



    }

    fun openFile(pickerInitialUri: Uri) {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
////            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "*/*"
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
        //startActivityForResult(intent, 1)


        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "*/*"

        }


        openFilePicker!!.launch(intent)
        /*
        val openFilePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
            }
        }
        openFilePicker.launch(intent)
        */
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            Log.d("FILEOPENER", data.toString())
//            data?.data?.also { uri ->
//                Log.d("FILEOPENER", uri.toString())
//                filePickerUri = uri
//            }
//        }
//    }

    fun createAsymmetricKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder("MyRSAkeys1", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(builder.build())
        return generator.genKeyPair()
    }

    fun getAsymmetricKeyPair(): KeyPair? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val privateKey = keyStore.getKey("MyRSAkeys1", null) as PrivateKey?
        val publicKey = keyStore.getCertificate("MyRSAkeys1")?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }

    fun createSymmetricKey(): SecretKey {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(256)

//        val sk: SecretKey = SecretKeySpec(ByteArray(2),"")

        return generator.generateKey()
    }

    fun generateRandomInitializationVector(blockSize: Int): ByteArray {
        val secureRandom = SecureRandom()
        val iv = ByteArray(blockSize)
        secureRandom.nextBytes(iv)
        return iv
    }

    fun asymmetricEncryption(dataBytes: ByteArray, publicKey: Key): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val bytes = cipher.doFinal(dataBytes)
        //return Base64.encodeToString(bytes, Base64.DEFAULT)

//        val fileToWriteName = "encrypted_file.txt"
//        val fileToWriteDir = ContextWrapper(context)
//            .getDir("encryptedFiles", Context.MODE_PRIVATE)
//        //
//        val fileToWrite = File(fileToWriteDir, fileToWriteName)
//        if (fileToWrite.exists()) fileToWrite.delete()
//        //
//        fileToWrite.outputStream().apply {
//            write(bytes)
//            flush()
//            close()
//        }

        return Base64.encodeToString(bytes, Base64.DEFAULT)

    }


    fun asymmetricDecryption(data: String, privateKey: Key?): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        //val encryptedData = Base64.decode(data, Base64.DEFAULT)


        val bytes = Base64.decode(data, Base64.DEFAULT)

        val decodedData = cipher.doFinal(bytes)

        return decodedData
    }

    //fun encryptFile(filePath: String) {
    fun encryptFile(fileUri: Uri, secretKey: SecretKey) {
        //AES_256/CBC/PKCS5PADDING

        //val secretKey = createSymmetricKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        val iv = generateRandomInitializationVector(cipher.blockSize)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val bytes = readBytesFromUri(fileUri)
        val encodedBytes = cipher.doFinal(bytes)

        currentEncryptedFileName = UUID.randomUUID().toString().replace("-","") + "_" + Base64.encodeToString(iv, Base64.DEFAULT)
        val fileToWriteName = currentEncryptedFileName//"encrypted_file.txt"
        val fileToWriteDir = ContextWrapper(context)
            .getDir("encryptedFiles", Context.MODE_PRIVATE)
        //
        val fileToWrite = File(fileToWriteDir, fileToWriteName)
        if (fileToWrite.exists()) fileToWrite.delete()
        //
        fileToWrite.outputStream().apply {
            write(encodedBytes)
            flush()
            close()
        }

        /*
        //val origFile = File(fileUri)
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        Log.d("FILEOPENER MasterKeys", mainKeyAlias)

        dumpImageMetaData(fileUri)

        val fileToWrite = "encrypted_file.txt"
        val fileToWriteDir = ContextWrapper(context)
            .getDir("encryptedFiles", Context.MODE_PRIVATE)

        val asd = File(fileToWriteDir, fileToWrite)
        if (asd.exists()) asd.delete()

        // TODO: write filename (from dumpMetaData) to the begin of the file

        val encryptedFile = EncryptedFile.Builder(
            File(fileToWriteDir, fileToWrite),
            requireContext(),
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val fileContent = readBytesFromUri(fileUri)//origFile.readBytes()
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
         */
    }

    fun decryptFile(secretKey: SecretKey) {
        //AES_256/CBC/PKCS5PADDING

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        //val iv = generateRandomInitializationVector(cipher.blockSize)
        val iv = Base64.decode(currentEncryptedFileName.split("_").last(), Base64.DEFAULT)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))


        val fileToReadName = currentEncryptedFileName//"encrypted_file.txt"
        val fileToReadDir = ContextWrapper(context)
            .getDir("encryptedFiles", Context.MODE_PRIVATE)
        //
        val fileToRead = File(fileToReadDir, fileToReadName)
        //

        val bytes = fileToRead.inputStream().readBytes()
        val decodedBytes = cipher.doFinal(bytes)

        val fileToWriteDir = ContextWrapper(context)
            .getDir("decryptedFiles", Context.MODE_PRIVATE)
        //val decryptedFile = File(fileToWriteDir, "decrypted_file.gif")
        val decryptedFile = File(fileToWriteDir, currentEncryptedFileName.split("_").first())
        decryptedFile.outputStream().apply {
            write(decodedBytes)
            flush()
            close()
        }

        /*
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val fileToWrite = "encrypted_file.txt"
        val fileToReadDir = ContextWrapper(context)
            .getDir("encryptedFiles", Context.MODE_PRIVATE)

        val encryptedFile = EncryptedFile.Builder(
            File(fileToReadDir, fileToWrite),
            requireContext(),
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        val plainText = byteArrayOutputStream.toByteArray()

        val fileToWriteDir = ContextWrapper(context)
            .getDir("decryptedFiles", Context.MODE_PRIVATE)
        val decryptedFile = File(fileToWriteDir, "decrypted_file.gif")
        decryptedFile.outputStream().apply {
            write(plainText)
            flush()
            close()
        }
         */
    }

    private fun readBytesFromUri(uri: Uri): ByteArray{//String {
        val stringBuilder = StringBuilder()
        var bytes: ByteArray? = null

        contentResolver?.openInputStream(uri)?.use { inputStream ->
            bytes = inputStream.readBytes()
//            BufferedReader(InputStreamReader(inputStream)).use { reader ->
//
//                var line: String? = reader.readLine()
//                while (line != null) {
//                    stringBuilder.append(line)
//                    line = reader.readLine()
//                }
//            }
        }
        //return stringBuilder.toString()
        return bytes!!
    }

    fun dumpImageMetaData(uri: Uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = contentResolver?.query(
            uri, null, null, null, null, null)

        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayName: String = if (!it.isNull(displayNameIndex)) {
                    it.getString(displayNameIndex)
                } else {
                    "Unknown"
                }
                Log.i("FILEOPENER metadata", "Display Name: $displayName")

                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                val size: String = if (!it.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i("FILEOPENER metadata", "Size: $size")
            }
        }
    }

    @Throws(IOException::class)
    fun zipFiles(files: List<String>, archiveSavePath: File) {
        val BUFFER_SIZE = 6 * 1024
        var origin: BufferedInputStream? = null
        val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(archiveSavePath)))
        try {
            val data = ByteArray(BUFFER_SIZE)
            for (i in files.indices) {
                val fi = FileInputStream(files[i])
                origin = BufferedInputStream(fi, BUFFER_SIZE)
                try {
                    val entry = ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1))
                    out.putNextEntry(entry)
                    var count = origin.read(data, 0, BUFFER_SIZE)
                    while (count != -1) {
                        out.write(data, 0, count)
                        count = origin.read(data, 0, BUFFER_SIZE)
                    }
                } finally {
                    origin.close()
                }
            }
        } finally {
            out.close()
        }
    }

    @Throws(IOException::class)
    fun unzipFiles(archivePath: File, saveDir: String) {
        val BUFFER_SIZE = 6 * 1024
        try {
            val f = File(saveDir)
            if (!f.isDirectory) {
                f.mkdirs()
            }
            val zin = ZipInputStream(FileInputStream(archivePath))
            try {
                var ze = zin.nextEntry
                while (ze != null) {
                    val path = saveDir + File.separator + ze.name
                    if (ze.isDirectory) {
                        val unzipFile = File(path)
                        if(!unzipFile.isDirectory) {
                            unzipFile.mkdirs()
                        }
                    } else {
                        val fout = FileOutputStream(path, false)
                        try {
                            var c = zin.read()
                            while (c != -1) {
                                fout.write(c)
                                c = zin.read()
                            }
                            zin.closeEntry()
                        } finally {
                            fout.close()
                        }
                    }
                    ze = zin.nextEntry
                }

            } finally {
                zin.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ZIP", "Unzip exception", e)
        }
    }



















    class GetFileKeyObserver: DefaultObserver<FileKeyEntity>() {
        override fun onNext(t: FileKeyEntity) {
            Log.d("API_TESTING", "GetFileKeyObserver.onNext; $t")
        }

        override fun onError(e: Throwable) {
            Log.d("API_TESTING", "GetFileKeyObserver.onError; $e")
        }

        override fun onComplete() {
            Log.d("API_TESTING", "GetFileKeyObserver.onComplete")
        }
    }

    class GetUserPublicKeyObserver: DefaultObserver<UserPublicKeyEntity>() {
        override fun onNext(t: UserPublicKeyEntity) {
            Log.d("API_TESTING", "GetUserPublicKeyObserver.onNext; $t")
        }

        override fun onError(e: Throwable) {
            Log.d("API_TESTING", "GetUserPublicKeyObserver.onError; $e")
        }

        override fun onComplete() {
            Log.d("API_TESTING", "GetUserPublicKeyObserver.onComplete")
        }
    }

    class ShareFileObserver: DefaultObserver<ResponseBody>() {
        override fun onNext(t: ResponseBody) {
            Log.d("API_TESTING", "ShareFileObserver.onNext; $t")
        }

        override fun onError(e: Throwable) {
            Log.d("API_TESTING", "ShareFileObserver.onError; $e")
        }

        override fun onComplete() {
            Log.d("API_TESTING", "ShareFileObserver.onComplete")
        }
    }

    class DownloadFileObserver(var filesPath: File): DefaultObserver<Response<ResponseBody>>() { // TODO: changed return type to Response so headers can be retrieved
        override fun onNext(t: Response<ResponseBody>) {
            Log.d("API_TESTING", "DownloadFileObserver.onNext; $t")

            //val decryptedFile = File(filesPath, UUID.randomUUID().toString() + ".zip")

            Log.d("RETROFIT_TEST", t.headers()["Content-Disposition"].toString())

            val patternMatcher = Regex(".+filename=\"(.+?)\".*").toPattern().matcher(t.headers()["Content-Disposition"].toString())
            patternMatcher.find()
            //Log.d("RETROFIT_TEST", patternMatcher.group(1))
            val filename = patternMatcher.group(1)
            val decryptedFile = File(filesPath, filename + ".zip")
            // TODO: get filename from headers
            decryptedFile.outputStream().apply {
                //write(t.bytes())
                write(t.body()?.bytes())
                flush()
                close()
            }
        }

        override fun onError(e: Throwable) {
            Log.d("API_TESTING", "DownloadFileObserver.onError; $e")
        }

        override fun onComplete() {
            Log.d("API_TESTING", "DownloadFileObserver.onComplete")
        }
    }
}