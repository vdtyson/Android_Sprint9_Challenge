package com.versilistyson.assignment

import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0?.remove()
        return true
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProvderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProvderClient = FusedLocationProviderClient(this)


        val jumpToCurrentLocationBttn = map_toolbar.menu.findItem(R.id.go_to_current_location_menu_bttn)
        val addMarkerBttn = map_toolbar.menu.findItem(R.id.add_marker_menu_bttn)

        jumpToCurrentLocationBttn.setOnMenuItemClickListener(
            MenuItem.OnMenuItemClickListener {
                jumpToCurrentLocation()
                return@OnMenuItemClickListener true
            }
        )

        addMarkerBttn.setOnMenuItemClickListener(
            MenuItem.OnMenuItemClickListener {
                placeMarker(map.cameraPosition.target)
                return@OnMenuItemClickListener true
            }
        )


    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)


        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(

                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationProvderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
        map.setOnMapLongClickListener {
            placeMarker(it)
        }
    }
    private fun placeMarker(latLngForNewMarker: LatLng) {
        map.addMarker(MarkerOptions().position(latLngForNewMarker))
        playPlopSound()
    }
    private fun playPlopSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.plop)
        mediaPlayer.start()
    }




    private fun jumpToCurrentLocation() {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }
}
