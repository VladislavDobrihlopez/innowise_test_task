package com.voitov.pexels_app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity("featured_collections")
data class FeaturedCollectionsEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
)