package com.laohei.bili_sdk.apis.impl

import com.laohei.bili_sdk.apis.InternationalizationApi
import com.laohei.bili_sdk.apis.URL_COUNTRY_LIST
import com.laohei.bili_sdk.exception.globalSDKExceptionHandle
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.location.CountryModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class InternationalizationApiImpl(
    private val client: HttpClient
) : InternationalizationApi {
    companion object {
        private val TAG = InternationalizationApiImpl::class.simpleName
        private const val DBG = true
    }

    override suspend fun getCountries(): BiliResponse<CountryModel> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get(URL_COUNTRY_LIST)
            Json.decodeFromString<BiliResponse<CountryModel>>(response.bodyAsText())
        }.fold(
            onSuccess = { it },
            onFailure = {
                if (DBG) {
                    globalSDKExceptionHandle(TAG.toString(), it)
                }
                BiliResponse(
                    code = 400,
                    message = "ERROR",
                    data = CountryModel(emptyList(), emptyList())
                )
            }
        )
    }
}