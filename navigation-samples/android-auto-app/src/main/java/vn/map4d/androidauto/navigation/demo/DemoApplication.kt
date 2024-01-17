package vn.map4d.androidauto.navigation.demo

import android.app.Application
import vn.map4d.navigation.lifecycle.Map4dNavigationApp
import vn.map4d.navigation.options.MFNavigationViewOptions
import vn.map4d.services.directions.MFDirectionOptions
import vn.map4d.services.directions.MFRoute

class DemoApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    /**
     * Setup to create instance Map4dNavigation first with empty route and default options for
     * synchronization Mobile app and Android Auto app
     */
    val options = MFNavigationViewOptions.builder()
      .directionOptions(MFDirectionOptions.Builder().build())
      .directionsRoute(ArrayList<MFRoute>())
      .build()
    Map4dNavigationApp.setup(applicationContext, options)
    Map4dNavigationApp.attachAllActivities(this)
  }
}