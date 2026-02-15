# 🗺️ Address Selection with Maps - Implementation Guide

## 🎯 Best APIs for Android Address/Location Selection

### **Recommended: Google Maps Platform APIs**
Since you're already using Firebase (Google), the Google Maps Platform integrates seamlessly.

---

## 📦 Required APIs

### 1. **Google Places API** (Recommended)
✅ **Best for:** Address autocomplete, place search, location details  
✅ **Features:**
- Address autocomplete suggestions
- Place details (formatted address, coordinates)
- Current location detection
- No map UI required (lightweight)
- Built-in address validation

### 2. **Google Maps SDK for Android**
✅ **Best for:** Visual map selection with marker  
✅ **Features:**
- Interactive map
- Drag marker to select location
- Reverse geocoding (coordinates → address)
- Current location button

### 3. **Alternative: Mapbox**
⚠️ **Alternative option** if you want to avoid Google dependencies
- Free tier available
- Good documentation
- Similar features to Google Maps

---

## 🚀 Implementation Options

### **Option 1: Places Autocomplete Widget (EASIEST)** ⭐ Recommended
Simple widget that handles everything for you.

### **Option 2: Custom Autocomplete with RecyclerView**
More customizable, shows suggestions in your own UI.

### **Option 3: Map Picker Activity**
Visual selection on a full map screen.

---

## 📝 Implementation: Places Autocomplete (Recommended)

### Step 1: Add Dependencies

**File:** `app/build.gradle.kts`

```kotlin
dependencies {
    // Existing dependencies...
    
    // Google Places API
    implementation("com.google.android.libraries.places:places:3.3.0")
    
    // Google Play Services Location (for current location)
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Optional: Maps SDK (if you want map view)
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}
```

### Step 2: Get API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable **Places API** and **Maps SDK for Android**
3. Create API Key (restrict to your package name)
4. Add to `local.properties`:

```properties
MAPS_API_KEY=YOUR_API_KEY_HERE
```

### Step 3: Configure AndroidManifest.xml

```xml
<manifest>
    <application>
        <!-- Add inside <application> tag -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
    </application>
    
    <!-- Add permissions -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
</manifest>
```

---

## 💻 Code Implementation

### Option 1: Simple Autocomplete Widget (Easiest)

**Create:** `AddressPickerActivity.kt`

```kotlin
package com.example.afmobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class AddressPickerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ADDRESS = "extra_address"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LNG = "extra_lng"
        private const val AUTOCOMPLETE_REQUEST_CODE = 1001
    }

    private lateinit var tvSelectedAddress: TextView
    private lateinit var btnSelectAddress: Button
    private lateinit var btnSave: Button
    
    private var selectedAddress: String? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_picker)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress)
        btnSelectAddress = findViewById(R.id.btnSelectAddress)
        btnSave = findViewById(R.id.btnSave)
        
        // Load current address if provided
        selectedAddress = intent.getStringExtra(EXTRA_ADDRESS)
        tvSelectedAddress.text = selectedAddress ?: "No address selected"
    }

    private fun setupListeners() {
        btnSelectAddress.setOnClickListener {
            openPlaceAutocomplete()
        }

        btnSave.setOnClickListener {
            if (selectedAddress != null) {
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
                        Toast.makeText(this, "Address selected!", Toast.LENGTH_SHORT).show()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
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
```

**Create Layout:** `res/layout/activity_address_picker.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select Your Address"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="24dp" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Current Address:"
        android:textSize="14sp"
        android:textColor="@color/subtext_color"
        android:layout_marginBottom="8dp" />

    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cardElevation="2dp"
        app:cardCornerRadius="8dp"
        android:layout_marginBottom="24dp">

        <TextView
            android:id="@+id/tvSelectedAddress"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="16dp"
            android:text="No address selected"
            android:textSize="16sp"
            android:minHeight="80dp" />

    </com.google.android.material.card.MaterialCardView>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnSelectAddress"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="📍 Search Address"
        android:textSize="16sp"
        app:icon="@drawable/ic_location"
        app:iconGravity="textStart"
        android:layout_marginBottom="16dp" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnSave"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Save Address"
        android:textSize="16sp"
        app:backgroundTint="@color/primary_color" />

</LinearLayout>
```

---

## 🔗 Update ProfileFragment.kt

```kotlin
// In ProfileFragment.kt
private fun setupAuthenticatedClickListeners(view: View) {
    // ...existing code...
    
    view.findViewById<RelativeLayout>(R.id.my_address_layout)?.setOnClickListener {
        // Open address picker
        val intent = Intent(requireContext(), AddressPickerActivity::class.java)
        
        // Pass current address if exists
        auth.currentUser?.let { user ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userDoc = withContext(Dispatchers.IO) {
                        firestore.collection("users")
                            .document(user.uid)
                            .get()
                            .await()
                    }
                    val firebaseUser = userDoc.toObject(FirebaseUser::class.java)
                    intent.putExtra(AddressPickerActivity.EXTRA_ADDRESS, firebaseUser?.address)
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading address: ${e.message}")
                }
                startActivityForResult(intent, REQUEST_ADDRESS_PICKER)
            }
        }
    }
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    
    if (requestCode == REQUEST_ADDRESS_PICKER && resultCode == Activity.RESULT_OK) {
        val address = data?.getStringExtra(AddressPickerActivity.EXTRA_ADDRESS)
        val lat = data?.getDoubleExtra(AddressPickerActivity.EXTRA_LAT, 0.0)
        val lng = data?.getDoubleExtra(AddressPickerActivity.EXTRA_LNG, 0.0)
        
        // Save to Firestore
        updateUserAddress(address, lat, lng)
    }
}

private fun updateUserAddress(address: String?, lat: Double?, lng: Double?) {
    auth.currentUser?.let { user ->
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    firestore.collection("users")
                        .document(user.uid)
                        .update(
                            mapOf(
                                "address" to address,
                                "latitude" to lat,
                                "longitude" to lng,
                                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                            )
                        )
                        .await()
                }
                Toast.makeText(requireContext(), "Address updated!", Toast.LENGTH_SHORT).show()
                loadUserProfile(user.uid) // Refresh
            } catch (e: Exception) {
                Log.e(TAG, "Error updating address: ${e.message}")
                Toast.makeText(requireContext(), "Failed to update address", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

companion object {
    private const val REQUEST_ADDRESS_PICKER = 1002
}
```

---

## 🎨 Add Icons

Create `res/drawable/ic_location.xml`:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7,-7.75 7,-13c0,-3.87 -3.13,-7 -7,-7zM12,11.5c-1.38,0 -2.5,-1.12 -2.5,-2.5s1.12,-2.5 2.5,-2.5 2.5,1.12 2.5,2.5 -1.12,2.5 -2.5,2.5z"/>
</vector>
```

---

## 📊 Update Firestore User Structure

Update `FirebaseUser.kt` data class:

```kotlin
data class FirebaseUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val latitude: Double? = null,  // Add
    val longitude: Double? = null, // Add
    val createdAt: com.google.firebase.Timestamp? = null,
    val updatedAt: com.google.firebase.Timestamp? = null
)
```

---

## 💰 Cost Considerations

### Google Places API Pricing (as of 2024):
- **Autocomplete**: $2.83 per 1,000 requests
- **Place Details**: $17 per 1,000 requests
- **Free tier**: $200/month credit = ~70,600 autocomplete requests/month

### Tips to Minimize Costs:
1. Use session tokens (groups requests together)
2. Request only needed fields
3. Implement debouncing for autocomplete
4. Cache recent addresses

---

## 🔐 Security: Restrict API Key

In Google Cloud Console:
1. Set application restrictions → Android apps
2. Add package name: `com.example.afmobile`
3. Add SHA-1 certificate fingerprint
4. Set API restrictions → Restrict to Places API

---

## 🧪 Testing Checklist

- [ ] API key configured
- [ ] Permissions granted (location)
- [ ] Autocomplete opens correctly
- [ ] Address selection works
- [ ] Address saves to Firestore
- [ ] Coordinates stored properly
- [ ] Profile shows updated address

---

## 📚 Additional Resources

- [Google Places API Docs](https://developers.google.com/maps/documentation/places/android-sdk/overview)
- [Places Autocomplete](https://developers.google.com/maps/documentation/places/android-sdk/autocomplete)
- [Get API Key](https://console.cloud.google.com/google/maps-apis/credentials)

---

## 🎯 Alternative: Mapbox (If avoiding Google)

```gradle
implementation 'com.mapbox.maps:android:10.16.0'
```

**Pros:**
- Free tier: 50,000 requests/month
- Beautiful maps
- Good documentation

**Cons:**
- Requires separate account
- Different API structure
- Less integrated with Firebase

---

**Recommendation:** Start with **Google Places Autocomplete Widget** - it's the easiest, most reliable, and integrates well with your existing Firebase setup!
