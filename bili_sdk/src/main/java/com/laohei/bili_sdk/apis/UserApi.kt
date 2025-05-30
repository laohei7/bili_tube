package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.model.BiliUserProfile
import com.laohei.bili_sdk.module_v2.common.BiliResponse
import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData
import com.laohei.bili_sdk.module_v2.user.InfoCardModel
import com.laohei.bili_sdk.module_v2.user.SpiModel
import com.laohei.bili_sdk.module_v2.user.UploadedVideoModel
import com.laohei.bili_sdk.module_v2.user.UserStatModel

enum class UserRelationAction(val id: Int) {
    FOLLOW(1), UNFOLLOW(2), QUIETLY_UNFOLLOW(4), BLOCKING(5), UNBLOCK(6), KICK_OUT(7)
}

interface UserApi {
    suspend fun postRelationModify(
        cookie: String? = null,
        mid: Long,
        act: UserRelationAction,
        csrf: String? = null
    ): BiliResponseNoData

    suspend fun getUploadedVideos(
        cookie: String?,
        vmid: Long,
        aid: Long? = null
    ): BiliResponse<UploadedVideoModel>

    suspend fun getUserProfile(cookie: String? = null): BiliResponse<BiliUserProfile>?

    suspend fun getUserStat(cookie: String? = null): BiliResponse<UserStatModel>

    suspend fun getSpiInfo(cookie: String? = null): BiliResponse<SpiModel>

    suspend fun getUserInfoCard(
        cookie: String?,
        mid: Long
    ): BiliResponse<InfoCardModel>
}