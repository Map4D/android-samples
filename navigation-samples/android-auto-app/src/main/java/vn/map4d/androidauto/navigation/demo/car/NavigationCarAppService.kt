package vn.map4d.androidauto.navigation.demo.car

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class NavigationCarAppService : CarAppService() {

  override fun createHostValidator(): HostValidator {
    return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
  }

  override fun onCreateSession(): Session {
    return NavigationCarSession()
  }
}