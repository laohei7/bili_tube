package com.laohei.bili_sdk.module_v2.location

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class CountryModel(
    val common: List<CountryItem>,
    val others: List<CountryItem>,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class CountryItem(
    val id: Int,
    val cname: String,
    @SerialName("country_id") val countryId: String
) {
    companion object {
        val chain = CountryItem(
            id = 1, countryId = "86", cname = "中国"
        )
    }
}