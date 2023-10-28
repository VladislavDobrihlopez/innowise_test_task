package com.voitov.pexels_app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PexelsApp: Application() {
    lateinit var computer: Computer
    init {
        Component().inject(this)
        Log.d("TEST_INJECTION", "$computer")
    }
}

class Computer {
    override fun toString(): String {
        return "overriden toString()"
    }
}

class Component {
    fun getComputer() = Computer()
    fun inject(app: PexelsApp) {
        app.computer = getComputer()
    }
}