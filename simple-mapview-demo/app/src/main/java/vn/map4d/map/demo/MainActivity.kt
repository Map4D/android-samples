package vn.map4d.map.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.MFSupportMapFragment
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.map.demo.databinding.ActivityMainBinding
import vn.map4d.types.MFLocationCoordinate

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.map.getMapAsync(this)
  }

  override fun onMapReady(map: Map4D?) {

    // Add a marker in Da Nang and move the camera
    val danang = MFLocationCoordinate(16.075884, 108.228932)
    map?.addMarker(MFMarkerOptions().position(danang).title("Marker in Da Nang"))
    map?.moveCamera(MFCameraUpdateFactory.newCoordinate(danang))
  }
}