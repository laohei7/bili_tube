package com.laohei.bili_tube.utill

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

object HttpClientFactory {
    val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                header(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
                )
                header(
                    HttpHeaders.Referrer,
                    "https://www.bilibili.com"
                )
            }
        }
    }

    val coilClient by lazy { HttpClient(OkHttp) }

}