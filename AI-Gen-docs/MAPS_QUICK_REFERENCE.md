# 🗺️ Maps API Quick Reference

## Best Option for Your App: Google Places API ⭐

### Why Google Places?
- ✅ Already using Firebase (Google ecosystem)
- ✅ Simple autocomplete widget
- ✅ No map UI needed (lightweight)
- ✅ Address validation built-in
- ✅ Free tier: $200/month credit

---

## 🚀 Quick Start (5 Steps)

### 1. Add Dependency
```kotlin
// app/build.gradle.kts
implementation("com.google.android.libraries.places:places:3.3.0")
```

### 2. Get API Key
- Go to: https://console.cloud.google.com/
- Enable: **Places API**
- Create API Key
- Add to `local.properties`:
```
MAPS_API_KEY=YOUR_API_KEY_HERE
```

### 3. Configure Manifest
```xml
<application>
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="${MAPS_API_KEY}" />
</application>
```

### 4. Initialize Places
```kotlin
if (!Places.isInitialized()) {
    Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
}
```

### 5. Open Autocomplete
```kotlin
val fields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
    .setCountry("PH")
    .build(this)
startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
```

---

## 📋 API Options Comparison

| API | Best For | Complexity | Cost (per 1k) |
|-----|----------|------------|---------------|
| **Google Places Autocomplete** ⭐ | Simple address search | Low | $2.83 |
| **Google Maps SDK** | Visual map picker | Medium | $7-17 |
| **Mapbox** | Google alternative | Medium | Free tier |
| **OpenStreetMap** | Completely free | High | Free |

---

## 🎯 Recommended Implementation

### ProfileFragment Address Click:
```kotlin
view.findViewById<RelativeLayout>(R.id.my_address_layout)?.setOnClickListener {
    val intent = Intent(requireContext(), AddressPickerActivity::class.java)
    startActivityForResult(intent, REQUEST_ADDRESS_PICKER)
}
```

### Handle Result:
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_ADDRESS_PICKER && resultCode == Activity.RESULT_OK) {
        val address = data?.getStringExtra("address")
        updateFirestoreAddress(address)
    }
}
```

---

## 💾 Firestore Structure

```kotlin
users/{uid}
  ├─ address: "123 Main St, Manila, Philippines"
  ├─ latitude: 14.5995
  └─ longitude: 120.9842
```

---

## 📱 UI Flow

```
ProfileFragment
    ↓ (Click "My Address")
AddressPickerActivity
    ↓ (Click "Search Address")
Google Places Autocomplete Widget
    ↓ (Select address)
Save to Firestore
    ↓
Update Profile Display
```

---

## 🔒 Security Checklist

- [ ] API key in `local.properties` (not in git)
- [ ] Restrict API key to your package name
- [ ] Add SHA-1 certificate fingerprint
- [ ] Enable only needed APIs
- [ ] Set Android app restriction

---

## 📦 Files to Create

1. ✅ `AddressPickerActivity.kt` - Main address picker screen
2. ✅ `activity_address_picker.xml` - Layout
3. ✅ `ic_location.xml` - Location icon
4. ✅ Update `ProfileFragment.kt` - Add click handler
5. ✅ Update `FirebaseUser.kt` - Add lat/lng fields

---

## 🎨 UI Preview

```
┌─────────────────────────────────┐
│ Select Your Address             │
├─────────────────────────────────┤
│                                 │
│ Current Address:                │
│ ┌─────────────────────────────┐ │
│ │ 123 Main St, Manila, PH     │ │
│ │                             │ │
│ └─────────────────────────────┘ │
│                                 │
│ [📍 Search Address]             │
│                                 │
│ [Save Address]                  │
│                                 │
└─────────────────────────────────┘
```

When user clicks "Search Address":
```
┌─────────────────────────────────┐
│ ⌕ Search for places             │
├─────────────────────────────────┤
│ 📍 Manila City Hall             │
│ 📍 Makati Central Post Office   │
│ 📍 BGC, Taguig                  │
│ ...                             │
└─────────────────────────────────┘
```

---

## 💡 Pro Tips

1. **Restrict to Philippines**: Use `.setCountry("PH")`
2. **Debounce Input**: Save API calls
3. **Session Tokens**: Group requests for better pricing
4. **Cache Results**: Store recent addresses
5. **Offline Support**: Save last known address

---

## 🌟 Complete Guide

See: `MAPS_ADDRESS_PICKER_GUIDE.md` for full implementation with code samples.

---

**Bottom Line:** Use **Google Places Autocomplete** - it's simple, reliable, and perfect for your app! 🚀
