package com.laohei.bili_sdk.apis

import com.laohei.bili_sdk.model_v2.common.BiliResponse
import com.laohei.bili_sdk.model_v2.location.CountryModel

interface InternationalizationApi {
    suspend fun getCountries(): BiliResponse<CountryModel>
}