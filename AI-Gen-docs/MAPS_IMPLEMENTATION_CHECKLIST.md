# ✅ Address Picker Implementation Checklist

## Phase 1: Setup (10 minutes)

### 1.1 Add Dependency
- [ ] Open `app/build.gradle.kts`
- [ ] Add: `implementation("com.google.android.libraries.places:places:3.3.0")`
- [ ] Sync Gradle

### 1.2 Get API Key
- [ ] Go to https://console.cloud.google.com/
- [ ] Select project: `anf-chocolate`
- [ ] Enable **Places API**
- [ ] Navigate to Credentials
- [ ] Create API Key
- [ ] Copy the key

### 1.3 Configure API Key
- [ ] Open `local.properties`
- [ ] Add: `MAPS_API_KEY=your_key_here`
- [ ] Open `app/build.gradle.kts`
- [ ] Add to `android.defaultConfig`:
```kotlin
manifestPlaceholders["MAPS_API_KEY"] = 
    project.findProperty("MAPS_API_KEY") as String? ?: ""
```

### 1.4 Update Manifest
- [ ] Open `AndroidManifest.xml`
- [ ] Add inside `<application>`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```
- [ ] Add permissions before `<application>`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

---

## Phase 2: Create Address Picker (15 minutes)

### 2.1 Create Activity
- [ ] Create `app/src/main/java/com/example/afmobile/AddressPickerActivity.kt`
- [ ] Copy code from `MAPS_ADDRESS_PICKER_GUIDE.md` (section: "AddressPickerActivity.kt")
- [ ] Verify imports are correct

### 2.2 Create Layout
- [ ] Create `app/src/main/res/layout/activity_address_picker.xml`
- [ ] Copy XML from `MAPS_ADDRESS_PICKER_GUIDE.md` (section: "activity_address_picker.xml")

### 2.3 Create Icon
- [ ] Create `app/src/main/res/drawable/ic_location.xml`
- [ ] Copy XML from `MAPS_ADDRESS_PICKER_GUIDE.md` (section: "Add Icons")

### 2.4 Register Activity
- [ ] Open `AndroidManifest.xml`
- [ ] Add inside `<application>`:
```xml
<activity
    android:name=".AddressPickerActivity"
    android:theme="@style/Theme.AFMobile" />
```

---

## Phase 3: Update Profile (5 minutes)

### 3.1 Update FirebaseUser Data Class
- [ ] Open `app/src/main/java/com/example/afmobile/data/FirebaseUser.kt`
- [ ] Add fields:
```kotlin
val latitude: Double? = null,
val longitude: Double? = null,
```

### 3.2 Update ProfileFragment
- [ ] Open `ProfileFragment.kt`
- [ ] Add constant:
```kotlin
companion object {
    private const val REQUEST_ADDRESS_PICKER = 1002
}
```
- [ ] Update `my_address_layout` click listener (see guide)
- [ ] Add `onActivityResult` method (see guide)
- [ ] Add `updateUserAddress` method (see guide)

---

## Phase 4: Testing (5 minutes)

### 4.1 Build and Run
- [ ] Build project: `./gradlew assembleDebug`
- [ ] Install on device
- [ ] Launch app

### 4.2 Test Flow
- [ ] Sign in to app
- [ ] Navigate to Profile tab
- [ ] Tap "My Address"
- [ ] Verify AddressPickerActivity opens
- [ ] Tap "Search Address" button
- [ ] Verify Places Autocomplete overlay opens
- [ ] Type "Manila"
- [ ] Verify suggestions appear
- [ ] Select an address
- [ ] Verify address displays in the card
- [ ] Tap "Save Address"
- [ ] Verify returns to Profile
- [ ] Check Firestore console - verify address saved

### 4.3 Error Checking
- [ ] Check logcat for errors
- [ ] Verify no API key errors
- [ ] Verify address saves to Firestore
- [ ] Test with different addresses

---

## Phase 5: Security (5 minutes)

### 5.1 Restrict API Key
- [ ] Go to Google Cloud Console
- [ ] Click on your API key
- [ ] Set Application restrictions:
  - [ ] Select "Android apps"
  - [ ] Add package name: `com.example.afmobile`
  - [ ] Add SHA-1 fingerprint:
    ```bash
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
    ```
- [ ] Set API restrictions:
  - [ ] Select "Restrict key"
  - [ ] Check "Places API"
- [ ] Save restrictions

### 5.2 Verify .gitignore
- [ ] Ensure `local.properties` is in `.gitignore`
- [ ] Verify API key is NOT in any committed files

---

## Phase 6: Polish (Optional)

### 6.1 Add Loading States
- [ ] Show progress bar while loading
- [ ] Disable buttons during operations

### 6.2 Error Handling
- [ ] Add try-catch blocks
- [ ] Show user-friendly error messages
- [ ] Handle network errors

### 6.3 UX Improvements
- [ ] Add address display in Profile
- [ ] Show "Current Location" button
- [ ] Add address validation

---

## 🎯 Expected Results

After completing this checklist:

✅ User can search for addresses  
✅ Google Places autocomplete works  
✅ Selected address saves to Firestore  
✅ Address displays in Profile  
✅ Coordinates (lat/lng) stored  
✅ API key is secure  

---

## 📊 Verification Commands

### Check if Places API is initialized:
```kotlin
Log.d("Places", "Initialized: ${Places.isInitialized()}")
```

### Check Firestore data:
```bash
# In Firebase Console
Firestore > users > {uid} > check 'address' field
```

### Check API key:
```bash
# Should NOT show your key
git grep "AIza"
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| API key error | Check manifest configuration |
| Places not opening | Verify Places.initialize() called |
| Address not saving | Check Firestore permissions |
| No suggestions | Verify API key enabled for Places |
| Build error | Sync Gradle, clean build |

---

## 📚 Reference Files

- **Complete Code:** `MAPS_ADDRESS_PICKER_GUIDE.md`
- **Quick Reference:** `MAPS_QUICK_REFERENCE.md`
- **Visual Guide:** `MAPS_VISUAL_COMPARISON.md`
- **Summary:** `MAPS_IMPLEMENTATION_SUMMARY.md`

---

## ⏱️ Time Estimate

| Phase | Time |
|-------|------|
| Setup | 10 min |
| Create Picker | 15 min |
| Update Profile | 5 min |
| Testing | 5 min |
| Security | 5 min |
| **Total** | **40 min** |

---

## ✅ Final Checklist

Before marking as complete:

- [ ] App builds without errors
- [ ] Address picker opens
- [ ] Autocomplete shows suggestions
- [ ] Address saves to Firestore
- [ ] API key is restricted
- [ ] No errors in logcat
- [ ] Tested on real device

---

**Ready to start? Begin with Phase 1!** 🚀

**Questions?** Check the detailed guide: `MAPS_ADDRESS_PICKER_GUIDE.md`
