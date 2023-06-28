package com.dondika.juraganbatik.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    val id: String = "",
    val username: String = "",
    val batikName: String = "",
    val batikPrice: String = "",
    val batikAmount: String = "",
    val batikImg: String = ""
): Parcelable
