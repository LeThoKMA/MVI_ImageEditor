package com.example.mviimageeditor.model

import com.google.gson.annotations.SerializedName

data class PreviewPhoto(
    @SerializedName("urls")
    val urls: Urls,
)
