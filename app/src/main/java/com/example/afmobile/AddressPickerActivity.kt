package com.example.afmobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AddressPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_ADDRESS = "extra_address"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LNG = "extra_lng"
        private const val AUTOCOMPLETE_REQUEST_CODE = 1001
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1003
        private const val TAG = "AddressPickerActivity"
    }

    private lateinit var tvSelectedAddress: TextView
    private lateinit var btnSelectAddress: MaterialButton
    private lateinit var btnCurrentLocation: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var btnBack: Button
    private lateinit var mapCard: MaterialCardView

    private var googleMap: GoogleMap? = null
    private var selectedAddress: String? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val geocoder by lazy { Geocoder(this, Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_picker)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBpNs5g_k_1I1aGCMOjUauo1m_C1oQjMxA")
        }

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        setupListeners()
        setupMap()

        // Load current address if provided
        selectedAddress = intent.getStringExtra(EXTRA_ADDRESS)
        selectedLat = intent.getDoubleExtra(EXTRA_LAT, 0.0).takeIf { it != 0.0 }
        selectedLng = intent.getDoubleExtra(EXTRA_LNG, 0.0).takeIf { it != 0.0 }

        tvSelectedAddress.text = selectedAddress ?: "No address selected"

        // If we have coordinates, show them on map
        if (selectedLat != null && selectedLng != null) {
            // Map will be updated in onMapReady
        }
    }

    private fun initViews() {
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress)
        btnSelectAddress = findViewById(R.id.btnSelectAddress)
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
        mapCard = findViewById(R.id.mapCard)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSelectAddress.setOnClickListener {
            openPlaceAutocomplete()
        }

        btnCurrentLocation.setOnClickListener {
            requestCurrentLocation()
        }

        btnSave.setOnClickListener {
            if (selectedAddress != null && selectedLat != null && selectedLng != null) {
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_ADDRESS, selectedAddress)
                    putExtra(EXTRA_LAT, selectedLat)
                    putExtra(EXTRA_LNG, selectedLng)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Show map card
        mapCard.visibility = android.view.View.VISIBLE

        // If we have existing coordinates, show them
        if (selectedLat != null && selectedLng != null) {
            val location = LatLng(selectedLat!!, selectedLng!!)
            googleMap?.addMarker(MarkerOptions().position(location).title("Selected Location"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        } else {
            // Default to Philippines center
            val philippines = LatLng(12.8797, 121.7740)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines, 6f))
        }

        // Enable location if permission granted
        enableMyLocation()

        // Set map click listener
        googleMap?.setOnMapClickListener { latLng ->
            onLocationSelected(latLng)
        }

        // Set marker drag listener
        googleMap?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: com.google.android.gms.maps.model.Marker) {}
            override fun onMarkerDrag(marker: com.google.android.gms.maps.model.Marker) {}
            override fun onMarkerDragEnd(marker: com.google.android.gms.maps.model.Marker) {
                onLocationSelected(marker.position)
            }
        })
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    private fun onLocationSelected(latLng: LatLng) {
        // Clear previous markers
        googleMap?.clear()

        // Add new marker (draggable)
        googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Selected Location")
                .draggable(true)
        )

        // Move camera
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Reverse geocode to get address
        selectedLat = latLng.latitude
        selectedLng = latLng.longitude

        reverseGeocode(latLng)
    }

    private fun reverseGeocode(latLng: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val address = withContext(Dispatchers.IO) {
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    addresses?.firstOrNull()?.getAddressLine(0)
                }

                if (address != null) {
                    selectedAddress = address
                    tvSelectedAddress.text = address
                } else {
                    selectedAddress = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                    tvSelectedAddress.text = selectedAddress
                }
            } catch (e: Exception) {
                Log.e(TAG, "Geocoding error: ${e.message}")
                selectedAddress = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                tvSelectedAddress.text = selectedAddress
            }
        }
    }

    private fun openPlaceAutocomplete() {
        // Specify the types of place data to return
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Start the autocomplete intent
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountry("PH") // Restrict to Philippines
            .build(this)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun requestCurrentLocation() {
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Get current location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                onLocationSelected(latLng)
                Toast.makeText(this, "Using current location", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error getting location: ${e.message}")
            Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
                requestCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        selectedAddress = place.address
                        selectedLat = place.latLng?.latitude
                        selectedLng = place.latLng?.longitude

                        tvSelectedAddress.text = selectedAddress

                        // Update map if available
                        if (selectedLat != null && selectedLng != null) {
                            val latLng = LatLng(selectedLat!!, selectedLng!!)
                            googleMap?.clear()
                            googleMap?.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title("Selected Location")
                                    .draggable(true)
                            )
                            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }

                        Toast.makeText(this, "Address selected!", Toast.LENGTH_SHORT).show()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e(TAG, "Autocomplete error: ${status.statusMessage}")
                        Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // User cancelled the operation
                }
            }
        }
    }
}
