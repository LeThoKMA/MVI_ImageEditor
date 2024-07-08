package com.example.mviimageeditor.model

import com.example.mviimageeditor.model.Blue
import com.google.gson.annotations.SerializedName

data class TopicSubmissions(
    @SerializedName("blue")
    val blue: com.example.mviimageeditor.model.Blue,
)
