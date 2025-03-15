package com.laohei.bili_sdk.user

import com.laohei.bili_sdk.apis.USER_PROFILE_URL
import com.laohei.bili_sdk.apis.USER_STAT_URL
import com.laohei.bili_sdk.model.BiliUserProfile
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.user.UserStatModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UserInfo(
    private val client: HttpClient
) {

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
            Json.decodeFromString<BiliResponse<BiliUserProfile>>(bodyAsText())
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

}