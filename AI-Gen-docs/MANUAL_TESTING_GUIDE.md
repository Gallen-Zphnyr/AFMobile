# 📱 Manual Testing Instructions - Firebase Products Fix

**Issue Fixed:** Products not loading from Firebase  
**Date:** February 15, 2026  
**Status:** Ready for Testing

---

## ⚡ Quick Test (30 seconds)

1. **Open the AFMobile app** on your device
2. **Login** with your credentials
3. **Navigate to Home screen** (should be automatic after login)
4. **Pull down to refresh** the product list
5. **Check if products appear** in the list

**If you see products with names, prices, and images → ✅ Fix works!**

---

## 🔍 Detailed Testing Steps

### **Step 1: Verify App Installation**

The app should already be installed with the latest fix.

**Check version:**
- App was rebuilt and installed at: February 15, 2026
- Look for "BUILD SUCCESSFUL" message from gradle build

---

### **Step 2: Launch and Login**

1. Tap the **AFMobile icon** on your device
2. Enter your login credentials
3. Tap **Login**

**Expected:**
- ✅ No crash on launch
- ✅ Login succeeds
- ✅ Automatically navigates to Home screen

---

### **Step 3: Check Product List**

On the **Home screen**, you should see:

**Top Section:**
- 🔍 Search bar
- 🏷️ Category filter chips (All, WHITE, etc.)

**Main Section:**
- 📦 Product cards in a scrollable list
- Each card should show:
  - Product image
  - Product name
  - Price (formatted as ₱X.XX)
  - Category badge
  - Stock level (e.g., "Stock: 20")

**If products are empty:**
- Pull down on the screen to trigger a refresh
- Wait 2-3 seconds for Firebase sync

---

### **Step 4: Test Product Features**

#### **Search Test:**
1. Tap the **search bar** at the top
2. Type a product name (e.g., "Tobleron")
3. Products should filter as you type
4. Clear the search → all products should reappear

#### **Category Filter Test:**
1. Tap a **category chip** (e.g., "WHITE")
2. Only products in that category should show
3. Tap **"All"** → all products should reappear

#### **Product Details Test:**
1. Tap on any **product card**
2. Should open product details (if implemented)

---

### **Step 5: Check for Errors**

**If you're testing with Android Studio connected:**

1. Open **Logcat** in Android Studio
2. Filter by: `ProductRepository`
3. Look for these logs:

**✅ Success Messages (you want to see these):**
```
ProductRepository: Starting product sync from Firebase...
ProductRepository: Successfully synced 3 products
```

**❌ Error Messages (you should NOT see these):**
```
ProductRepository: Error parsing product ...
ProductRepository: Could not deserialize object
ProductRepository: Number is not supported
ProductRepository: No products found in Firebase
```

---

## 🎯 Test Results Checklist

Mark each item as you test:

### **Basic Functionality:**
- [ ] App launches without crashing
- [ ] Login works correctly
- [ ] Home screen displays
- [ ] Products load and display in list
- [ ] Product images load correctly
- [ ] Product names display
- [ ] Prices display correctly (₱X.XX format)
- [ ] Categories display
- [ ] Stock levels display

### **Product Features:**
- [ ] Search bar works
- [ ] Search filters products correctly
- [ ] Category chips work
- [ ] Category filter works correctly
- [ ] Pull-to-refresh works
- [ ] Products refresh from Firebase

### **Navigation:**
- [ ] Bottom navigation visible
- [ ] Can switch to Cart tab
- [ ] Can switch to Orders tab
- [ ] Can switch to Profile tab
- [ ] Can return to Home tab

### **No Errors:**
- [ ] No crashes during testing
- [ ] No "Error parsing product" in logs
- [ ] No "No products found" error (when products exist)
- [ ] No Firebase deserialization errors

---

## 📸 What Success Looks Like

### **Home Screen Should Show:**
```
┌─────────────────────────────┐
│ 🔍 Search products...       │
├─────────────────────────────┤
│ 🏷️ All  WHITE  Category2   │
├─────────────────────────────┤
│ ┌───────────────────────┐   │
│ │ [Product Image]       │   │
│ │ Tobleron              │   │
│ │ ₱100.00               │   │
│ │ WHITE                 │   │
│ │ Stock: 20             │   │
│ └───────────────────────┘   │
│                             │
│ ┌───────────────────────┐   │
│ │ [Product Image]       │   │
│ │ Product Name 2        │   │
│ │ ₱199.99               │   │
│ │ Category              │   │
│ │ Stock: 15             │   │
│ └───────────────────────┘   │
│                             │
│ (scroll for more...)        │
└─────────────────────────────┘
```

---

## 🐛 Troubleshooting

### **Problem: Products not showing**

**Solution 1: Refresh**
- Pull down on the Home screen to refresh
- Wait 2-3 seconds

**Solution 2: Check Firebase**
- Go to Firebase Console → Firestore Database
- Verify products exist in "products" collection
- Check that products have required fields

**Solution 3: Clear app data**
```bash
# If connected via USB debugging
adb shell pm clear com.example.afmobile
# Then relaunch the app
```

---

### **Problem: App crashes on launch**

**Solution:**
- Check that the latest build was installed
- Look at logcat for error messages
- Try reinstalling: `./gradlew installDebug`

---

### **Problem: "No products found" error**

**Possible Causes:**
1. No internet connection
2. Firebase rules blocking access
3. No products in Firebase database
4. Wrong Firebase project configured

**Check:**
- Device has internet connection
- Firebase Firestore rules allow read access
- Products exist in Firebase Console

---

## 📊 Expected Performance

**Product Sync Time:**
- Initial load: 1-3 seconds (from local SQLite)
- Refresh from Firebase: 2-4 seconds
- Background sync: Every 15 minutes

**Product Display:**
- RecyclerView should scroll smoothly
- Images should load asynchronously (placeholders first)
- Search should filter instantly
- Category filter should be immediate

---

## ✅ Test Complete Criteria

**Your test is successful if:**

1. ✅ Products display on Home screen
2. ✅ All product fields show correctly (name, price, category, stock, image)
3. ✅ Search functionality works
4. ✅ Category filtering works
5. ✅ Pull-to-refresh works
6. ✅ No error messages in logs about Firebase deserialization
7. ✅ No crashes or freezes

---

## 📝 Report Results

After testing, note:

**Test Date:** _________________  
**Device:** SM-A057F (Android 15)  
**Test Result:** ✅ PASS / ❌ FAIL

**Products Loaded:** _____ (count)  
**Search Works:** ✅ YES / ❌ NO  
**Categories Work:** ✅ YES / ❌ NO  
**Errors Seen:** _________________

---

## 🎉 Success!

If all tests pass, the Firebase deserialization issue is fully resolved!

**Next Features to Test:**
- Cart functionality
- Order placement
- User profile
- Product details screen

---

**Testing Guide Created:** February 15, 2026  
**Fix Version:** Latest  
**Ready for Testing:** ✅ YES
