# 🔧 Enable Google Maps APIs - Step by Step Guide

## 🚨 Error: Legacy API Not Enabled

If you're seeing an error about a "legacy API which is not enabled", you need to enable the required Google Maps APIs in Google Cloud Console.

---

## 🎯 Quick Fix - Enable Required APIs

### Step 1: Go to Google Cloud Console

**URL:** https://console.cloud.google.com/

**Or click these direct links:**

1. **Maps SDK for Android:**
   https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com

2. **Places API:**
   https://console.cloud.google.com/apis/library/places-backend.googleapis.com

3. **Geocoding API (optional but recommended):**
   https://console.cloud.google.com/apis/library/geocoding-backend.googleapis.com

---

## 📋 Detailed Steps

### Option 1: Enable via Direct Links (EASIEST)

1. **Click this link:** https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com
2. **Select your project:** `anf-chocolate`
3. **Click "ENABLE"** button
4. Wait for it to enable (~10 seconds)
5. ✅ Done!

**Repeat for Places API:**
1. **Click:** https://console.cloud.google.com/apis/library/places-backend.googleapis.com
2. **Select project:** `anf-chocolate`
3. **Click "ENABLE"**
4. ✅ Done!

---

### Option 2: Enable via Console Navigation

1. **Go to:** https://console.cloud.google.com/
2. **Select your project:** `anf-chocolate` (top dropdown)
3. **Click the hamburger menu** (☰) on the left
4. **Navigate to:** APIs & Services → Library
5. **Search for:** "Maps SDK for Android"
6. **Click on it**
7. **Click "ENABLE"** button
8. **Repeat** for "Places API"

---

## ✅ Required APIs to Enable

### 1. Maps SDK for Android ⭐ REQUIRED
**Why:** Displays the interactive map in your app
**Enable:** https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com

### 2. Places API ⭐ REQUIRED
**Why:** Address autocomplete and search
**Enable:** https://console.cloud.google.com/apis/library/places-backend.googleapis.com

### 3. Geocoding API (Recommended)
**Why:** Convert addresses to coordinates and vice versa
**Enable:** https://console.cloud.google.com/apis/library/geocoding-backend.googleapis.com

### 4. Geolocation API (Optional)
**Why:** Get user's current location
**Enable:** https://console.cloud.google.com/apis/library/geolocation.googleapis.com

---

## 🔍 How to Check if APIs are Enabled

### Method 1: Via Console
1. Go to: https://console.cloud.google.com/apis/dashboard
2. Select project: `anf-chocolate`
3. You should see enabled APIs listed
4. Look for:
   - ✅ Maps SDK for Android
   - ✅ Places API

### Method 2: Via Enabled APIs Page
1. Go to: https://console.cloud.google.com/apis/api/maps-android-backend.googleapis.com/overview
2. If you see "API Enabled" at the top → ✅ It's enabled
3. If you see "Enable API" button → ❌ You need to click it

---

## 🎯 Your API Key Details

**Your API Key:** `AIzaSyBpNs5g_k_1I1aGCMOjUauo1m_C1oQjMxA`

**Project:** `anf-chocolate`

**Current Status:** Key exists, but APIs may not be enabled

---

## 🔐 Important: Restrict Your API Key

After enabling the APIs, restrict your key for security:

### 1. Go to Credentials Page
https://console.cloud.google.com/apis/credentials

### 2. Click on your API key

### 3. Set Application Restrictions:
- **Type:** Android apps
- **Package name:** `com.example.afmobile`
- **SHA-1 certificate fingerprint:**

Get your SHA-1:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### 4. Set API Restrictions:
Enable ONLY these APIs:
- ✅ Maps SDK for Android
- ✅ Places API
- ✅ Geocoding API (if using)

### 5. Click SAVE

---

## 🧪 Test After Enabling

### 1. Rebuild Your App:
```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
./gradlew clean assembleDebug
./gradlew installDebug
```

### 2. Test the Feature:
1. Open app
2. Sign in
3. Go to Profile → My Address
4. **Should now open without errors!** ✅
5. Search for an address
6. See the map appear with marker
7. Save the address

---

## ❌ Common Errors and Solutions

### Error: "This API project is not authorized to use this API"
**Solution:** Enable the API in Google Cloud Console (follow steps above)

### Error: "API key not valid"
**Solution:** Check that:
1. API key is correct in AndroidManifest.xml
2. APIs are enabled in Cloud Console
3. Key restrictions allow your app package

### Error: "REQUEST_DENIED"
**Solution:** 
1. Enable billing in Google Cloud (even if using free tier)
2. Enable the specific API
3. Check API key restrictions

---

## 💡 Quick Troubleshooting

### If map still doesn't work after enabling:

1. **Wait 2-5 minutes** - API enabling can take time to propagate
2. **Clear app data:** Settings → Apps → AFMobile → Clear Data
3. **Reinstall app:** `./gradlew installDebug`
4. **Check logcat:** `adb logcat | grep -i "maps\|places"`

### Check API Status:
```bash
# This will show if APIs are working
adb logcat | grep -E "Maps|Places|Google"
```

---

## 📊 Enable APIs Summary

| API | Status | Priority | Link |
|-----|--------|----------|------|
| Maps SDK for Android | ⏳ Enable | ⭐⭐⭐ Required | [Enable](https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com) |
| Places API | ⏳ Enable | ⭐⭐⭐ Required | [Enable](https://console.cloud.google.com/apis/library/places-backend.googleapis.com) |
| Geocoding API | ⏳ Enable | ⭐⭐ Recommended | [Enable](https://console.cloud.google.com/apis/library/geocoding-backend.googleapis.com) |
| Geolocation API | ⏳ Enable | ⭐ Optional | [Enable](https://console.cloud.google.com/apis/library/geolocation.googleapis.com) |

---

## 🎯 Action Items

### Do This Now:

1. ✅ **Click:** https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com
2. ✅ **Select project:** anf-chocolate
3. ✅ **Click "ENABLE"**
4. ✅ **Click:** https://console.cloud.google.com/apis/library/places-backend.googleapis.com
5. ✅ **Click "ENABLE"** again
6. ✅ **Wait 2 minutes**
7. ✅ **Rebuild app:** `./gradlew clean assembleDebug`
8. ✅ **Install:** `./gradlew installDebug`
9. ✅ **Test:** Open Profile → My Address

---

## 🎉 Expected Result

After enabling APIs:
- ✅ Address Picker opens without crash
- ✅ Map displays correctly
- ✅ Places autocomplete works
- ✅ Markers show on map
- ✅ Can save addresses

---

## 📞 Need Help?

### Check API Status:
https://console.cloud.google.com/apis/dashboard?project=anf-chocolate

### View Enabled APIs:
https://console.cloud.google.com/apis/api/maps-android-backend.googleapis.com/overview?project=anf-chocolate

### Manage API Keys:
https://console.cloud.google.com/apis/credentials?project=anf-chocolate

---

**Quick Action:** Click the links above, enable the APIs, wait 2 minutes, rebuild your app! 🚀

**Estimated Time:** 5 minutes

**Status:** ⏳ Waiting for you to enable APIs
