package com.vadim212.securefilesharingapp.domain

import okhttp3.Headers
import okhttp3.ResponseBody

class DownloadedFile(body: ResponseBody, headers: Headers) {

    var body: ResponseBody = body // TODO: change ResponseBody to ByteArray or InputStream?
        private set

    var headers: Headers = headers
        private set

}