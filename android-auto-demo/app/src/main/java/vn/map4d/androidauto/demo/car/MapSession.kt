package vn.map4d.androidauto.demo.car

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import vn.map4d.androidauto.map.map4dMapInstaller

class MapSession : Session() {

  private val map4dCarMap = map4dMapInstaller().install()

  override fun onCreateScreen(intent: Intent): Screen {
    val mapScreen = MapScreen(map4dCarMap)

    return if (carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      carContext.getCarService(ScreenManager::class.java)
        .push(mapScreen)
      RequestPermissionScreen(carContext)
    } else mapScreen
  }
}