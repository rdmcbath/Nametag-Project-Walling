package com.example.nametagprojectwalling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id") val id: String,
    @SerializedName("headline") val headline: String,
    @SerializedName("image") val image: String,
    @SerializedName("upvotes") val upvotes: Int,
    @SerializedName("author") val author: String
)