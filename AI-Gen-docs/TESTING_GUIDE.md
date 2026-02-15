# 🧪 Testing Guide - Navigation & Firebase Sync Fix

**Date:** February 15, 2026  
**App Version:** Latest (post navigation & Firebase fixes)

---

## ✅ What Was Fixed

### **Fix #1: Navigation Crash (RESOLVED)**
The app was crashing on launch with:
```
java.lang.IllegalStateException: Activity com.example.afmobile.HomeActivity 
does not have a NavController set on 2131231073
```

**Fixes Applied:**
1. ✅ Changed `HomeActivity` to properly initialize `NavController` from `NavHostFragment`
2. ✅ Updated bottom navigation menu IDs to match navigation graph fragment IDs

---

### **Fix #2: Firebase Product Sync Error (RESOLVED)** 🆕
The app was failing to load products from Firebase with error:
```
Error parsing product: Could not deserialize object. 
Deserializing values to Number is not supported (found in field 'salesCount')
```

**Root Cause:**
- `FirebaseProduct` data class was using `Number` type for `salesCount`, `stockLevel`, and `price`
- Firebase Firestore cannot deserialize to the abstract `Number` class
- Firebase stores numeric values as either `Long` or `Double`

**Fix Applied:**
- ✅ Changed `FirebaseProduct` to use concrete types:
  - `price: Double` (for decimal values)
  - `stockLevel: Long` (for integer values)
  - `salesCount: Long` (for integer values)
- ✅ Updated in `/app/src/main/java/com/example/afmobile/data/Product.kt`

**Files Modified:**
- `/app/src/main/java/com/example/afmobile/data/Product.kt`

---

## 🚀 Quick Test Steps

### **Phase 1: Launch & Navigation Test**

#### **1. Launch the App**
```bash
# App should already be installed, just tap the AFMobile icon on your device
```

**Expected Result:**
- ✅ App launches without crash
- ✅ Login screen appears (MainActivity)

---

#### **2. Login**
Use any valid credentials (or test with Firebase Auth)

**Expected Result:**
- ✅ Successfully navigates to HomeActivity
- ✅ HomeFragment is displayed
- ✅ Bottom navigation bar is visible with 4 items

---

### **Phase 2: Firebase Product Sync Test** 🆕

#### **3. Verify Products Load from Firebase**

**What happens automatically:**
- App syncs products from Firebase on launch
- Products are stored in local SQLite database
- HomeFragment displays products in RecyclerView

**How to verify:**
1. **Pull down to refresh** on the Home screen
2. Products should load and display in the list
3. Each product card should show:
   - Product image
   - Product name
   - Price (formatted as ₱X.XX)
   - Category
   - Stock level

**Expected Results:**
- ✅ No error messages about "Could not deserialize"
- ✅ Products appear in the RecyclerView
- ✅ All product fields display correctly
- ✅ No crashes or Firebase parsing errors

**To check logs via Android Studio:**
```
Filter by: ProductRepository
Look for: "Successfully synced X products"
Should NOT see: "Error parsing product" or "Number is not supported"
```

---

#### **4. Test Product Search**
- Type in the search bar at the top of Home screen
- Search for product names (e.g., "Tobleron")

**Expected Results:**
- ✅ Search results filter correctly
- ✅ Products matching search term appear
- ✅ Clear search returns all products

---

#### **5. Test Category Filtering**
- Tap category chips below the search bar
- Try different categories (e.g., "WHITE", etc.)

**Expected Results:**
- ✅ Products filter by selected category
- ✅ Category chip is highlighted when selected
- ✅ Tapping "All" shows all products again

---

### **Phase 3: Bottom Navigation Test**

#### **Test Home Tab:**
- Tap "Home" icon in bottom navigation
- **Expected:** HomeFragment displays with product list, search bar, category chips

#### **Test Cart Tab:**
- Tap "Cart" icon in bottom navigation
- **Expected:** CartFragment displays (shopping cart screen)
- **Expected:** "Cart" icon is highlighted in bottom nav

#### **Test Orders Tab:**
- Tap "Orders" icon in bottom navigation
- **Expected:** OrdersFragment displays (orders screen)
- **Expected:** "Orders" icon is highlighted in bottom nav

#### **Test Profile Tab:**
- Tap "Profile" icon in bottom navigation
- **Expected:** ProfileFragment displays (user profile screen)
- **Expected:** "Profile" icon is highlighted in bottom nav

---

### **4. Test Navigation Switching**

Rapidly tap between different tabs:
- Home → Cart → Orders → Profile → Home

**Expected Results:**
- ✅ Each fragment loads without delay
- ✅ No crashes or errors
- ✅ Selected tab is always highlighted
- ✅ Fragment state is preserved when switching back

---

### **5. Test Back Button**

While on any fragment:
- Press device back button

**Expected Results:**
- ✅ App goes to home screen or exits (depending on navigation stack)
- ✅ No crash

---

## 🔍 Monitor Logcat

To see detailed logs while testing:

```bash
# Monitor all app logs
adb logcat | grep -E "com.example.afmobile|HomeActivity|HomeFragment"

# Look for successful navigation
adb logcat | grep "Navigation"

# Check for errors
adb logcat | grep -E "FATAL|ERROR"
```

---

## ✅ Success Criteria

All of the following should work:

- [ ] App launches without crash
- [ ] Login works and navigates to HomeActivity
- [ ] HomeFragment is displayed by default
- [ ] Bottom navigation is visible with 4 tabs
- [ ] Tapping "Home" shows HomeFragment
- [ ] Tapping "Cart" shows CartFragment
- [ ] Tapping "Orders" shows OrdersFragment
- [ ] Tapping "Profile" shows ProfileFragment
- [ ] Selected tab is highlighted in bottom navigation
- [ ] Can switch between tabs smoothly
- [ ] Back button works correctly
- [ ] No crashes or errors in logcat

---

## 🐛 If Issues Occur

### **App Still Crashes on Launch:**
```bash
# Check logcat for exact error
adb logcat -c  # Clear logs
# Launch app
adb logcat | grep -E "FATAL|ERROR"
```

### **Bottom Navigation Not Working:**
- Check that menu item IDs match nav_graph.xml destination IDs
- Verify all fragments have corresponding layouts

### **Wrong Fragment Displays:**
- Check `app:startDestination` in `/app/src/main/res/navigation/nav_graph.xml`
- Should be: `app:startDestination="@id/homeFragment"`

---

## 📱 Device Info

Currently testing on:
- **Device:** SM-A057F
- **Android Version:** 15
- **Installation Status:** ✅ Installed successfully

---

## 🎯 Next Features to Test

After confirming navigation works:

1. **HomeFragment Features:**
   - [ ] Product list loads from Firebase
   - [ ] Search functionality works
   - [ ] Category filtering works
   - [ ] Swipe to refresh works
   - [ ] Products sync from Firestore

2. **CartFragment Features:**
   - [ ] Shopping cart displays
   - [ ] Can add/remove items

3. **OrdersFragment Features:**
   - [ ] Order history displays
   - [ ] Order details visible

4. **ProfileFragment Features:**
   - [ ] User info displays
   - [ ] Logout functionality works
   - [ ] Settings accessible

---

## 📊 Performance Notes

**Build Times:**
- Clean build: ~32 seconds
- Incremental build: ~6 seconds

**APK Size:**
- Debug APK: Check `/app/build/outputs/apk/debug/app-debug.apk`

---

## 🔧 Troubleshooting Commands

```bash
# Reinstall app
cd /home/plantsed11/AndroidStudioProjects/AFMobile
./gradlew installDebug

# Force stop and restart
adb shell am force-stop com.example.afmobile
adb shell am start -n com.example.afmobile/.MainActivity

# Clear app data
adb shell pm clear com.example.afmobile

# Check installed version
adb shell pm list packages | grep afmobile
adb shell dumpsys package com.example.afmobile | grep version
```

---

## ✅ Summary

**Status:** Ready for Testing  
**Last Build:** February 15, 2026  
**Installation:** ✅ Successful  
**Known Issues:** None  

The navigation crash has been fixed. The app should now:
- Launch without errors
- Display bottom navigation correctly
- Allow switching between Home, Cart, Orders, and Profile screens
- Maintain proper navigation state

**Happy Testing! 🎉**
