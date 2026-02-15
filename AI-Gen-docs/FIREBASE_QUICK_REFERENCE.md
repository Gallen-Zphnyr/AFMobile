# Firebase Quick Reference

## Quick Start Commands

### Deploy Everything
```bash
cd /home/plantsed11/AndroidStudioProjects/AFMobile
firebase deploy
```

### Deploy Only Functions
```bash
firebase deploy --only functions
```

### Deploy Only Firestore Rules
```bash
firebase deploy --only firestore:rules
```

### Test Functions Locally
```bash
cd functions
npm run serve
```

### View Function Logs
```bash
firebase functions:log
```

## Testing Authentication in Android

### Test User Sign Up
```kotlin
// In MainActivity - already implemented
// Fill form: username, email, password, confirm password
// Click SIGN UP button
// Check Firebase Console → Authentication
```

### Test User Sign In
```kotlin
// In MainActivity - already implemented
// Enter: email and password
// Click LOGIN button
// Should redirect to ProfileActivity
```

## Cloud Functions Endpoints

All functions are callable HTTPS functions:

### 1. createUserProfile
```javascript
// Called automatically on sign up
functions.getHttpsCallable("createUserProfile").call({
  uid: "user_id",
  username: "john_doe",
  email: "john@example.com"
})
```

### 2. updateUserProfile
```javascript
functions.getHttpsCallable("updateUserProfile").call({
  username: "new_username",
  phoneNumber: "+1234567890",
  address: "123 Main St"
})
```

### 3. getUserProfile
```javascript
functions.getHttpsCallable("getUserProfile").call({
  uid: "user_id" // optional, defaults to current user
})
```

### 4. deleteUserAccount
```javascript
functions.getHttpsCallable("deleteUserAccount").call({})
```

## Firestore Structure

```
/users/{uid}
  - uid: string
  - username: string
  - email: string
  - createdAt: timestamp
  - updatedAt: timestamp
  - profilePicture: string | null
  - phoneNumber: string | null
  - address: string | null

/orders/{orderId}  (for future use)
  - userId: string
  - items: array
  - total: number
  - status: string
  - createdAt: timestamp

/products/{productId}  (for future use)
  - name: string
  - description: string
  - price: number
  - image: string
```

## Example Usage in Kotlin

### Sign Up
```kotlin
// Already implemented in MainActivity
auth.createUserWithEmailAndPassword(email, password).await()
functions.getHttpsCallable("createUserProfile")
    .call(hashMapOf("uid" to uid, "username" to username, "email" to email))
    .await()
```

### Sign In
```kotlin
// Already implemented in MainActivity
auth.signInWithEmailAndPassword(email, password).await()
```

### Get Current User
```kotlin
val currentUser = auth.currentUser
if (currentUser != null) {
    val uid = currentUser.uid
    val email = currentUser.email
}
```

### Sign Out
```kotlin
auth.signOut()
```

### Update Profile (to be implemented)
```kotlin
val data = hashMapOf(
    "username" to "new_name",
    "phoneNumber" to "+1234567890"
)
functions.getHttpsCallable("updateUserProfile")
    .call(data)
    .await()
```

## Security Rules Summary

- **Users can read all profiles** (for social features)
- **Users can only modify their own profile**
- **Only authenticated users can access data**
- **Orders are private to each user**
- **Products are readable by everyone**

## Troubleshooting

### "Function not found" error
- Make sure functions are deployed: `firebase deploy --only functions`
- Check function name spelling

### "Permission denied" error
- User must be authenticated
- Check Firestore rules
- Verify user is accessing their own data

### "Invalid argument" error
- Check required parameters are provided
- Verify data types match function expectations

## Firebase Console URLs

- **Project Console**: https://console.firebase.google.com/project/anf-chocolate
- **Authentication**: https://console.firebase.google.com/project/anf-chocolate/authentication/users
- **Firestore**: https://console.firebase.google.com/project/anf-chocolate/firestore
- **Functions**: https://console.firebase.google.com/project/anf-chocolate/functions

## Important Notes

1. **Never commit sensitive data** (API keys should stay in `google-services.json`)
2. **Test in emulators first** before deploying to production
3. **Always validate user input** on both client and server
4. **Use security rules** to protect user data
5. **Handle errors gracefully** with user-friendly messages
6. **Add loading indicators** for better UX during async operations

## Next Features to Implement

- [ ] Password reset functionality
- [ ] Email verification
- [ ] Profile picture upload
- [ ] User profile editing in ProfileActivity
- [ ] Social authentication (Google, Facebook)
- [ ] Order management
- [ ] Product catalog
- [ ] Shopping cart with Firestore
- [ ] Push notifications
