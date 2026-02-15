# 🔥 Firestore Cart Index Fix

## Problem
The app was crashing when trying to load cart items with the following error:
```
FAILED_PRECONDITION: The query requires an index.
```

## Root Cause
The `CartRepository` query was filtering by `userId` and ordering by `addedAt`:
```kotlin
firestore.collection(COLLECTION_CART)
    .whereEqualTo("userId", userId)
    .orderBy("addedAt", Query.Direction.DESCENDING)
    .get()
```

Firestore requires a composite index when you combine:
- A filter on one field (`userId`)
- An ordering on another field (`addedAt`)

## Solution
Added the required composite index to `firestore.indexes.json`:

```json
{
  "indexes": [
    {
      "collectionGroup": "cart",
      "queryScope": "COLLECTION",
      "fields": [
        {
          "fieldPath": "userId",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "addedAt",
          "order": "DESCENDING"
        }
      ]
    }
  ],
  "fieldOverrides": []
}
```

## Deployment
Deployed to Firebase with:
```bash
firebase deploy --only firestore:indexes
```

✅ **Status**: Index successfully deployed and active!

### Actual Index Structure (from Firebase Console)
```json
{
  "collectionGroup": "cart",
  "queryScope": "COLLECTION",
  "fields": [
    { "fieldPath": "userId", "order": "ASCENDING" },
    { "fieldPath": "addedAt", "order": "DESCENDING" },
    { "fieldPath": "__name__", "order": "DESCENDING" }
  ],
  "density": "SPARSE_ALL"
}
```
*Note: Firebase automatically adds the `__name__` field for tie-breaking*

## Testing
After deployment:
1. The cart should now load properly for authenticated users
2. Cart items will be sorted by `addedAt` in descending order (newest first)
3. No more `FAILED_PRECONDITION` errors

## Notes
- Index creation in Firebase can take a few minutes to complete
- The index is specific to the `cart` collection
- Other existing indexes (for orders and products collections) were preserved during deployment

## Verified Cart Data Structure
Example cart item document in Firestore:
```
Collection: cart
Document ID: ZsxkWx9XIWXy3d3UW2LG

Fields:
- addedAt: timestamp (e.g., February 15, 2026 at 4:30:43 PM UTC+8)
- productId: string (e.g., "3NbKvPK9euzNcCS71DFr")
- productImageUrl: string (Firebase Storage URL)
- productName: string (e.g., "Tobleron")
- productPrice: number (e.g., 149)
- quantity: number (e.g., 1)
- updatedAt: timestamp
- userId: string (e.g., "4Qw6l0ZqRLcjg0eDUg62NhtIAAk1")
```

✅ All required fields are present and properly typed for the index to work!

