package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.bangumi.AnimeScheduleModel
import com.laohei.bili_sdk.module_v2.bangumi.BangumiModel
import com.laohei.bili_sdk.module_v2.bangumi.RelatedBangumiModel
import com.laohei.bili_sdk.module_v2.common.BiliResponse2
import com.laohei.bili_sdk.module_v2.common.BiliResponse3

interface BangumiApi {
    suspend fun searchBangumis(
        st: Int = 1,
        order: Int = 3,
        sort: Int = 0,
        page: Int = 1,
        seasonType: Int = 1,
        pageSize: Int = 20,
        type: Int = 1,
        seasonVersion: String = "-1",
        spokenLanguageType: String = "-1",
        area: String = "-1",
        isFinish: String = "-1",
        copyright: String = "-1",
        seasonStatus: String = "-1",
        seasonMonth: String = "-1",
        styleId: String = "-1",
        cookie: String? = null,
    ): BiliResponse3<BangumiModel>

    suspend fun relatedBangumis(
        seasonId: Long,
        cookie: String? = null,
    ): BiliResponse3<RelatedBangumiModel>

    suspend fun getTimeline(
        types: Int = 1,
        before: Int? = 6,
        after: Int? = 6,
        cookie: String? = null,
    ): BiliResponse2<List<AnimeScheduleModel>>
}