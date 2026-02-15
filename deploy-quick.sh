#!/bin/bash

# Quick Deploy Script - Updated for Node.js 20
# Run this to deploy your Firebase Cloud Functions

echo "🚀 Firebase Cloud Functions Deployment"
echo "========================================"
echo ""
echo "✅ Runtime: Node.js 20"
echo "✅ Firebase Functions SDK: v5.0.0"
echo ""

# Navigate to project
cd /home/plantsed11/AndroidStudioProjects/AFMobile

# Clean install dependencies
echo "📦 Installing dependencies..."
cd functions
rm -rf node_modules package-lock.json
npm install
cd ..

echo ""
echo "✅ Dependencies installed!"
echo ""

# Deploy using npx (no global install needed)
echo "🚀 Deploying to Firebase..."
echo ""

npx firebase-tools deploy --only functions,firestore --project anf-chocolate

echo ""
echo "✅ Deployment complete!"
echo ""
echo "🔗 View your functions at:"
echo "https://console.firebase.google.com/project/anf-chocolate/functions"
echo ""
