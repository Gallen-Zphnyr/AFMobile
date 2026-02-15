# 🚀 Manual Deployment Guide for Firebase Cloud Functions

Since the automated deployment requires Firebase CLI setup, here's a step-by-step manual guide.

## Option 1: Using the Deployment Script (Recommended)

I've created an automated script for you. Simply run:

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
./deploy-firebase.sh
```

This script will:
- ✅ Install Firebase CLI (if not already installed)
- ✅ Login to Firebase
- ✅ Install function dependencies
- ✅ Deploy Cloud Functions
- ✅ Deploy Firestore rules

---

## Option 2: Manual Step-by-Step Deployment

If you prefer to deploy manually, follow these steps:

### Step 1: Install Firebase CLI

Open a new terminal and run:

```bash
sudo npm install -g firebase-tools
```

Wait for installation to complete.

### Step 2: Verify Installation

```bash
firebase --version
```

You should see a version number (e.g., 13.x.x).

### Step 3: Login to Firebase

```bash
firebase login
```

This will:
1. Open your browser
2. Ask you to login with your Google account
3. Grant permissions to Firebase CLI

### Step 4: Navigate to Project

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
```

### Step 5: Set Firebase Project

```bash
firebase use anf-chocolate
```

### Step 6: Install Function Dependencies

```bash
cd functions
npm install
cd ..
```

### Step 7: Deploy Cloud Functions

```bash
firebase deploy --only functions
```

This will deploy all 5 functions:
- ✅ createUserProfile
- ✅ updateUserProfile
- ✅ getUserProfile
- ✅ deleteUserAccount
- ✅ onUserDelete

**Expected output:**
```
✔ functions[createUserProfile]: Successful create operation.
✔ functions[updateUserProfile]: Successful create operation.
✔ functions[getUserProfile]: Successful create operation.
✔ functions[deleteUserAccount]: Successful create operation.
✔ functions[onUserDelete]: Successful create operation.

✔ Deploy complete!
```

### Step 8: Deploy Firestore Rules

```bash
firebase deploy --only firestore:rules
```

**Expected output:**
```
✔ firestore: rules file firestore.rules compiled successfully
✔ firestore: released rules firestore.rules to cloud.firestore

✔ Deploy complete!
```

### Step 9: Verify Deployment

```bash
firebase functions:list
```

This should show all your deployed functions.

---

## Option 3: Deploy via Firebase Console (Alternative)

If CLI deployment doesn't work, you can manually copy the code:

### For Cloud Functions:

1. Go to [Firebase Console - Functions](https://console.firebase.google.com/project/anf-chocolate/functions)
2. Click **Get Started** (if first time)
3. Enable Cloud Functions for Firebase
4. Copy the code from `/home/plantsed11/AndroidStudioProjects/AFMobile/functions/index.js`
5. Paste it in the Firebase Console inline editor
6. Deploy

### For Firestore Rules:

1. Go to [Firebase Console - Firestore](https://console.firebase.google.com/project/anf-chocolate/firestore)
2. Click **Rules** tab
3. Copy rules from `/home/plantsed11/AndroidStudioProjects/AFMobile/firestore.rules`
4. Paste in the console editor
5. Click **Publish**

---

## Post-Deployment Steps

After successful deployment:

### 1. Enable Email Authentication

1. Go to [Firebase Console - Authentication](https://console.firebase.google.com/project/anf-chocolate/authentication)
2. Click **Get Started** (if first time)
3. Go to **Sign-in method** tab
4. Click **Email/Password**
5. Toggle **Enable** to ON
6. Click **Save**

### 2. Create Firestore Database

1. Go to [Firebase Console - Firestore](https://console.firebase.google.com/project/anf-chocolate/firestore)
2. Click **Create database**
3. Select **Start in production mode**
4. Choose your location (e.g., `us-central1`)
5. Click **Enable**

### 3. Verify Functions are Live

1. Go to [Firebase Console - Functions](https://console.firebase.google.com/project/anf-chocolate/functions)
2. You should see all 5 functions listed
3. Check they show **Healthy** status

---

## Testing Your Deployment

### Test via Android App

1. **Build and Install:**
   ```bash
   cd /home/plantsed11/AndroidStudioProjects/AFMobile
   ./gradlew installDebug
   ```

2. **Test Sign Up:**
   - Open the app
   - Click "Sign up here"
   - Fill in form:
     - Username: `testuser`
     - Email: `testuser@example.com`
     - Password: `password123`
   - Click SIGN UP
   - Wait for success message

3. **Verify in Firebase Console:**
   - Check [Authentication](https://console.firebase.google.com/project/anf-chocolate/authentication/users) for new user
   - Check [Firestore](https://console.firebase.google.com/project/anf-chocolate/firestore) for user profile document

4. **Test Sign In:**
   - Enter: `testuser@example.com` / `password123`
   - Click LOGIN
   - Should redirect to ProfileActivity

---

## Troubleshooting

### Issue: "Firebase CLI not found"

**Solution:**
```bash
sudo npm install -g firebase-tools
```

### Issue: "Permission denied"

**Solution:**
```bash
sudo npm install -g firebase-tools
# Or without sudo:
npm install -g firebase-tools --unsafe-perm
```

### Issue: "Not logged in to Firebase"

**Solution:**
```bash
firebase login
```

If browser doesn't open:
```bash
firebase login --no-localhost
```

### Issue: "Project not found"

**Solution:**
```bash
firebase use anf-chocolate
# Or
firebase use --add
# Then select "anf-chocolate" from the list
```

### Issue: "Function deployment failed"

**Solution:**
```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile/functions
npm install
cd ..
firebase deploy --only functions --debug
```

### Issue: "Module not found in functions"

**Solution:**
```bash
cd functions
rm -rf node_modules
npm install
cd ..
firebase deploy --only functions
```

### Issue: "Billing required"

Cloud Functions require the Blaze (pay-as-you-go) plan. The free tier is generous:
- 2M invocations/month free
- 400,000 GB-seconds free
- 200,000 CPU-seconds free

**To upgrade:**
1. Go to Firebase Console → Settings → Usage and billing
2. Click **Modify plan**
3. Select **Blaze (Pay as you go)**

---

## View Logs

To see function execution logs:

```bash
# All logs
firebase functions:log

# Specific function
firebase functions:log --only createUserProfile

# Real-time logs
firebase functions:log --tail
```

---

## Quick Commands Reference

```bash
# Login
firebase login

# Set project
firebase use anf-chocolate

# Deploy everything
firebase deploy

# Deploy only functions
firebase deploy --only functions

# Deploy only Firestore rules
firebase deploy --only firestore:rules

# List functions
firebase functions:list

# View logs
firebase functions:log

# Local testing (emulator)
firebase emulators:start
```

---

## Files Created for Deployment

All necessary files are ready:

✅ `functions/index.js` - Cloud Functions code
✅ `functions/package.json` - Dependencies
✅ `functions/node_modules/` - Installed packages
✅ `firebase.json` - Firebase configuration
✅ `firestore.rules` - Security rules
✅ `firestore.indexes.json` - Database indexes
✅ `deploy-firebase.sh` - Automated deployment script

---

## Next Steps After Deployment

1. ✅ Deploy Cloud Functions
2. ✅ Deploy Firestore Rules
3. ✅ Enable Email Authentication
4. ✅ Create Firestore Database
5. ✅ Test sign up in app
6. ✅ Test sign in in app
7. ✅ Verify data in Firestore

---

## Support

If you encounter any issues:

1. Check function logs: `firebase functions:log`
2. Check Firebase Console for errors
3. Verify all steps completed
4. Ensure billing is enabled (for Cloud Functions)

---

**Ready to deploy? Run the deployment script:**

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
./deploy-firebase.sh
```

Or follow the manual steps above! 🚀
