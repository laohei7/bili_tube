package com.laohei.bili_tube.utill

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

object HttpClientFactory {
    val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                header(HttpHeaders.UserAgent, "bili tube")
            }
        }
    }
}