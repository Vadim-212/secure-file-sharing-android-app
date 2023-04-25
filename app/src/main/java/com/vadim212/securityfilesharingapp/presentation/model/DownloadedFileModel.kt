package com.vadim212.securityfilesharingapp.presentation.model

import okhttp3.Headers
import okhttp3.ResponseBody

class DownloadedFileModel(body: ResponseBody, headers: Headers) {

    var body: ResponseBody = body // TODO: change ResponseBody to ByteArray or InputStream?
        private set

    var headers: Headers = headers
        private set

}