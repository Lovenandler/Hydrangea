package com.hello.app.model

import android.os.Parcel
import android.os.Parcelable
data class Items(
    var key: String? = null,
    var name: String? = null,
    var image: String? = null,
    var price: String? = null,
    var structure: String? = null)
{
constructor(): this(
    "",
    "",
    "",
    "",
    ""
)
}