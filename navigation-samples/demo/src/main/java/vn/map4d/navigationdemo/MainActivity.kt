package vn.map4d.navigationdemo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import vn.map4d.android.navigation.ui.MFNavigationLauncher
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.navigation.options.MFNavigationLauncherOptions
import vn.map4d.navigationdemo.databinding.ActivityMainBinding
import vn.map4d.services.MFLocationComponent
import vn.map4d.services.MFServiceCallback
import vn.map4d.services.MFServicesOptions
import vn.map4d.services.directions.*
import vn.map4d.types.MFLocationCoordinate

class MainActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {
  companion object {
    private const val SIMULATE_CHECKBOX_KEY: String = "SIMULATE_CHECKBOX_KEY"
  }
  private var map4D: Map4D? = null
  private var coordinates: MutableList<MFLocationCoordinate> = arrayListOf()
  private var markers: MutableList<MFMarker> = arrayListOf()
  private var options: MFNavigationLauncherOptions? = null

  private lateinit var preferences: SharedPreferences
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.map.getMapAsync(this)
    binding.map.onCreate(savedInstanceState)
    binding.btnClear.setOnClickListener(this)
    binding.btnStart.setOnClickListener(this)
    binding.btnAddCoordinate.setOnClickListener(this)
    preferences = getPreferences(MODE_PRIVATE)
    binding.simulateCheckBox.isChecked = preferences.getBoolean(SIMULATE_CHECKBOX_KEY, true)
  }

  override fun onStart() {
    super.onStart()
    binding.map.onStart()
  }

  override fun onStop() {
    binding.map.onStop()
    val editor = preferences.edit()
    editor.putBoolean(SIMULATE_CHECKBOX_KEY, binding.simulateCheckBox.isChecked)
    editor.commit()
    super.onStop()
  }

  override fun onDestroy() {
    binding.map.onDestroy()
    super.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    binding.map.onSaveInstanceState(outState)
  }

  override fun onMapReady(map4D: Map4D?) {
    this.map4D = map4D
  }

  private fun addCoordinate() {
    val latLng = map4D?.cameraPosition?.target
    val markerOptions = MFMarkerOptions().position(latLng!!)
    val marker = this.map4D?.addMarker(markerOptions)
    markers.add(marker!!)
    coordinates.add(latLng)
  }

  override fun onClick(view: View?) {
    when(view?.id) {
      R.id.btnClear -> {
        coordinates.clear()
        for(marker in markers) {
          marker.remove()
        }
        markers.clear()
      }
      R.id.btnStart -> {
        updateNavigation()
      }
      R.id.btnAddCoordinate -> {
        addCoordinate()
      }
    }
  }

  private fun updateNavigation() {
    val size = coordinates.size
    if (size < 1) {
      return
    }
    val wayPoints = ArrayList<MFLocationComponent>()
    val serviceOptions = MFServicesOptions.Builder(this).build()
    val direction = MFDirectionsService(serviceOptions)
    var origin: MFLocationComponent?
    var destination: MFLocationComponent?
    if (size == 1) {
      origin = MFLocationComponent(16.093547, 108.225128)
      destination = MFLocationComponent(coordinates[0].latitude, coordinates[0].longitude)
    }
    else {
      origin = MFLocationComponent(coordinates[0].latitude, coordinates[0].longitude)
      destination = MFLocationComponent(coordinates[size - 1].latitude, coordinates[size - 1].longitude)
      for (i in 1 .. size - 2) {
        wayPoints.add(MFLocationComponent(coordinates[i].latitude, coordinates[i].longitude))
      }
    }

    val language: String = when (binding.languageChooser.checkedRadioButtonId) {
      R.id.viRadio -> "vi"
      else -> "en"
    }

    val directionOptionsBuilder = MFDirectionOptions.Builder()
      .origin(origin)
      .destination(destination)
      .language(language)
      .mode(MFTravelMode.CAR)
      .restriction(MFRouteRestriction.restrictLocation(
        MFLocationComponent(16.093547, 108.225128),
        listOf(MFRouteRestriction.MFRouteType.FERRY, MFRouteRestriction.MFRouteType.TOLL)))
    if (wayPoints.size > 0) {
      directionOptionsBuilder.waypoints(wayPoints)
    }
    val routeOptions = directionOptionsBuilder.build()
    direction.fetchDirections(routeOptions, object: MFServiceCallback<Array<MFRoute>> {
      override fun onError(code: String, message: String) {
        Log.e("MainActivity", "Error when get route from Direction service ! Code : $code Message:  $message")
      }

      override fun onSuccess(data: Array<MFRoute>?) {
        if (!data.isNullOrEmpty()) {
          val simulateRoute = binding.simulateCheckBox.isChecked
          options = MFNavigationLauncherOptions.builder()
            .directionsRoute(data.toCollection(ArrayList()))
            .shouldSimulateRoute(simulateRoute)
            .directionOptions(routeOptions)
            .build()

          options?.apply {
            MFNavigationLauncher.startNavigation(this@MainActivity, options)
          }
        }
        else {
          options = null
          Log.e("MainActivity", "Can not find any route! Please check origin and destination Waypoint.")
        }
      }
    })
  }
}