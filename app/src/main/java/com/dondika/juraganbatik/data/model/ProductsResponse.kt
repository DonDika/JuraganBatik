package com.dondika.juraganbatik.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductsResponse(
    val id: String,
    val sellerName: String,
    val sellerEmail: String,
    val batikName: String,
    val batikPrice: String,
    val batikAmount: String,
    val batikImg: String
): Parcelable
