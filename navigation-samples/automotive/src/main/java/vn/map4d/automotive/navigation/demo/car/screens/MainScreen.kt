package vn.map4d.automotive.navigation.demo.car.screens

import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import vn.map4d.androidauto.map.map4dMapInstaller
import vn.map4d.androidauto.navigation.Map4dCarContext
import vn.map4d.androidauto.navigation.navigation_screen.NavigationScreen
import vn.map4d.automotive.navigation.demo.R
import vn.map4d.navigation.lifecycle.Map4dNavigationApp
import vn.map4d.navigation.options.MFNavigationViewOptions
import vn.map4d.services.MFLocationComponent
import vn.map4d.services.MFServiceCallback
import vn.map4d.services.MFServicesOptions
import vn.map4d.services.directions.MFDirectionOptions
import vn.map4d.services.directions.MFDirectionsService
import vn.map4d.services.directions.MFRoute
import vn.map4d.services.directions.MFRouteRestriction
import vn.map4d.services.directions.MFTravelMode
import vn.map4d.types.MFLocationCoordinate

class MainScreen(private val map4dCarContext: Map4dCarContext) : Screen(map4dCarContext.carContext), DefaultLifecycleObserver {

  private val mapController = MapController()
  private val selectedLocation = mutableListOf<MFLocationCoordinate>()
  private val language = "vi"
  private val shouldSimulate = false

  init {
    lifecycle.addObserver(this)
    map4dMapInstaller(map4dCarContext.map4dCarMap)
      .onResumed(mapController)
      .install()
  }
  override fun onGetTemplate(): Template {
    val actionStrip = createActionStrip()
    val mapActionStrip = createMapActionStrip()
    return NavigationTemplate.Builder()
      .setActionStrip(actionStrip)
      .setMapActionStrip(mapActionStrip)
      .build()
  }

  private fun createActionStrip() : ActionStrip {
    val actionStripBuilder = ActionStrip.Builder()
    actionStripBuilder.addAction(
        Action.Builder()
          .setTitle(carContext.getString(R.string.clear))
          .setOnClickListener {
            selectedLocation.clear()
            mapController.clearAllSelectedMarker()
          }
          .build()
      )

    actionStripBuilder.addAction(
      Action.Builder()
        .setTitle(carContext.getString(R.string.add))
        .setOnClickListener {
          mapController.map4d?.cameraPosition?.target?.let {
            selectedLocation.add(it)
            mapController.addSelectedMarker(it)
          }
        }
        .build()
    )

    actionStripBuilder.addAction(
      Action.Builder()
        .setTitle(carContext.getString(R.string.start))
        .setOnClickListener {
          startNavigation()
        }
        .build()
    )

    return actionStripBuilder.build()
  }

  private fun createMapActionStrip(): ActionStrip {
    return ActionStrip.Builder()
      .addAction(Action.PAN)
      .addAction(
        Action.Builder()
          .setIcon(
            CarIcon.Builder(
              IconCompat.createWithResource(
                carContext,
                vn.map4d.androidauto.navigation.R.drawable.icon_zoom_in_24
              )
            ).build()
          )
          .setOnClickListener {
            mapController.zoomInAction()
          }
          .build()
      )
      .addAction(
        Action.Builder()
          .setIcon(
            CarIcon.Builder(
              IconCompat.createWithResource(
                carContext,
                vn.map4d.androidauto.navigation.R.drawable.icon_zoom_out_24
              )
            ).build()
          )
          .setOnClickListener {
            mapController.zoomOutAction()
          }
          .build()
      )
      .build()
  }

  override fun onResume(owner: LifecycleOwner) {
    super.onResume(owner)
    mapController.restoreSelectedPositionMarker(selectedLocation)
  }

  override fun onDestroy(owner: LifecycleOwner) {
    super.onDestroy(owner)
    lifecycle.removeObserver(this)
  }

  private fun startNavigation() {
    if (selectedLocation.size >= 2) {
      val serviceOptions = MFServicesOptions.Builder(carContext.applicationContext).build()
      val direction = MFDirectionsService(serviceOptions)

      val origin = MFLocationComponent(selectedLocation.first().latitude, selectedLocation.first().longitude)
      val destination = MFLocationComponent(selectedLocation.last().latitude, selectedLocation.last().longitude)
      val wayPoints = if (selectedLocation.size > 2)
        selectedLocation.subList(1, selectedLocation.size - 2).map { MFLocationComponent(it.latitude, it.longitude) }
      else emptyList()

      val directionOptionsBuilder = MFDirectionOptions.Builder()
        .origin(origin)
        .destination(destination)
        .language(language)
        .mode(MFTravelMode.CAR)
        .restriction(
          MFRouteRestriction.restrictLocation(
            MFLocationComponent(16.093547, 108.225128),
            listOf(MFRouteRestriction.MFRouteType.FERRY, MFRouteRestriction.MFRouteType.TOLL))
        )

      if (wayPoints.isNotEmpty()) {
        directionOptionsBuilder.waypoints(selectedLocation.map { MFLocationComponent(it.latitude, it.longitude) })
      }

      val routeOptions = directionOptionsBuilder.build()

      direction.fetchDirections(routeOptions, object: MFServiceCallback<Array<MFRoute>> {
        override fun onError(code: String, message: String) {
          Log.e(TAG, "Error when get route from Direction service ! Code : $code Message:  $message")
        }

        override fun onSuccess(data: Array<MFRoute>?) {
          if (!data.isNullOrEmpty()) {
            val route = data.toCollection(ArrayList())
            val options = MFNavigationViewOptions.builder()
              .directionsRoute(route)
              .directionOptions(routeOptions)
              .shouldSimulateRoute(shouldSimulate)
              .build()
            Map4dNavigationApp.setup(carContext, options)
            Map4dNavigationApp.current().startNavigation(route)
            screenManager.push(NavigationScreen(map4dCarContext))
          }
          else {
            Log.e(TAG, "Can not find any route! Please check origin and destination.")
          }
        }
      })
    }
  }

  companion object {
    private const val TAG = "MainScreen"
  }
}