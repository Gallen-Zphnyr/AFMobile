const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

/**
 * Create User Profile
 * Called when a new user signs up to create their profile in Firestore
 */
exports.createUserProfile = functions.https.onCall(async (data, context) => {
  try {
    // Verify the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError(
        'unauthenticated',
        'User must be authenticated to create a profile.'
      );
    }

    const { uid, username, email } = data;

    // Validate input
    if (!uid || !username || !email) {
      throw new functions.https.HttpsError(
        'invalid-argument',
        'Missing required fields: uid, username, or email.'
      );
    }

    // Verify the authenticated user matches the uid
    if (context.auth.uid !== uid) {
      throw new functions.https.HttpsError(
        'permission-denied',
        'User can only create their own profile.'
      );
    }

    // Create user profile in Firestore
    const userProfile = {
      uid: uid,
      username: username,
      email: email,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      profilePicture: null,
      phoneNumber: null,
      address: null
    };

    await admin.firestore()
      .collection('users')
      .doc(uid)
      .set(userProfile);

    functions.logger.info(`User profile created for ${username} (${uid})`);

    return {
      success: true,
      message: 'User profile created successfully',
      uid: uid
    };

  } catch (error) {
    functions.logger.error('Error creating user profile:', error);

    // If it's already an HttpsError, rethrow it
    if (error instanceof functions.https.HttpsError) {
      throw error;
    }

    // Otherwise, wrap it in a generic error
    throw new functions.https.HttpsError(
      'internal',
      'An error occurred while creating the user profile.',
      error.message
    );
  }
});

/**
 * Update User Profile
 * Allows users to update their profile information
 */
exports.updateUserProfile = functions.https.onCall(async (data, context) => {
  try {
    // Verify the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError(
        'unauthenticated',
        'User must be authenticated to update profile.'
      );
    }

    const uid = context.auth.uid;
    const { username, phoneNumber, address, profilePicture } = data;

    // Build update object with only provided fields
    const updateData = {
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };

    if (username !== undefined) updateData.username = username;
    if (phoneNumber !== undefined) updateData.phoneNumber = phoneNumber;
    if (address !== undefined) updateData.address = address;
    if (profilePicture !== undefined) updateData.profilePicture = profilePicture;

    // Update user profile in Firestore
    await admin.firestore()
      .collection('users')
      .doc(uid)
      .update(updateData);

    functions.logger.info(`User profile updated for ${uid}`);

    return {
      success: true,
      message: 'User profile updated successfully'
    };

  } catch (error) {
    functions.logger.error('Error updating user profile:', error);

    if (error instanceof functions.https.HttpsError) {
      throw error;
    }

    throw new functions.https.HttpsError(
      'internal',
      'An error occurred while updating the user profile.',
      error.message
    );
  }
});

/**
 * Get User Profile
 * Retrieves user profile information
 */
exports.getUserProfile = functions.https.onCall(async (data, context) => {
  try {
    // Verify the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError(
        'unauthenticated',
        'User must be authenticated to get profile.'
      );
    }

    const uid = data.uid || context.auth.uid;

    // Get user profile from Firestore
    const userDoc = await admin.firestore()
      .collection('users')
      .doc(uid)
      .get();

    if (!userDoc.exists) {
      throw new functions.https.HttpsError(
        'not-found',
        'User profile not found.'
      );
    }

    functions.logger.info(`User profile retrieved for ${uid}`);

    return {
      success: true,
      profile: userDoc.data()
    };

  } catch (error) {
    functions.logger.error('Error getting user profile:', error);

    if (error instanceof functions.https.HttpsError) {
      throw error;
    }

    throw new functions.https.HttpsError(
      'internal',
      'An error occurred while retrieving the user profile.',
      error.message
    );
  }
});

/**
 * Delete User Account
 * Deletes user authentication and all associated data
 */
exports.deleteUserAccount = functions.https.onCall(async (data, context) => {
  try {
    // Verify the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError(
        'unauthenticated',
        'User must be authenticated to delete account.'
      );
    }

    const uid = context.auth.uid;

    // Delete user profile from Firestore
    await admin.firestore()
      .collection('users')
      .doc(uid)
      .delete();

    // Delete user authentication
    await admin.auth().deleteUser(uid);

    functions.logger.info(`User account deleted for ${uid}`);

    return {
      success: true,
      message: 'User account deleted successfully'
    };

  } catch (error) {
    functions.logger.error('Error deleting user account:', error);

    if (error instanceof functions.https.HttpsError) {
      throw error;
    }

    throw new functions.https.HttpsError(
      'internal',
      'An error occurred while deleting the user account.',
      error.message
    );
  }
});

/**
 * On User Delete Trigger
 * Cleanup function that runs when a user is deleted from Firebase Auth
 */
exports.onUserDelete = functions.auth.user().onDelete(async (user) => {
  try {
    const uid = user.uid;

    // Delete user profile from Firestore
    await admin.firestore()
      .collection('users')
      .doc(uid)
      .delete();

    functions.logger.info(`Cleaned up data for deleted user ${uid}`);

  } catch (error) {
    functions.logger.error('Error in onUserDelete cleanup:', error);
  }
});

