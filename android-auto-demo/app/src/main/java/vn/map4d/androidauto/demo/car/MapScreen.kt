package vn.map4d.androidauto.demo.car

import android.text.Spannable
import android.text.SpannableString
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.PlaceListNavigationTemplate
import androidx.core.graphics.drawable.IconCompat
import vn.map4d.androidauto.demo.R
import vn.map4d.androidauto.map.Map4dCarMap
import vn.map4d.androidauto.map.map4dMapInstaller

class MapScreen(map4dCarMap: Map4dCarMap) : Screen(map4dCarMap.carContext) {

  private var isInPanMode: Boolean = false
  private val carCameraController = CarCameraController()
  private var mIsFavorite = false

  init {
    map4dMapInstaller(map4dCarMap)
      .onCreated(carCameraController)
      .gestureHandler(carCameraController.gestureHandler)
      .install()
  }

  override fun onGetTemplate(): Template {
//    val builder = NavigationTemplate.Builder()
//      .setBackgroundColor(CarColor.SECONDARY)
//
//    builder.setActionStrip(
//      ActionStrip.Builder()
//        .addAction(
//          Action.Builder()
//            .setIcon(
//              CarIcon.Builder(
//                IconCompat.createWithResource(
//                  carContext,
//                  android.R.drawable.ic_menu_mylocation
//                )
//              ).build()
//            )
//            .setOnClickListener {
//              carCameraController.focusOnLocationPuck()
//            }
//            .build()
//        )
//        .addAction(
//          Action.Builder()
//            .setIcon(
//              CarIcon.Builder(
//                IconCompat.createWithResource(
//                  carContext,
//                  android.R.drawable.ic_menu_search
//                )
//              ).build()
//            )
//            .setOnClickListener {
//              screenManager.push(SearchScreen(carContext))
//            }
//            .build()
//        )
//        .build()
//    )
//    // Set the map action strip with the pan and zoom buttons.
//    val panIconBuilder = CarIcon.Builder(
//      IconCompat.createWithResource(
//        carContext,
//        R.drawable.ic_pan_24
//      )
//    )
//    if (isInPanMode) {
//      panIconBuilder.setTint(CarColor.BLUE)
//    }
//    builder.setMapActionStrip(
//      ActionStrip.Builder()
//        .addAction(
//          Action.Builder(Action.PAN)
//            .setIcon(panIconBuilder.build())
//            .build()
//        )
//        .addAction(
//          Action.Builder()
//            .setIcon(
//              CarIcon.Builder(
//                IconCompat.createWithResource(
//                  carContext,
//                  R.drawable.ic_zoom_out_24
//                )
//              ).build()
//            )
//            .setOnClickListener {
//              // handle zoom out
//              carCameraController.zoomOutAction()
//            }
//            .build()
//        )
//        .addAction(
//          Action.Builder()
//            .setIcon(
//              CarIcon.Builder(
//                IconCompat.createWithResource(
//                  carContext,
//                  R.drawable.ic_zoom_in_24
//                )
//              ).build()
//            )
//            .setOnClickListener {
//              carCameraController.zoomInAction()
//            }
//            .build()
//        )
//        .build()
//    )
//
//    // When the user enters the pan mode, remind the user that they can exit the pan mode by
//    // pressing the select button again.
//    builder.setPanModeListener { isInPanMode: Boolean ->
//      if (isInPanMode) {
//        CarToast.makeText(
//          carContext,
//          "Press Select to exit the pan mode",
//          CarToast.LENGTH_LONG
//        ).show()
//      }
//      this.isInPanMode = isInPanMode
//      invalidate()
//    }
//
//    return builder.build()
    val header = Header.Builder()
      .setStartHeaderAction(Action.BACK)
      .addEndHeaderAction(
        Action.Builder()
          .setIcon(
            CarIcon.Builder(
              IconCompat.createWithResource(
                carContext,
                if (mIsFavorite) R.drawable.ic_pan_24 else R.drawable.ic_zoom_out_24
              )
            )
              .build()
          )
          .setOnClickListener {
            CarToast.makeText(
              carContext,
              if (mIsFavorite) "Favorite toast" else "Not Favorite toast",
              CarToast.LENGTH_SHORT
            )
              .show()
            mIsFavorite = !mIsFavorite
            invalidate()
          }
          .build())
      .addEndHeaderAction(
        Action.Builder()
          .setOnClickListener { finish() }
          .setIcon(
            CarIcon.Builder(
              IconCompat.createWithResource(
                carContext,
                R.drawable.ic_zoom_in_24
              )
            )
              .build()
          )
          .build())
      .setTitle("Place list demo")
      .build()

    return PlaceListNavigationTemplate.Builder()
      .setItemList(getPlaceList())
      .setHeader(header)
      .setMapActionStrip(RoutingDemoModels.getMapActionStrip(carContext))
      .setActionStrip(
        ActionStrip.Builder()
          .addAction(
            Action.Builder()
              .setTitle(
                "Title Demo"
              )
              .setOnClickListener {}
              .build())
          .build())
      .build()
  }

  fun getPlaceList(): ItemList {
    val placesRepository = PlacesRepository()
    val itemListBuilder = ItemList.Builder()
      .setNoItemsMessage("No places to show")

    placesRepository.getPlaces()
      .forEach {
        itemListBuilder.addItem(
          Row.Builder()
            .setTitle(it.name)
            // Each item in the list *must* have a DistanceSpan applied to either the title
            // or one of the its lines of text (to help drivers make decisions)
            .addText(SpannableString(" ").apply {
              setSpan(
                DistanceSpan.create(
                  Distance.create(Math.random() * 100, Distance.UNIT_KILOMETERS)
                ), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE
              )
            })
            .setOnClickListener {

            }
            // Setting Metadata is optional, but is required to automatically show the
            // item's location on the provided map
            .setMetadata(
              Metadata.Builder()
                .setPlace(
                  Place.Builder(CarLocation.create(it.latitude, it.longitude))
                    // Using the default PlaceMarker indicates that the host should
                    // decide how to style the pins it shows on the map/in the list
                    .setMarker(PlaceMarker.Builder().build())
                    .build())
                .build()
            ).build()
        )
      }
    return itemListBuilder.build()
  }
}