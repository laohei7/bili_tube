package com.laohei.bili_sdk.user

import com.laohei.bili_sdk.apis.URL_SPI
import com.laohei.bili_sdk.apis.URL_USER_INFO_CARD
import com.laohei.bili_sdk.apis.USER_PROFILE_URL
import com.laohei.bili_sdk.apis.USER_STAT_URL
import com.laohei.bili_sdk.model.BiliUserProfile
import com.laohei.bili_sdk.model.BiliWbi
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.user.SpiModel
import com.laohei.bili_sdk.module_v2.user.UserStatModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GetUserInfo(
    private val client: HttpClient
) {

    companion object {
        private val TAG = GetUserInfo::class.simpleName
        private const val DBG = true
    }

    suspend fun getUserProfile(cookie: String? = null) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(USER_PROFILE_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            try {
                Json.decodeFromString<BiliResponse<BiliUserProfile>>(bodyAsText())
            } catch (e: Exception) {
                Json.decodeFromString<BiliResponse<BiliWbi>>(bodyAsText())
            }
        }
    }


    suspend fun getUserStat(cookie: String? = null) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(USER_STAT_URL) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<UserStatModel>>(bodyAsText())
        }
    }

    suspend fun getSpiInfo(cookie: String? = null) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_SPI) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                }
            }
        } catch (_: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<SpiModel>>(bodyAsText())
        }
    }

    suspend fun getUserInfoCard(
        cookie: String?,
        mid: Long
    ) = withContext(Dispatchers.IO) {
        val response = try {
            client.get(URL_USER_INFO_CARD) {
                cookie?.apply {
                    header(HttpHeaders.Cookie, this)
                }
                parameter("mid", mid)
            }
        } catch (e: Exception) {
            null
        }
        response?.run {
            Json.decodeFromString<BiliResponse<InfoCardModel>>(bodyAsText())
        }
    }



}