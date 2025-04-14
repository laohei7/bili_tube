package com.laohei.bili_sdk.search

import android.util.Log
import com.laohei.bili_sdk.apis.SEARCH_TYPE_URL
import com.laohei.bili_sdk.apis.SEARCH_URL
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.module_v2.search.SearchResultModel
import com.laohei.bili_sdk.module_v2.search.SearchResultModel2
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class SearchRequest(
    private val client: HttpClient
) {

    companion object {
        private val TAG = SearchRequest::class.simpleName
        private const val DBG = true

        enum class SearchType(val type: String) {
            None("none"),
            All("all"), Video("video"), Bangumi("media_bangumi"), FT("media_ft")
        }
    }

    private val json = Json {
        classDiscriminator = "type"
        serializersModule = SerializersModule {
            polymorphic(SearchResultItemType::class) {
                subclass(
                    SearchResultItemType.VideoItem::class,
                    SearchResultItemType.VideoItem.serializer()
                )
                subclass(
                    SearchResultItemType.MediaBangumiItem::class,
                    SearchResultItemType.MediaBangumiItem.serializer()
                )
                subclass(
                    SearchResultItemType.MediaFTItem::class,
                    SearchResultItemType.MediaFTItem.serializer()
                )
                defaultDeserializer { _ -> SearchResultItemType.UnknownItem.serializer() }
            }
        }
    }

    suspend fun search(
        cookie: String? = null,
        type: SearchType = SearchType.All,
        keyword: String,
        page: Int = 1
    ) = withContext(Dispatchers.IO) {
        val url = when (type) {
            SearchType.All -> SEARCH_URL
            else -> SEARCH_TYPE_URL
        }
        val response = try {
            client.get(url) {
                url {
                    cookie?.apply {
                        headers.append(HttpHeaders.Cookie, this)
                    }
                    parameters.append("keyword", keyword)
                    when (type) {
                        SearchType.All -> {}
                        else -> {
                            parameters.append("search_type", type.type)
                            parameters.append("page", page.toString())
                        }
                    }
                }
                if (DBG) {
                    Log.d(TAG, "search: $url")
                }
            }
        } catch (e: Exception) {
            if (DBG) {
                Log.d(TAG, "search: ${e.message}")
            }
            null
        }
        response?.run {
            try {
                when (type) {
                    SearchType.All -> {
                        json.decodeFromString<BiliResponse<SearchResultModel>>(bodyAsText())
                    }

                    else -> {
                        json.decodeFromString<BiliResponse<SearchResultModel2>>(bodyAsText())
                    }
                }
            } catch (e: Exception) {
                if (DBG) {
                    Log.d(TAG, "search: ${e.message}")
                }
                null
            }
        }
    }

}