#!/bin/bash

# Firebase Cloud Functions Deployment Script
# Run this script to deploy your Cloud Functions to Firebase

set -e  # Exit on error

echo "🚀 Firebase Cloud Functions Deployment Script"
echo "=============================================="
echo ""
echo "📦 Using Node.js 20 (required as of Oct 2025)"
echo ""

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found!"
    echo ""
    echo "Installing Firebase CLI..."
    sudo npm install -g firebase-tools
    echo "✅ Firebase CLI installed successfully!"
    echo ""
fi

# Display Firebase version
echo "📦 Firebase CLI version:"
firebase --version
echo ""

# Navigate to project directory
cd /home/plantsed11/AndroidStudioProjects/AFMobile

# Check if already logged in
echo "🔐 Checking Firebase authentication..."
if ! firebase projects:list &> /dev/null; then
    echo "Please login to Firebase..."
    firebase login
fi

echo ""
echo "✅ Firebase authentication verified!"
echo ""

# Set the Firebase project
echo "🎯 Setting Firebase project to: anf-chocolate"
firebase use anf-chocolate

echo ""
echo "📝 Project configuration:"
firebase projects:list | grep anf-chocolate
echo ""

# Install function dependencies
echo "📦 Installing Cloud Functions dependencies..."
cd functions
npm install
cd ..
echo "✅ Dependencies installed!"
echo ""

# Deploy Cloud Functions
echo "🚀 Deploying Cloud Functions..."
echo ""
firebase deploy --only functions

echo ""
echo "✅ Cloud Functions deployed successfully!"
echo ""

# Deploy Firestore rules
echo "🔒 Deploying Firestore security rules..."
firebase deploy --only firestore:rules

echo ""
echo "✅ Firestore rules deployed successfully!"
echo ""

# Display deployed functions
echo "📋 Deployed Functions:"
firebase functions:list

echo ""
echo "🎉 Deployment Complete!"
echo ""
echo "Next steps:"
echo "1. Go to Firebase Console: https://console.firebase.google.com/project/anf-chocolate"
echo "2. Enable Authentication → Email/Password"
echo "3. Create Firestore Database"
echo "4. Test the app!"
echo ""
