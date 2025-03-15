package com.laohei.bili_sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliQRCode(
    val url: String,
    @SerialName("qrcode_key") val qrcodeKey: String
)

@Serializable
data class BiliQRCodeStatus(
    val url: String,
    @SerialName("refresh_token") val refreshToken: String,
    val timestamp: Long,
    val code: Int,
    val message: String
){
    companion object{
        fun networkError():BiliQRCodeStatus= BiliQRCodeStatus(
            url ="",
            refreshToken = "",
            timestamp = 0L,
            code = -1,
            message = "网络异常，稍后重试"
        )
    }
}