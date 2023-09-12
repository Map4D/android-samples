package vn.map4d.map.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.map.demo.databinding.ActivityMainBinding
import vn.map4d.types.MFLocationCoordinate
import vn.map4d.map.core.MFMapView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var binding: ActivityMainBinding

  private var mapView: MFMapView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    mapView = binding.map
    mapView?.onCreate(savedInstanceState)
    mapView?.getMapAsync(this)
  }

  override fun onMapReady(map: Map4D?) {

    // Add a marker in Da Nang and move the camera
    val danang = MFLocationCoordinate(16.075884, 108.228932)
    map?.addMarker(MFMarkerOptions().position(danang).title("Marker in Da Nang"))
    map?.moveCamera(MFCameraUpdateFactory.newCoordinate(danang))
  }

  override fun onDestroy() {
    mapView?.onDestroy()
    super.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView?.onSaveInstanceState(outState)
  }

  override fun onStart() {
    super.onStart()
    mapView?.onStart()
  }

  override fun onStop() {
    mapView?.onStop()
    super.onStop()
  }
}