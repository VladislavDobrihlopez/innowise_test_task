package com.voitov.pexels_app.data.datasource.local

interface PersistentKeyValueStorage<V> {
    fun put(value: V)
    fun getValue(): V?

    companion object {
        const val STORAGE_LOCATION = "user_info_shared_pref"
    }
}