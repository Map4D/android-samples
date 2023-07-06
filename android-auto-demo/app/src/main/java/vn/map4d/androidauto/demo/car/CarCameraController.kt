package vn.map4d.androidauto.demo.car

import android.graphics.Point
import vn.map4d.androidauto.map.DefaultMap4dCarMapGestureHandler
import vn.map4d.androidauto.map.Map4dCarMapObserver
import vn.map4d.androidauto.map.Map4dCarMapSurface
import vn.map4d.map.camera.MFCameraPosition
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.types.MFLocationCoordinate

/**
 * Controller class to handle map camera changes.
 */
class CarCameraController : Map4dCarMapObserver {

  private var lastGpsLocation: MFLocationCoordinate = DA_NANG
  private var previousCameraState: MFCameraPosition? = null
  private var isTrackingPuck = true

  private var surface: Map4dCarMapSurface? = null

  val gestureHandler = object : DefaultMap4dCarMapGestureHandler() {
    override fun onScroll(
      map4dCarMapSurface: Map4dCarMapSurface,
      visibleCenter: Point,
      distanceX: Float,
      distanceY: Float
    ) {
      super.onScroll(map4dCarMapSurface, visibleCenter, distanceX, distanceY)
      isTrackingPuck = false
    }
  }

  override fun onMap4dCarMapReady() {
    super.onMap4dCarMapReady()
    surface?.map4D?.moveCamera(
      MFCameraUpdateFactory.newCameraPosition(
        MFCameraPosition.Builder()
          .target(lastGpsLocation)
          .tilt(previousCameraState?.tilt ?: INITIAL_TILT)
          .zoom(previousCameraState?.zoom ?: INITIAL_ZOOM)
          .build()
      )
    )
  }

  /**
   * Initialise the car camera controller with a map surface.
   */
  override fun onAttached(map4dCarMapSurface: Map4dCarMapSurface) {
    super.onAttached(map4dCarMapSurface)
    this.surface = map4dCarMapSurface
  }

  override fun onDetached(map4dCarMapSurface: Map4dCarMapSurface) {
    previousCameraState = map4dCarMapSurface.map4D?.cameraPosition
    super.onDetached(map4dCarMapSurface)
  }

  /**
   * Make camera center to location puck and track the location puck's position.
   */
  fun focusOnLocationPuck() {
    surface?.map4D?.animateCamera(
      MFCameraUpdateFactory.newCoordinate(lastGpsLocation)
    )
    isTrackingPuck = true
  }

  /**
   * Function dedicated to zoom in map action buttons.
   */
  fun zoomInAction() = scaleEaseBy(ZOOM_ACTION_DELTA)

  /**
   * Function dedicated to zoom in map action buttons.
   */
  fun zoomOutAction() = scaleEaseBy(-ZOOM_ACTION_DELTA)

  private fun scaleEaseBy(delta: Double) {
    val map4D = surface?.map4D
    val cameraPosition = map4D?.cameraPosition ?: return
    val fromZoom = cameraPosition?.zoom
    val toZoom = (fromZoom?.plus(delta))?.coerceIn(MIN_ZOOM_OUT, MAX_ZOOM_IN)
    map4D.animateCamera(MFCameraUpdateFactory.newCoordinateZoom(cameraPosition.target, toZoom ?: 17.0))
  }

  companion object {
    /**
     * Default location for the demo.
     */
    private val DA_NANG = MFLocationCoordinate(16.075820, 108.228988)

    /**
     * Default zoom for the demo.
     */
    private const val INITIAL_ZOOM = 16.0

    /**
     * Constant camera tilt for the demo.
     */
    private const val INITIAL_TILT = 60.0

    /**
     * When zooming the camera by a delta, this will prevent the camera from zooming further.
     */
    private const val MIN_ZOOM_OUT = 6.0

    /**
     * When zooming the camera by a delta, this will prevent the camera from zooming further.
     */
    private const val MAX_ZOOM_IN = 20.0

    /**
     * Simple zoom delta to associate with the zoom action buttons.
     */
    private const val ZOOM_ACTION_DELTA = 0.5
  }
}