package com.laohei.bili_sdk.location

import android.util.Log
import com.laohei.bili_sdk.apis.URL_COUNTRY_LIST
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.location.CountryModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetCountryList(
    private val client: HttpClient
) {

    companion object {
        private val TAG = GetCountryList::class.simpleName
        private const val DBG = true
    }

    suspend fun countryList() = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_COUNTRY_LIST) {
                if (DBG) {
                    Log.d(TAG, "getCountryList: $url")
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<CountryModel>>(bodyAsText())
        }
    }
}