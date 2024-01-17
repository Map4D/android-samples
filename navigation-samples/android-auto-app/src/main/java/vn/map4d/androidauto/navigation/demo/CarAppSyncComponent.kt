package vn.map4d.androidauto.navigation.demo

import android.util.Log
import androidx.car.app.ScreenManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import vn.map4d.androidauto.navigation.demo.app.MainActivity
import vn.map4d.androidauto.navigation.demo.car.NavigationCarSession
import vn.map4d.androidauto.navigation.navigation_screen.NavigationScreen
import vn.map4d.navigation.core.Map4dNavigation
import vn.map4d.navigation.lifecycle.Map4dNavigationApp
import vn.map4d.navigation.lifecycle.Map4dNavigationObserver

class CarAppSyncComponent private constructor() : Map4dNavigationObserver {
  private var activity: MainActivity? = null
  private var session: NavigationCarSession? = null

  fun attachActivity(activity: MainActivity) {
    Log.e(TAG, "Call attachActivity()")
    this.activity = activity
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        this@CarAppSyncComponent.activity = activity
        Map4dNavigationApp.registerObserver(appSyncComponent)
      }
      override fun onDestroy(owner: LifecycleOwner) {
        Map4dNavigationApp.unregisterObserver(appSyncComponent)
        this@CarAppSyncComponent.activity = null
      }
    })
  }

  fun setCarSession(session: NavigationCarSession) {
    Log.e(TAG, "Call setCarSession()")
    this.session = session
    session.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        this@CarAppSyncComponent.session = session
        Map4dNavigationApp.registerObserver(carSyncComponent)
      }
      override fun onDestroy(owner: LifecycleOwner) {
        Map4dNavigationApp.unregisterObserver(carSyncComponent)
        this@CarAppSyncComponent.session = null
      }
    })
  }

  override fun onAttached(mapboxNavigation: Map4dNavigation) {
    // Attached when car or app is available
    Log.i(TAG, "onAttached CarAppSyncComponent")
  }

  override fun onDetached(mapboxNavigation: Map4dNavigation) {
    // Detached when the car and app are unavailable
    Log.i(TAG, "onDetached CarAppSyncComponent")
  }

  private val appListener = object : AppViewListener {
    override fun switchToNavigation() {
      if (CarAppScreen.current.value != CarAppScreen.NAVIGATION_SCREEN) {
        val screenManager = session?.carContext?.getCarService(ScreenManager::class.java) ?: return
        session?.map4dCarContext?.let {
          screenManager.push(NavigationScreen(it))
          CarAppScreen.current.value = CarAppScreen.NAVIGATION_SCREEN
        }
      }
    }
  }

  private val appSyncComponent = object : Map4dNavigationObserver {
    var isAttached = false
      private set

    override fun onAttached(mapboxNavigation: Map4dNavigation) {
      Log.i(TAG, "onAttached app")
      val activity = activity
      checkNotNull(activity) {
        "Activity is not set for onAttached"
      }
      if (carSyncComponent.isAttached) {
        onCarAppStateUpdate(CarAppScreen.current.value)
      }
      activity.setListener(appListener)
      isAttached = true
    }

    override fun onDetached(mapboxNavigation: Map4dNavigation) {
      val activity = activity
      checkNotNull(activity) {
        "Activity is not set for onDetached"
      }
      isAttached = false
      activity.setListener(null)
      Log.i(TAG, "onDetached app")
    }
  }

  private val carSyncComponent = object : Map4dNavigationObserver {
    var isAttached = false
      private set

    override fun onAttached(mapboxNavigation: Map4dNavigation) {
      Log.i(TAG, "onAttached car")
      isAttached = true
      val activity = activity ?: return
      CarAppScreen.current.observe(activity) { carAppScreen: Int? ->
        onCarAppStateUpdate(carAppScreen)
      }
    }

    override fun onDetached(mapboxNavigation: Map4dNavigation) {
      Log.i(TAG, "onDetached car")
      isAttached = false
      val activity = activity ?: return
      CarAppScreen.current.removeObservers(activity)
    }
  }

  private fun onCarAppStateUpdate(carAppScreen: Int?) {
    val carAppScreen = carAppScreen ?: return
    val activity = activity ?: return
    when (carAppScreen) {
      CarAppScreen.NAVIGATION_SCREEN -> {
        if (!activity.isSwitchToNavigation()) {
          activity.showNavigationInfo()
        }
      }
    }
  }

  companion object {
    private const val TAG = "CarAppSyncComponent"
    fun getInstance(): CarAppSyncComponent = Map4dNavigationApp
      .getObservers(CarAppSyncComponent::class.java).firstOrNull()
      ?: CarAppSyncComponent().also { Map4dNavigationApp.registerObserver(it) }
  }
}