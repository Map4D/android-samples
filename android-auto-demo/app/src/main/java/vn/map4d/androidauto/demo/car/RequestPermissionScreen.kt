package vn.map4d.androidauto.demo.car

import android.Manifest
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template

class RequestPermissionScreen(carContext: CarContext) : Screen(carContext) {
  override fun onGetTemplate(): Template {
    val permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val listener = ParkedOnlyOnClickListener.create {
      carContext.requestPermissions(permissions) { approved, rejected ->
        CarToast.makeText(
          carContext,
          "Approved: $approved, Rejected: $rejected",
          CarToast.LENGTH_LONG
        ).show()
        if (approved.isNotEmpty()) {
          finish()
        }
      }
    }
    val action = Action.Builder()
      .setTitle("Grant Permission")
      .setBackgroundColor(CarColor.GREEN)
      .setOnClickListener(listener)
      .build()
    return MessageTemplate.Builder("This app requires location permission to work")
      .addAction(action)
      .setHeaderAction(Action.BACK)
      .build()
  }
}