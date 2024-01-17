package vn.map4d.androidauto.navigation.demo.car.screens

import vn.map4d.androidauto.map.Map4dCarMapObserver
import vn.map4d.androidauto.map.Map4dCarMapSurface
import vn.map4d.androidauto.navigation.demo.R
import vn.map4d.androidauto.navigation.demo.car.utils.Map4DMarkerUtils
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.Map4D
import vn.map4d.types.MFLocationCoordinate

class MapController: Map4dCarMapObserver {

  private var surface: Map4dCarMapSurface? = null
  var map4d: Map4D? = null
  private var centerMapMarker: MFMarker? = null
  private val listSelectedLocationNeedToDraw = mutableListOf<MFLocationCoordinate>()
  private val selectedMarkers = mutableListOf<MFMarker>()

  override fun onMap4dCarMapReady() {
    super.onMap4dCarMapReady()
    this.map4d = surface?.map4D
    addListenerAndDrawCenterMapMarker()
    clearAndAddAllSelectedLocationAgain()
  }

  override fun onAttached(map4dCarMapSurface: Map4dCarMapSurface) {
    super.onAttached(map4dCarMapSurface)
    this.surface = map4dCarMapSurface
    this.map4d = map4dCarMapSurface.map4D
    addListenerAndDrawCenterMapMarker()
    clearAndAddAllSelectedLocationAgain()
  }

  override fun onDetached(map4dCarMapSurface: Map4dCarMapSurface) {
    super.onDetached(map4dCarMapSurface)
    clearAllSelectedMarker()
    clearCenterLocationMarker()
    this.map4d?.setOnCameraMoveListener(null)
  }

  fun zoomInAction() = scaleEaseBy(ZOOM_ACTION_DELTA)
  fun zoomOutAction() = scaleEaseBy(-ZOOM_ACTION_DELTA)

  private fun scaleEaseBy(delta: Double) {
    val map4D = surface?.map4D
    val cameraPosition = map4D?.cameraPosition ?: return
    val fromZoom = cameraPosition.zoom
    val toZoom = (fromZoom.plus(delta)).coerceIn(MIN_ZOOM_OUT, MAX_ZOOM_IN)
    map4D.animateCamera(MFCameraUpdateFactory.newCoordinateZoom(cameraPosition.target, toZoom))
  }

  fun addSelectedMarker(location: MFLocationCoordinate) {
    this.surface?.map4D?.let {
      val selectLocationMarker = Map4DMarkerUtils.addMarker(
        this.surface?.carContext!!,
        it,
        R.drawable.ic_selected_position_marker,
        location,
        0.5f, 1f, false, touchable = false, zIndex = 1.0f
      )
      selectLocationMarker?.let { marker ->
        selectedMarkers.add(marker)
      }
    }
  }

  fun restoreSelectedPositionMarker(locations: List<MFLocationCoordinate>) {
    listSelectedLocationNeedToDraw.clear();
    listSelectedLocationNeedToDraw.addAll(locations)
  }

  private fun clearAndAddAllSelectedLocationAgain() {
    this.surface?.map4D?.let {
      clearAllSelectedMarker()
      listSelectedLocationNeedToDraw.forEach {
        addSelectedMarker(it)
      }
      listSelectedLocationNeedToDraw.clear()
    }
  }

  fun clearAllSelectedMarker() {
    selectedMarkers.forEach { it.remove() }
    selectedMarkers.clear()
  }

  private fun addListenerAndDrawCenterMapMarker() {
    surface?.map4D?.let {
      addCenterMapMarker()
      it.setOnCameraMoveListener {
        addCenterMapMarker()
      }
    }
  }

  private fun addCenterMapMarker() {
    this.surface?.map4D?.let {
      val centerMap = it.cameraPosition.target
      if (centerMapMarker != null)
        centerMapMarker?.position = centerMap
      else {
        centerMapMarker = Map4DMarkerUtils.addMarker(
          this.surface?.carContext!!,
          it,
          R.drawable.ic_center_map_location,
          centerMap,
          0.5f, 0.5f, true, touchable = false, zIndex = 2.0f
        )
      }
    }
  }

  private fun clearCenterLocationMarker() {
    centerMapMarker?.remove()
    centerMapMarker = null
  }

  companion object {
    private const val MIN_ZOOM_OUT = 2.0
    private const val MAX_ZOOM_IN = 22.0
    private const val ZOOM_ACTION_DELTA = 1.0
  }
}