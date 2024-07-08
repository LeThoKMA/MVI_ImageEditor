package com.example.mviimageeditor.model

import com.google.gson.annotations.SerializedName

data class Ancestry(
    @SerializedName("category")
    val category: com.example.mviimageeditor.model.Category,
    @SerializedName("subcategory")
    val subcategory: com.example.mviimageeditor.model.Subcategory,
    @SerializedName("type")
    val type: com.example.mviimageeditor.model.Type,
)
