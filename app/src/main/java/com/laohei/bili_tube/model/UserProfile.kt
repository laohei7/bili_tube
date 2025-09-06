package com.laohei.bili_tube.model

import com.laohei.bili_sdk.model_v2.user.InfoCardModel

data class UserProfile(
    val face: String,
    val name: String,
    val sign: String,
    val follower: Long,
    val likeNum: Long,
    val attention: Long,
    val isSubscribed: Boolean,
    val level: Int,
    val official: String
) {
    companion object {
        val Empty = UserProfile(
            face = "",
            name = "",
            sign = "",
            follower = 0L,
            likeNum = 0L,
            attention = 0L,
            isSubscribed = false,
            level = 0,
            official = ""
        )
    }
}

fun InfoCardModel.toUserProfile(): UserProfile {
    return UserProfile(
        face = this.card.face,
        name = this.card.name,
        sign = this.card.sign,
        isSubscribed = this.following,
        follower = this.follower,
        likeNum = this.likeNum,
        attention = this.card.attention,
        official = this.card.official.title,
        level = this.card.levelInfo.currentLevel,
    )
}
