package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.dynamic.DynamicModel
import com.laohei.bili_sdk.module_v2.hot.HotModel
import com.laohei.bili_sdk.module_v2.recomment.RecommendModel

interface VideoApi {
    suspend fun getRecommends(
        cookie: String? = null,
        refreshType: Int = 4,
        lastShowList: String? = null,
        feedVersion: String = "V8",
        freshIdx: Int = 1,
        freshIdx1h: Int = 1,
        brush: Int = 1,
        webLocation: Int = 1430650,
        homepageVer: Int = 1,
        fetchRow: Int = 1,
        ps: Int = 12,
        yNum: Int = 3
    ): BiliResponse<RecommendModel>

    suspend fun getHots(
        cookie: String? = null,
        pn: Int = 1,
        ps: Int = 20
    ): BiliResponse<HotModel>

    suspend fun getDynamics(
        cookie: String? = null,
        type: String = "all",
        page: Int = 1,
        offset: Long? = null
    ): BiliResponse<DynamicModel>
}