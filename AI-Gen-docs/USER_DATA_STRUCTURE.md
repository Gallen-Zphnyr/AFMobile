# 📋 User Data Structure - Simplified

**Date:** February 15, 2026  
**Update:** User profiles now only require name and email initially  
**Status:** ✅ UPDATED

---

## 🎯 User Profile Data Points

### **Required Fields (Set on Sign Up)** ✅

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `uid` | String | ✅ Yes | Firebase Auth user ID (auto-generated) |
| `username` | String | ✅ Yes | User's display name |
| `email` | String | ✅ Yes | User's email address |
| `createdAt` | Timestamp | ✅ Yes | Account creation date (auto-set) |
| `updatedAt` | Timestamp | ✅ Yes | Last update date (auto-set) |

### **Optional Fields (Can Be Set Later)** 📝

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `address` | String | ❌ No | User's address/location (null initially) |
| `phoneNumber` | String | ❌ No | User's phone number (null initially) |
| `profilePicture` | String | ❌ No | URL to profile picture (null initially) |

---

## 🔄 Sign Up Flow

### **What Happens When User Signs Up:**

```
1. User fills sign-up form
   ├── Username: "John Doe"
   ├── Email: "john@example.com"
   └── Password: "******"
   
2. Firebase Auth creates account
   └── Returns UID: "abc123xyz"
   
3. Cloud Function creates Firestore profile
   └── /users/abc123xyz
       ├── uid: "abc123xyz"
       ├── username: "John Doe"
       ├── email: "john@example.com"
       ├── createdAt: <timestamp>
       ├── updatedAt: <timestamp>
       ├── profilePicture: null    ← Can be set later
       ├── phoneNumber: null        ← Can be set later
       └── address: null            ← Can be set later
```

---

## 📊 Firestore Structure

### **On Sign Up (Minimal Data)**

```json
{
  "users": {
    "abc123xyz": {
      "uid": "abc123xyz",
      "username": "John Doe",
      "email": "john@example.com",
      "createdAt": "2026-02-15T10:30:00Z",
      "updatedAt": "2026-02-15T10:30:00Z",
      "profilePicture": null,
      "phoneNumber": null,
      "address": null
    }
  }
}
```

### **After User Updates Profile (Full Data)**

```json
{
  "users": {
    "abc123xyz": {
      "uid": "abc123xyz",
      "username": "John Doe",
      "email": "john@example.com",
      "createdAt": "2026-02-15T10:30:00Z",
      "updatedAt": "2026-02-15T12:45:00Z",
      "profilePicture": "https://storage.googleapis.com/...",
      "phoneNumber": "+1234567890",
      "address": "123 Main St, Lipa, Batangas"
    }
  }
}
```

---

## 🎨 UI Behavior

### **Profile Screen - Initial State**

```
┌────────────────────────────────┐
│  [JD]  John Doe                │
│        john@example.com         │
├────────────────────────────────┤
│  📦 Your Orders                │
│  🛒 My Cart                    │
│  📍 My Address   → "Add"       │ ← Shows user can add this
│  💳 Payment Methods            │
│  ⚙️  Settings                  │
│  ❓ Help & Support             │
│  🚪 Logout                     │
└────────────────────────────────┘
```

---

## 🛠️ Implementation Details

### **User Data Class (Kotlin)**

```kotlin
data class User(
    val uid: String = "",
    val username: String = "",              // Required
    val email: String = "",                 // Required
    val profilePicture: String? = null,     // Optional (null initially)
    val phoneNumber: String? = null,        // Optional (null initially)
    val address: String? = null,            // Optional (null initially)
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
```

### **Firebase Cloud Function (JavaScript)**

```javascript
exports.createUserProfile = functions.https.onCall(async (data, context) => {
  const { uid, username, email } = data;  // Only requires these 3
  
  const userProfile = {
    uid: uid,
    username: username,
    email: email,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    profilePicture: null,     // Can be set later
    phoneNumber: null,         // Can be set later
    address: null              // Can be set later
  };
  
  await admin.firestore()
    .collection('users')
    .doc(uid)
    .set(userProfile);
});
```

---

## ✅ Benefits of This Approach

### **1. Faster Sign Up** ⚡
- User only needs to provide name and email
- No lengthy forms to fill out
- Quick onboarding process

### **2. Progressive Profile Building** 📈
- Users can add more info when they need to
- Address required when placing order
- Phone number required for delivery notifications

### **3. Better UX** 🎨
- Less friction during sign-up
- Optional fields don't block user from using the app
- Can explore products without completing full profile

### **4. Privacy-Friendly** 🔒
- Only collect essential data initially
- User decides when to share more information
- Complies with data minimization principles

---

## 🔜 When Optional Fields Are Needed

### **Address Required For:**
- ✅ Checkout process
- ✅ Order delivery
- ✅ Shipping calculations

### **Phone Number Required For:**
- ✅ Order status notifications
- ✅ Delivery updates
- ✅ Account recovery

### **Profile Picture Optional For:**
- 📝 Personalization
- 📝 Social features (future)

---

## 🧪 Testing

### **Test Sign Up with Minimal Data**

1. Open app → Click "Sign up here"
2. Fill form:
   - Username: `testuser2`
   - Email: `testuser2@example.com`
   - Password: `password123`
   - Confirm: `password123`
3. Click **SIGN UP**

**Expected:**
- ✅ Account created successfully
- ✅ Profile created in Firestore with only name and email
- ✅ Address, phone, and profile picture are null

### **Verify in Firebase Console**

Go to: Firestore → `users` → `{uid}`

```json
{
  "uid": "...",
  "username": "testuser2",
  "email": "testuser2@example.com",
  "createdAt": "...",
  "updatedAt": "...",
  "profilePicture": null,      ✅ Null initially
  "phoneNumber": null,          ✅ Null initially
  "address": null               ✅ Null initially - Can be set later
}
```

---

## 📝 Future: Adding Address/Location

### **Option 1: Edit Profile Screen**

```kotlin
// TODO: Implement address edit
view.findViewById<RelativeLayout>(R.id.my_address_layout)?.setOnClickListener {
    // Show dialog to add/edit address
    showAddressDialog()
}

private fun showAddressDialog() {
    // Dialog with fields:
    // - Street Address
    // - City
    // - Province
    // - Postal Code
    // - Country
}
```

### **Option 2: During Checkout**

```kotlin
// If address is not set when user tries to checkout
if (userAddress.isNullOrEmpty()) {
    // Prompt user to add delivery address
    showAddressRequiredDialog()
}
```

---

## 📊 Data Migration

### **No Migration Needed!**

Existing users with address data will keep it. New users will have null values which is perfectly fine since the fields are optional.

```
Old users: address = "123 Main St"   ✅ Still works
New users: address = null             ✅ Works, can be set later
```

---

## ✅ Summary

**Initial User Profile Contains:**
- ✅ UID (auto-generated)
- ✅ Username (user provides)
- ✅ Email (user provides)
- ✅ Created/Updated timestamps (auto-set)

**Can Be Added Later:**
- 📝 Address/Location
- 📝 Phone number
- 📝 Profile picture

**Benefits:**
- ⚡ Faster sign-up
- 🎯 Less friction
- 🔒 Privacy-friendly
- 📈 Progressive profile building

---

**Implementation Date:** February 15, 2026  
**Status:** ✅ Complete  
**Ready for Testing:** ✅ Yes
