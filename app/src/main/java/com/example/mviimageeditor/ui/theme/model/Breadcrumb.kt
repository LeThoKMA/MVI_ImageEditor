package com.example.mviimageeditor.ui.theme.model

import com.google.gson.annotations.SerializedName

data class Breadcrumb(
    @SerializedName("index")
    val index: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
)
