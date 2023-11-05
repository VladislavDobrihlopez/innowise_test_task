package com.voitov.pexels_app.data.datasource.local

import android.content.SharedPreferences
import javax.inject.Inject

class CacheConfigStorageImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PersistentKeyValueStorage<Long> {
    override fun put(value: Long) {
        sharedPreferences.edit().putLong(KEY, value).apply()
    }

    override fun getValue(): Long? {
        val res = sharedPreferences.getLong(KEY, -1L)
        return if (res == -1L) null else res
    }

    companion object {
        const val KEY = "last_startup_time"
    }
}