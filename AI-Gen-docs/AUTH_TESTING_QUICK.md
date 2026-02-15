# 🧪 Quick Testing Guide - Auth Integration

**Feature:** Profile, Cart & Orders with Authentication  
**Date:** February 15, 2026

---

## ⚡ Quick Test (2 minutes)

### **Test 1: Unauthenticated State** 

1. Open the app (if signed in, logout first from Profile tab)
2. Tap **Cart** tab → Should see "Sign In Required" 🔒
3. Tap **Orders** tab → Should see "Sign In Required" 🔒
4. Tap **Profile** tab → Should see "Please Sign In" 🔒
5. Each screen has a **"Sign In / Sign Up"** button

**Expected:** All three tabs show sign-in prompts ✅

---

### **Test 2: Sign In Flow**

1. From any tab (Cart/Orders/Profile), tap **"Sign In / Sign Up"**
2. Should navigate to **Login screen**
3. **Sign in** with your credentials:
   - Email: `testuser1@example.com`
   - Password: `password123`
4. Should navigate to **Home screen** after successful login

**Expected:** Successful sign-in and return to app ✅

---

### **Test 3: Authenticated State**

After signing in:

1. Tap **Profile** tab
   - ✅ Should see your username and email
   - ✅ Should see profile initials (e.g., "TU" for testuser1)
   - ✅ Should see menu options (Orders, Cart, Address, etc.)

2. Tap **Cart** tab
   - ✅ Should see "My Cart" title
   - ✅ Should see "Your cart is empty" message

3. Tap **Orders** tab
   - ✅ Should see "My Order" title
   - ✅ Should see filter buttons (All Orders, Active, Status)
   - ✅ Should see "No order placed yet" message

**Expected:** All tabs show content, no sign-in prompts ✅

---

### **Test 4: Logout**

1. Go to **Profile** tab
2. Scroll down to **Logout** option (red text at bottom)
3. Tap **Logout**
4. Should see "Signed out successfully" toast message
5. Profile screen should immediately change to "Please Sign In" view

**Expected:** Logout works and UI updates immediately ✅

---

### **Test 5: Navigation After Logout**

After logging out:

1. Tap **Cart** tab → Should show sign-in prompt again
2. Tap **Orders** tab → Should show sign-in prompt again
3. Tap **Home** tab → Should still work (products visible)

**Expected:** Home works without auth, others require sign-in ✅

---

## 📋 Feature Checklist

### **Profile Fragment**
- [ ] Shows user data when signed in
- [ ] Shows sign-in prompt when not signed in
- [ ] Loads username from Firestore
- [ ] Loads email from Firestore
- [ ] Displays initials correctly
- [ ] Logout button works
- [ ] UI refreshes after logout
- [ ] Sign-in button navigates to MainActivity

### **Cart Fragment**
- [ ] Shows cart content when signed in
- [ ] Shows sign-in prompt when not signed in
- [ ] Sign-in button navigates to MainActivity

### **Orders Fragment**
- [ ] Shows orders content when signed in
- [ ] Shows sign-in prompt when not signed in
- [ ] Sign-in button navigates to MainActivity

### **Authentication Flow**
- [ ] Sign-in button works from all fragments
- [ ] Returns to Home after sign-in
- [ ] Logout refreshes all fragments
- [ ] Auth state persists on tab changes

---

## 🐛 Common Issues

### **Issue: Profile shows "Please Sign In" even after signing in**

**Solution:**
1. Make sure you signed in successfully (check for "Welcome back!" toast)
2. Navigate away and back to Profile tab
3. Check if Firestore has user profile in `/users/{uid}` collection

---

### **Issue: Sign-in button doesn't work**

**Solution:**
1. Make sure MainActivity is in the AndroidManifest.xml
2. Check that MainActivity is set as the launcher activity
3. Try restarting the app

---

### **Issue: Profile shows wrong username**

**Solution:**
1. Go to Firebase Console → Firestore
2. Check `/users/{your-uid}` document
3. Verify username field is correct
4. Logout and sign in again to refresh

---

## ✅ All Tests Passed?

If all tests pass, you've successfully implemented:

- ✅ Firebase Auth integration in Profile, Cart, and Orders
- ✅ Authentication-aware UI (different views for signed in/out users)
- ✅ Profile synced with Firestore database
- ✅ Sign-in/sign-up navigation flow
- ✅ Logout functionality with UI refresh

**Great work! The authentication integration is complete! 🎉**

---

## 🔜 What's Next?

Now that authentication is working, you can implement:

1. **Cart Management**
   - Add products to cart
   - Store cart in Firestore
   - Calculate totals

2. **Order Management**
   - Place orders
   - Order history
   - Order tracking

3. **Profile Management**
   - Edit username, phone, address
   - Upload profile picture
   - Change password

---

**Testing Date:** __________  
**Tested By:** __________  
**All Tests Pass:** ✅ YES / ❌ NO  
**Notes:** __________
