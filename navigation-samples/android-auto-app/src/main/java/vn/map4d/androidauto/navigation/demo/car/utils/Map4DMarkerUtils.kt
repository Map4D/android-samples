package vn.map4d.androidauto.navigation.demo.car.utils

import android.content.Context
import vn.map4d.map.annotations.MFBitmapDescriptorFactory
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.core.Map4D
import vn.map4d.types.MFLocationCoordinate

object Map4DMarkerUtils {
    fun addMarker(
        context: Context,
        map4D: Map4D,
        resId: Int,
        latLng: MFLocationCoordinate,
        anchorU: Float? = null,
        anchorV: Float? = null,
        isFlat: Boolean = false,
        zIndex: Float? = null,
        touchable: Boolean = true
    ): MFMarker? {
        val markerOption = MFMarkerOptions()
        markerOption.position(latLng)
        markerOption.icon(MFBitmapDescriptorFactory.fromResource(context, resId))
        markerOption.flat(isFlat)

        if (anchorU != null && anchorV != null) {
            markerOption.anchor(anchorU, anchorV)
        }

        markerOption.touchable(touchable)

        if (zIndex!= null)
            markerOption.zIndex(zIndex)

        return map4D.addMarker(markerOption)
    }
}