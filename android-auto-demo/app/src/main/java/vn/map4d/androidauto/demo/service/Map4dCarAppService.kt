package vn.map4d.androidauto.demo.service

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import vn.map4d.androidauto.demo.car.MapSession

class Map4dCarAppService : CarAppService() {
  override fun createHostValidator(): HostValidator {
    return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
  }

  override fun onCreateSession(): Session {
    return MapSession()
  }
}