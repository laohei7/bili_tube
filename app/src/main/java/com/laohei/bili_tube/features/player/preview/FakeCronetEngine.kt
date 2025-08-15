package com.laohei.bili_tube.features.player.preview

import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandlerFactory
import java.util.concurrent.Executor

internal class FakeCronetEngine : CronetEngine() {
    override fun getVersionString(): String? {
        return null
    }

    override fun shutdown() {
    }

    override fun startNetLogToFile(fileName: String?, logAll: Boolean) {
    }

    override fun stopNetLog() {
    }

    override fun getGlobalMetricsDeltas(): ByteArray? {
        return null
    }

    override fun openConnection(url: URL?): URLConnection? {
        return null
    }

    override fun createURLStreamHandlerFactory(): URLStreamHandlerFactory? {
        return null
    }

    override fun newUrlRequestBuilder(
        url: String?,
        callback: UrlRequest.Callback?,
        executor: Executor?
    ): UrlRequest.Builder? {
        return null
    }
}