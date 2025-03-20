package com.laohei.bili_tube.model

sealed class UIModel<T> {
    data class Item<T>(val item: T) : UIModel<T>()
    data class Header<T>(val header: T) : UIModel<T>()
}