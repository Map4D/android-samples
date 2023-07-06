package vn.map4d.androidauto.demo.car

import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.core.graphics.drawable.IconCompat
import vn.map4d.androidauto.demo.R
import vn.map4d.androidauto.map.Map4dCarMap
import vn.map4d.androidauto.map.map4dMapInstaller

class MapScreen(map4dCarMap: Map4dCarMap) : Screen(map4dCarMap.carContext) {

  private var isInPanMode: Boolean = false
  private val carCameraController = CarCameraController()

  init {
    map4dMapInstaller(map4dCarMap)
      .onCreated(carCameraController)
      .gestureHandler(carCameraController.gestureHandler)
      .install()
  }

  override fun onGetTemplate(): Template {
    val builder = NavigationTemplate.Builder()
      .setBackgroundColor(CarColor.SECONDARY)

    builder.setActionStrip(
      ActionStrip.Builder()
        .addAction(
          Action.Builder()
            .setIcon(
              CarIcon.Builder(
                IconCompat.createWithResource(
                  carContext,
                  android.R.drawable.ic_menu_mylocation
                )
              ).build()
            )
            .setOnClickListener {
              carCameraController.focusOnLocationPuck()
            }
            .build()
        )
        .addAction(
          Action.Builder()
            .setIcon(
              CarIcon.Builder(
                IconCompat.createWithResource(
                  carContext,
                  android.R.drawable.ic_menu_search
                )
              ).build()
            )
            .setOnClickListener {
              screenManager.push(SearchScreen(carContext))
            }
            .build()
        )
        .build()
    )
    // Set the map action strip with the pan and zoom buttons.
    val panIconBuilder = CarIcon.Builder(
      IconCompat.createWithResource(
        carContext,
        R.drawable.ic_pan_24
      )
    )
    if (isInPanMode) {
      panIconBuilder.setTint(CarColor.BLUE)
    }
    builder.setMapActionStrip(
      ActionStrip.Builder()
        .addAction(
          Action.Builder(Action.PAN)
            .setIcon(panIconBuilder.build())
            .build()
        )
        .addAction(
          Action.Builder()
            .setIcon(
              CarIcon.Builder(
                IconCompat.createWithResource(
                  carContext,
                  R.drawable.ic_zoom_out_24
                )
              ).build()
            )
            .setOnClickListener {
              // handle zoom out
              carCameraController.zoomOutAction()
            }
            .build()
        )
        .addAction(
          Action.Builder()
            .setIcon(
              CarIcon.Builder(
                IconCompat.createWithResource(
                  carContext,
                  R.drawable.ic_zoom_in_24
                )
              ).build()
            )
            .setOnClickListener {
              carCameraController.zoomInAction()
            }
            .build()
        )
        .build()
    )

    // When the user enters the pan mode, remind the user that they can exit the pan mode by
    // pressing the select button again.
    builder.setPanModeListener { isInPanMode: Boolean ->
      if (isInPanMode) {
        CarToast.makeText(
          carContext,
          "Press Select to exit the pan mode",
          CarToast.LENGTH_LONG
        ).show()
      }
      this.isInPanMode = isInPanMode
      invalidate()
    }

    return builder.build()
  }
}