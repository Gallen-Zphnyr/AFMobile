#!/bin/bash

# 🚀 AFMobile Checkout System - Quick Deploy Script
# Run this script to deploy and test the checkout implementation

echo "════════════════════════════════════════════════════════════"
echo "   🛒 AFMobile Checkout System Deployment"
echo "════════════════════════════════════════════════════════════"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Change to project directory
cd /home/plantsed11/AndroidStudioProjects/AFMobile

# Step 1: Deploy Firestore Indexes
echo "${BLUE}Step 1: Deploying Firestore Indexes...${NC}"
echo "───────────────────────────────────────────────────────────"
firebase deploy --only firestore:indexes

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Firestore indexes deployed successfully!${NC}"
else
    echo "${RED}❌ Failed to deploy Firestore indexes${NC}"
    echo "Please check your Firebase login: firebase login"
    exit 1
fi

echo ""

# Step 2: Clean Build
echo "${BLUE}Step 2: Cleaning previous builds...${NC}"
echo "───────────────────────────────────────────────────────────"
./gradlew clean

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Clean successful!${NC}"
else
    echo "${YELLOW}⚠️  Clean had warnings, continuing...${NC}"
fi

echo ""

# Step 3: Build Debug APK
echo "${BLUE}Step 3: Building debug APK...${NC}"
echo "───────────────────────────────────────────────────────────"
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Build successful!${NC}"

    # Find the APK
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo "${GREEN}   APK Location: $APK_PATH${NC}"
        echo "${GREEN}   APK Size: $APK_SIZE${NC}"
    fi
else
    echo "${RED}❌ Build failed${NC}"
    echo "Please check the error messages above"
    exit 1
fi

echo ""

# Step 4: Check for connected devices
echo "${BLUE}Step 4: Checking for connected devices...${NC}"
echo "───────────────────────────────────────────────────────────"
DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)

if [ $DEVICES -gt 0 ]; then
    echo "${GREEN}✅ Found $DEVICES connected device(s)${NC}"

    # Step 5: Install APK
    echo ""
    echo "${BLUE}Step 5: Installing APK...${NC}"
    echo "───────────────────────────────────────────────────────────"
    ./gradlew installDebug

    if [ $? -eq 0 ]; then
        echo "${GREEN}✅ App installed successfully!${NC}"
    else
        echo "${YELLOW}⚠️  Installation had issues${NC}"
    fi
else
    echo "${YELLOW}⚠️  No devices connected${NC}"
    echo "   Connect a device or start an emulator, then run:"
    echo "   ./gradlew installDebug"
fi

echo ""
echo "════════════════════════════════════════════════════════════"
echo "${GREEN}   ✅ Deployment Complete!${NC}"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "${BLUE}📱 Next Steps:${NC}"
echo "   1. Open the app on your device"
echo "   2. Add items to cart from Home tab"
echo "   3. Go to Cart tab → Click 'Checkout'"
echo "   4. Review order → Click 'Place Order'"
echo "   5. Click 'Pay Now' in the payment dialog"
echo "   6. View your order in the Orders tab"
echo ""
echo "${BLUE}📚 Documentation:${NC}"
echo "   • Quick Start: AI-Gen-docs/CHECKOUT_QUICK_START.md"
echo "   • Testing Guide: AI-Gen-docs/CHECKOUT_TESTING_GUIDE.md"
echo "   • Visual Flow: AI-Gen-docs/CHECKOUT_VISUAL_FLOW.md"
echo ""
echo "${BLUE}🔍 Verify in Firebase Console:${NC}"
echo "   https://console.firebase.google.com/project/anf-chocolate/firestore"
echo ""
echo "${GREEN}Happy Testing! 🎉${NC}"
echo ""
