package com.example.mviimageeditor.ui.theme.model

import com.example.mviimageeditor.ui.theme.model.PhotoModel
import com.google.gson.annotations.SerializedName

data class PhotoSearchModel(
    @SerializedName("results")
    val photoModels: List<PhotoModel>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
)
