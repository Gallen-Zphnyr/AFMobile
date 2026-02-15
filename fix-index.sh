#!/bin/bash

# 🔥 Quick Fix: Deploy Firestore Index for Orders
# This script will help you create the required Firestore index

echo "════════════════════════════════════════════════════════════"
echo "   🔥 Firestore Index Fix for Orders Collection"
echo "════════════════════════════════════════════════════════════"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "${YELLOW}⚠️  The orders query requires a Firestore index${NC}"
echo ""
echo "${BLUE}You have 2 options to fix this:${NC}"
echo ""

# Option 1: Automatic via Firebase CLI
echo "${GREEN}Option 1: Deploy via Firebase CLI (Automatic)${NC}"
echo "───────────────────────────────────────────────────────────"
echo "cd /home/plantsed11/AndroidStudioProjects/AFMobile"
echo "firebase deploy --only firestore:indexes"
echo ""

# Option 2: Manual via Console
echo "${GREEN}Option 2: Create via Firebase Console (Manual)${NC}"
echo "───────────────────────────────────────────────────────────"
echo "Click this link to create the index automatically:"
echo ""
echo "${BLUE}https://console.firebase.google.com/v1/r/project/anf-chocolate/firestore/indexes?create_composite=Ckxwcm9qZWN0cy9hbmYtY2hvY29sYXRlL2RhdGFiYXNlcy8oZGVmYXVsdCkvY29sbGVjdGlvbkdyb3Vwcy9vcmRlcnMvaW5kZXhlcy9fEAEaCgoGdXNlcklkEAEaDQoJY3JlYXRlZEF0EAIaDAoIX19uYW1lX18QAg${NC}"
echo ""
echo "This will open Firebase Console and automatically configure the index."
echo "Just click 'Create Index' and wait for it to build (1-5 minutes)."
echo ""

# Try automatic deployment
echo "${BLUE}Attempting automatic deployment...${NC}"
echo ""

cd /home/plantsed11/AndroidStudioProjects/AFMobile

# Check if Firebase CLI is available
if command -v firebase &> /dev/null; then
    echo "${GREEN}✅ Firebase CLI found${NC}"

    # Try to deploy
    echo "Deploying indexes..."
    firebase deploy --only firestore:indexes

    if [ $? -eq 0 ]; then
        echo ""
        echo "${GREEN}✅ Index deployed successfully!${NC}"
        echo ""
        echo "Wait 1-2 minutes for the index to build, then:"
        echo "1. Restart the app"
        echo "2. Go to Orders tab"
        echo "3. Your orders should now load"
        echo ""
    else
        echo ""
        echo "${YELLOW}⚠️  Automatic deployment failed${NC}"
        echo ""
        echo "${BLUE}Please use Option 2 (manual):${NC}"
        echo "https://console.firebase.google.com/v1/r/project/anf-chocolate/firestore/indexes?create_composite=Ckxwcm9qZWN0cy9hbmYtY2hvY29sYXRlL2RhdGFiYXNlcy8oZGVmYXVsdCkvY29sbGVjdGlvbkdyb3Vwcy9vcmRlcnMvaW5kZXhlcy9fEAEaCgoGdXNlcklkEAEaDQoJY3JlYXRlZEF0EAIaDAoIX19uYW1lX18QAg"
    fi
else
    echo "${RED}❌ Firebase CLI not found${NC}"
    echo ""
    echo "${BLUE}Please use Option 2 (manual via console):${NC}"
    echo "https://console.firebase.google.com/v1/r/project/anf-chocolate/firestore/indexes?create_composite=Ckxwcm9qZWN0cy9hbmYtY2hvY29sYXRlL2RhdGFiYXNlcy8oZGVmYXVsdCkvY29sbGVjdGlvbkdyb3Vwcy9vcmRlcnMvaW5kZXhlcy9fEAEaCgoGdXNlcklkEAEaDQoJY3JlYXRlZEF0EAIaDAoIX19uYW1lX18QAg"
fi

echo ""
echo "════════════════════════════════════════════════════════════"
echo "${BLUE}📚 What This Index Does:${NC}"
echo "───────────────────────────────────────────────────────────"
echo "Allows querying orders by:"
echo "  • userId (find user's orders)"
echo "  • createdAt (sort by newest first)"
echo ""
echo "This is required for the Orders tab to work properly."
echo ""
echo "════════════════════════════════════════════════════════════"
echo ""
