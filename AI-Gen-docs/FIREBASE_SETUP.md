# Firebase Integration Setup Guide

This document explains how to set up and deploy Firebase Authentication and Cloud Functions for the AFMobile app.

## Overview

The app uses:
- **Firebase Authentication** for user sign up and sign in
- **Firebase Cloud Functions** for backend logic
- **Firestore** for storing user profiles and data

## Prerequisites

1. **Node.js** (v18 or later) - [Download](https://nodejs.org/)
2. **Firebase CLI** - Install with: `npm install -g firebase-tools`
3. **Android Studio** with Kotlin support
4. **Firebase Project** - Already configured (anf-chocolate)

## Setup Instructions

### 1. Install Firebase CLI

```bash
npm install -g firebase-tools
```

### 2. Login to Firebase

```bash
firebase login
```

### 3. Initialize Firebase Functions

Navigate to the project directory:

```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
```

The functions directory is already created. Install dependencies:

```bash
cd functions
npm install
```

### 4. Deploy Cloud Functions

Deploy the functions to Firebase:

```bash
firebase deploy --only functions
```

This will deploy the following functions:
- `createUserProfile` - Creates user profile in Firestore on sign up
- `updateUserProfile` - Updates user profile information
- `getUserProfile` - Retrieves user profile data
- `deleteUserAccount` - Deletes user account and data
- `onUserDelete` - Cleanup trigger when user is deleted

### 5. Deploy Firestore Rules

```bash
firebase deploy --only firestore:rules
```

### 6. Enable Firebase Authentication

In the Firebase Console:
1. Go to **Authentication** → **Sign-in method**
2. Enable **Email/Password** authentication
3. Save changes

### 7. Enable Firestore Database

In the Firebase Console:
1. Go to **Firestore Database**
2. Click **Create database**
3. Choose **Start in production mode**
4. Select your preferred region
5. Click **Enable**

## Android App Setup

The Android app dependencies are already configured in `app/build.gradle.kts`:

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-functions")

// Firebase KTX extensions
implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
implementation("com.google.firebase:firebase-functions-ktx:21.1.0")
implementation("com.google.firebase:firebase-common-ktx:21.0.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
```

### Build the Android App

```bash
./gradlew build
```

## How It Works

### Sign Up Flow

1. User fills in registration form (username, email, password)
2. App validates input (matching passwords, minimum length)
3. Firebase Authentication creates the user account
4. Cloud Function `createUserProfile` is called to create user profile in Firestore
5. User is redirected to login screen

### Sign In Flow

1. User enters email and password
2. Firebase Authentication verifies credentials
3. On success, user is redirected to ProfileActivity
4. On failure, error message is shown

### User Profile Structure

User profiles are stored in Firestore under `users/{uid}`:

```json
{
  "uid": "user_id",
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "profilePicture": null,
  "phoneNumber": null,
  "address": null
}
```

## Cloud Functions

### createUserProfile

**Callable Function** - Creates a user profile in Firestore

**Parameters:**
- `uid` - User ID from Firebase Auth
- `username` - Display name
- `email` - User's email address

**Returns:**
```json
{
  "success": true,
  "message": "User profile created successfully",
  "uid": "user_id"
}
```

### updateUserProfile

**Callable Function** - Updates user profile information

**Parameters:**
- `username` (optional)
- `phoneNumber` (optional)
- `address` (optional)
- `profilePicture` (optional)

**Returns:**
```json
{
  "success": true,
  "message": "User profile updated successfully"
}
```

### getUserProfile

**Callable Function** - Retrieves user profile data

**Parameters:**
- `uid` (optional) - Defaults to authenticated user's ID

**Returns:**
```json
{
  "success": true,
  "profile": { /* user profile data */ }
}
```

### deleteUserAccount

**Callable Function** - Deletes user account and all data

**Returns:**
```json
{
  "success": true,
  "message": "User account deleted successfully"
}
```

### onUserDelete

**Trigger Function** - Automatically runs when a user is deleted from Firebase Auth to cleanup Firestore data

## Testing

### Test Sign Up

1. Run the app on an emulator or device
2. Click "Sign up here"
3. Fill in the registration form
4. Click "SIGN UP"
5. Check Firebase Console → Authentication for the new user
6. Check Firestore → users collection for the user profile

### Test Sign In

1. Enter registered email and password
2. Click "LOGIN"
3. Verify redirect to ProfileActivity

## Security Rules

Firestore security rules are configured to:
- Allow authenticated users to read all user profiles
- Allow users to only modify their own profile
- Prevent unauthorized access to user data

## Environment Setup

For development, you can use the Firebase Emulator Suite:

```bash
firebase emulators:start
```

This will start local emulators for:
- Authentication
- Firestore
- Cloud Functions

## Troubleshooting

### Build Errors

If you encounter build errors:
```bash
./gradlew clean build --refresh-dependencies
```

### Function Deployment Issues

Check function logs:
```bash
firebase functions:log
```

### Authentication Issues

1. Verify Email/Password auth is enabled in Firebase Console
2. Check that `google-services.json` is up to date
3. Ensure minimum SDK version is 24 or higher

## Next Steps

1. **Add Firestore dependency** to store and retrieve user data
2. **Implement profile editing** in ProfileActivity
3. **Add password reset** functionality
4. **Implement email verification**
5. **Add social authentication** (Google, Facebook)
6. **Add loading indicators** during async operations
7. **Implement proper error handling** with user-friendly messages

## Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Cloud Functions](https://firebase.google.com/docs/functions)
- [Firestore](https://firebase.google.com/docs/firestore)
- [Android Firebase Setup](https://firebase.google.com/docs/android/setup)

## License

This project is part of the AFMobile application.
