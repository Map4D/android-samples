package vn.map4d.androidauto.navigation.demo

import androidx.lifecycle.MutableLiveData

object CarAppScreen {
    const val MAIN_SCREEN = 1
    const val NAVIGATION_SCREEN = MAIN_SCREEN + 1

    var current: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
}