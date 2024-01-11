package com.android.expert_project.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    var image: Int = 0,
    var title: String = "",
    var intro: String = "",
    var userName: String = "",
    var price: Int = 0,
    var address: String = "",
    var category: String = "",
    var heartCount: Int = 0,
    var chatCount: Int = 0,
    var heartOn:Boolean = false
) : Parcelable
