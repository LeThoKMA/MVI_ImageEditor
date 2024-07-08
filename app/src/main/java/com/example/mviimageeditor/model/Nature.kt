package com.example.mviimageeditor.model

import com.google.gson.annotations.SerializedName

data class Nature(
    @SerializedName("approved_on")
    val approvedOn: String,
    @SerializedName("status")
    val status: String,
)
