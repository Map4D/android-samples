package vn.map4d.automotive.navigation.demo.car

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import vn.map4d.androidauto.map.Map4dCarMap
import vn.map4d.androidauto.navigation.Map4dCarContext
import vn.map4d.androidauto.navigation.Map4dCarOptions.Customization
import vn.map4d.androidauto.navigation.map.Map4dCarMapLoader
import vn.map4d.androidauto.navigation.notification.Map4dCarNotificationOptions
import vn.map4d.androidauto.navigation.permissions.NeedsLocationPermissionsScreen
import vn.map4d.automotive.navigation.demo.car.screens.MainScreen
import vn.map4d.navigation.lifecycle.Map4dNavigationApp

class NavigationCarSession : Session() {

  private val map4dCarMapLoader = Map4dCarMapLoader()
  private val map4dCarMap = Map4dCarMap().registerObserver(map4dCarMapLoader)

  private val map4dCarContext: Map4dCarContext = Map4dCarContext(lifecycle, map4dCarMap)
    .customize(Customization(Map4dCarNotificationOptions.Builder(NavigationCarAppService::class.java).build()))

  init {

    // Attach the car lifecycle to Map4dNavigationApp.
    // You do not need to detach because it will internally detach when the lifecycle is destroyed.
    // But you will need to unregister any observer that was registered within the car lifecycle.
    Map4dNavigationApp.attach(this)

    lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        map4dCarMap.setup(carContext)
      }

      override fun onDestroy(owner: LifecycleOwner) {
        map4dCarMap.clearObservers()
      }
    })
  }

  override fun onCreateScreen(intent: Intent): Screen {
    return if (carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      && carContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      NeedsLocationPermissionsScreen(map4dCarContext.carContext)
    } else {
      MainScreen(map4dCarContext)
    }
  }
}