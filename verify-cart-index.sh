#!/bin/bash
# Cart Index Verification Script

echo "🔍 Verifying Firestore Cart Index Setup..."
echo ""

# Check if firestore.indexes.json exists
if [ -f "firestore.indexes.json" ]; then
    echo "✅ firestore.indexes.json exists"
else
    echo "❌ firestore.indexes.json NOT found"
    exit 1
fi

# Check if cart index is defined
if grep -q '"collectionGroup": "cart"' firestore.indexes.json; then
    echo "✅ Cart index is defined in firestore.indexes.json"
else
    echo "❌ Cart index NOT found in firestore.indexes.json"
    exit 1
fi

# Check Firebase indexes
echo ""
echo "📋 Current Firebase Indexes:"
firebase firestore:indexes 2>/dev/null | grep -A 20 '"cart"' || echo "⚠️  Could not fetch Firebase indexes (you may need to login)"

echo ""
echo "🎯 Expected Query in CartRepository:"
echo "   Collection: cart"
echo "   Where: userId == <current-user-id>"
echo "   OrderBy: addedAt DESC"
echo ""

# Check if CartRepository exists and has the query
if [ -f "app/src/main/java/com/example/afmobile/data/CartRepository.kt" ]; then
    echo "✅ CartRepository.kt exists"
    if grep -q 'whereEqualTo("userId"' app/src/main/java/com/example/afmobile/data/CartRepository.kt && \
       grep -q 'orderBy("addedAt"' app/src/main/java/com/example/afmobile/data/CartRepository.kt; then
        echo "✅ Query pattern matches index requirements"
    else
        echo "⚠️  Query pattern may not match - check CartRepository"
    fi
else
    echo "❌ CartRepository.kt NOT found"
fi

echo ""
echo "✨ Verification Complete!"
echo ""
echo "📝 Next Steps:"
echo "   1. Make sure the index is building/built in Firebase Console"
echo "   2. Test the app by:"
echo "      - Sign in with a user account"
echo "      - Navigate to Cart tab"
echo "      - Add items to cart"
echo "      - Cart should load without FAILED_PRECONDITION error"
echo ""
echo "🔗 Firebase Console: https://console.firebase.google.com/project/anf-chocolate/firestore/indexes"
