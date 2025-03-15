package com.laohei.bili_sdk.anime

import com.laohei.bili_sdk.apis.TIMELINE_URL
import com.laohei.bili_sdk.model.BiliAnimeSchedule
import com.laohei.bili_sdk.model.BiliTimelineResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Timeline(
    private val client: HttpClient
) {
    suspend fun timelineEpisodes(
        types: Int = 1,
        before: Int? = 6,
        after: Int? = 6,
        cookie: String? = null,
    ) = withContext(Dispatchers.IO)
    {
        val response = try {
            client.get(
                TIMELINE_URL
            ) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("types", types.toString())
                    before?.let {
                        parameters.append("before", it.toString())
                    }
                    after?.let {
                        parameters.append("after", it.toString())
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
        response?.body<BiliTimelineResponse<List<BiliAnimeSchedule>>>()
    }
}