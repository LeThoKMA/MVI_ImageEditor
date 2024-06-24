package com.example.mviimageeditor.ui.theme.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("pretty_slug")
    val prettySlug: String,
    @SerializedName("slug")
    val slug: String,
)
