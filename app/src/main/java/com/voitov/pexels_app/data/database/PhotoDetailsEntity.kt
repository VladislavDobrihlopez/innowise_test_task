package com.voitov.pexels_app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity("photo_details")
data class PhotoDetailsEntity(
    @PrimaryKey
    val id: Int = 0,
    val networkUrl: String,
    val localUrl: String,
    val author: String,
    val date: LocalDate
)