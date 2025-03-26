package com.laohei.bili_tube.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laohei.bili_sdk.dynamic.GetWebDynamic
import com.laohei.bili_sdk.module_v2.dynamic.DynamicItem
import com.laohei.bili_tube.core.COOKIE_KEY
import com.laohei.bili_tube.dataStore
import com.laohei.bili_tube.presentation.dynamic.DynamicPaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow

class BiliDynamicRepository(
    private val context: Context,
    private val getWebDynamic: GetWebDynamic
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDynamicList(): Flow<PagingData<DynamicItem>> {
        return flow {
            val cookie = context.dataStore.data.firstOrNull()?.get(COOKIE_KEY)
            emit(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        DynamicPaging(
                            cookie = cookie,
                            webDynamic = getWebDynamic
                        )
                    }
                ).flow
            )
        }.flattenConcat()
    }
}