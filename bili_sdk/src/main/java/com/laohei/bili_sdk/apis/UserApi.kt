package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.module_v2.common.BiliResponseNoData

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
}