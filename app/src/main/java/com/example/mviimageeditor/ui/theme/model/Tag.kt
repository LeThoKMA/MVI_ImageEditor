package com.example.mviimageeditor.ui.theme.model

import com.example.mviimageeditor.ui.theme.model.Source
import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("source")
    val source: Source,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
)
