package com.dondika.juraganbatik.data.model

data class Transaction(
    val transactionId: String,
    val orderId: String,
    val batikName: String,
    val batikPrice: String,
    val amount: String,
    val buyer: String,
    val sellerEmail: String,
    val sellerName: String,
    val status: String,
    val transactionTime: String
)
