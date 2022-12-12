package com.hello.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hello.app.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        //mMap.setOnMarkerClickListener
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED){
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location!=null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                val textAddress = getAddressDescription(location.latitude, location.longitude)
                val stringEmail = intent.extras?.getString("email")
                binding.addAddressText.setOnClickListener{
                    val intent = Intent(this, orderFlowers::class.java)
                    intent.putExtra("address",textAddress)
                    intent.putExtra("email",stringEmail)
                    startActivity(intent)
                }
                placeMarketOnMap(currentLatLong, textAddress)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }
    private fun getAddressDescription(latitude: Double, longitude: Double): String {
        var address = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var addressName = geoCoder.getFromLocation(latitude, longitude, 1)
        address = addressName.get(0).countryName+", "+ addressName.get(0).subAdminArea +", "+ addressName.get(0).postalCode +", "+ addressName.get(0).thoroughfare +", "+ addressName.get(0).featureName
        return address
    }

    private fun placeMarketOnMap(currentLatLong: LatLng, address: String) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title(address)
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker?) = false


}