package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.history.HistoryModel
import com.laohei.bili_sdk.module_v2.history.ToViewModel

interface HistoryApi {
    suspend fun getToView(
        cookie: String? = null,
        pn: Int = 1,
        ps: Int = 20
    ): BiliResponse<ToViewModel>

    suspend fun addToView(
        cookie: String? = null,
        aid: Long,
        bvid: String,
        csrf: String? = null
    ): BiliResponseNoData

    suspend fun getHistories(
        cookie: String? = null,
        ps: Int = 20,
        max: Long? = null,
        business: String? = null,
        viewAt: Long? = null
    ):BiliResponse<HistoryModel>
}