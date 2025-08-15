package com.laohei.bili_tube.features.login.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class LoginRoute {
    @Serializable
    data object SMS : LoginRoute()

    @Serializable
    data object Password : LoginRoute()

    @Serializable
    data object QRCode : LoginRoute()
}