# Firebase Deployment Checklist

## 📋 Pre-Deployment Checklist

### 1. Firebase Project Setup
- [x] Firebase project created (anf-chocolate)
- [x] google-services.json configured
- [ ] Enable Authentication → Email/Password in Firebase Console
- [ ] Create Firestore Database in Firebase Console

### 2. Local Development
- [x] Node.js dependencies installed (`npm install` in functions/)
- [x] Android dependencies configured
- [x] Build successful (`./gradlew build`)

### 3. Firebase CLI
- [ ] Firebase CLI installed (`npm install -g firebase-tools`)
- [ ] Logged into Firebase (`firebase login`)

## 🚀 Deployment Steps

### Step 1: Enable Firebase Authentication

1. Go to [Firebase Console](https://console.firebase.google.com/project/anf-chocolate)
2. Click **Authentication** in left menu
3. Click **Get Started** (if first time)
4. Go to **Sign-in method** tab
5. Click **Email/Password**
6. Toggle **Enable** switch to ON
7. Click **Save**

**Status:** ⏳ Pending

---

### Step 2: Create Firestore Database

1. Go to [Firebase Console](https://console.firebase.google.com/project/anf-chocolate)
2. Click **Firestore Database** in left menu
3. Click **Create database**
4. Select **Start in production mode**
5. Choose your location (e.g., `us-central1`)
6. Click **Enable**

**Status:** ⏳ Pending

---

### Step 3: Deploy Cloud Functions

Open terminal and run:

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
firebase deploy --only functions
```

Expected output:
```
✔ functions: Finished running predeploy script.
✔ functions[createUserProfile]: Successful create operation.
✔ functions[updateUserProfile]: Successful create operation.
✔ functions[getUserProfile]: Successful create operation.
✔ functions[deleteUserAccount]: Successful create operation.
✔ functions[onUserDelete]: Successful create operation.

✔ Deploy complete!
```

**Status:** ⏳ Pending

---

### Step 4: Deploy Firestore Security Rules

```bash
firebase deploy --only firestore:rules
```

Expected output:
```
✔ firestore: rules file firestore.rules compiled successfully
✔ firestore: released rules firestore.rules to cloud.firestore

✔ Deploy complete!
```

**Status:** ⏳ Pending

---

### Step 5: Verify Deployment

1. **Check Functions**
   - Go to Firebase Console → Functions
   - Verify all 5 functions are listed
   - Check that they show "Healthy" status

2. **Check Firestore Rules**
   - Go to Firebase Console → Firestore → Rules
   - Verify rules are updated
   - Check timestamp for latest deployment

**Status:** ⏳ Pending

---

### Step 6: Build and Install Android App

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
./gradlew installDebug
```

Or run from Android Studio:
- Click **Run** (green play button)
- Select device/emulator
- Wait for installation

**Status:** ⏳ Pending

---

## 🧪 Testing Steps

### Test 1: Sign Up New User

1. Open app
2. Click "Sign up here"
3. Fill in form:
   - Username: `testuser1`
   - Email: `testuser1@example.com`
   - Password: `password123`
   - Re-enter Password: `password123`
4. Click **SIGN UP**
5. Wait for success message

**Expected Result:**
- ✅ "Account created for testuser1!" message
- ✅ Form closes
- ✅ User appears in Firebase Console → Authentication
- ✅ User profile appears in Firestore → users collection

**Actual Result:** ⏳ Not tested yet

---

### Test 2: Sign In Existing User

1. On main screen
2. Enter:
   - Email: `testuser1@example.com`
   - Password: `password123`
3. Click **LOGIN**

**Expected Result:**
- ✅ "Welcome back!" message
- ✅ Redirect to ProfileActivity

**Actual Result:** ⏳ Not tested yet

---

### Test 3: Sign In with Wrong Password

1. On main screen
2. Enter:
   - Email: `testuser1@example.com`
   - Password: `wrongpassword`
3. Click **LOGIN**

**Expected Result:**
- ✅ Error message: "Login failed: ..."
- ✅ Stay on login screen

**Actual Result:** ⏳ Not tested yet

---

### Test 4: Sign Up with Existing Email

1. Click "Sign up here"
2. Fill in form with existing email
3. Click **SIGN UP**

**Expected Result:**
- ✅ Error message: "Sign up failed: ..."

**Actual Result:** ⏳ Not tested yet

---

### Test 5: Verify Firestore Data

1. Go to Firebase Console → Firestore
2. Navigate to `users` collection
3. Click on user document

**Expected Data:**
```json
{
  "uid": "...",
  "username": "testuser1",
  "email": "testuser1@example.com",
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "profilePicture": null,
  "phoneNumber": null,
  "address": null
}
```

**Actual Result:** ⏳ Not tested yet

---

## 📊 Deployment Commands Summary

```bash
# 1. Login to Firebase
firebase login

# 2. Deploy everything
cd /home/plantsed11/AndroidStudioProjects/AFMobile
firebase deploy

# OR deploy individually:

# 3. Deploy functions only
firebase deploy --only functions

# 4. Deploy Firestore rules only
firebase deploy --only firestore:rules

# 5. View function logs
firebase functions:log

# 6. Build Android app
./gradlew build

# 7. Install on device
./gradlew installDebug
```

## 🔍 Verification Commands

```bash
# Check Firebase project
firebase projects:list

# Check current project
firebase use

# List deployed functions
firebase functions:list

# View function logs (real-time)
firebase functions:log --only createUserProfile

# Test function locally (optional)
cd functions
npm run serve
```

## ❗ Common Issues and Solutions

### Issue 1: "Firebase CLI not found"
**Solution:**
```bash
npm install -g firebase-tools
```

### Issue 2: "Not logged in"
**Solution:**
```bash
firebase login
```

### Issue 3: "Project not found"
**Solution:**
```bash
firebase use anf-chocolate
```

### Issue 4: "Function deployment failed"
**Solution:**
```bash
cd functions
npm install
cd ..
firebase deploy --only functions
```

### Issue 5: "Build failed on Android"
**Solution:**
```bash
./gradlew clean build --refresh-dependencies
```

## 📝 Post-Deployment Notes

After successful deployment:

1. **Update Status:** Mark all steps as ✅ completed
2. **Document:** Note any issues encountered
3. **Monitor:** Check Firebase Console for errors
4. **Test:** Run all test cases
5. **Backup:** Save deployment configuration

## 🎯 Success Criteria

- [x] Build successful (LOCAL)
- [ ] Functions deployed (FIREBASE)
- [ ] Rules deployed (FIREBASE)
- [ ] Authentication enabled (FIREBASE)
- [ ] Firestore created (FIREBASE)
- [ ] Sign up works (TESTING)
- [ ] Sign in works (TESTING)
- [ ] Profile created in Firestore (TESTING)
- [ ] Error handling works (TESTING)

## 📅 Deployment Log

| Date | Action | Status | Notes |
|------|--------|--------|-------|
| 2026-02-13 | Initial setup | ✅ Complete | Local build successful |
| TBD | Deploy functions | ⏳ Pending | Awaiting Firebase deployment |
| TBD | Deploy rules | ⏳ Pending | Awaiting Firebase deployment |
| TBD | Testing | ⏳ Pending | Awaiting deployment |

---

**Next Step:** Deploy to Firebase using the commands above! 🚀
