# 📱 AFMobile App Flow Analysis

**Date:** February 14, 2026  
**Status:** ✅ YES - Products are being fetched from Firebase Firestore!

---

## 🎯 Complete App Flow

### 1️⃣ **Authentication Flow (MainActivity)**

```
App Launch
    ↓
MainActivity (Login Screen)
    ├── User enters email/password
    │   ↓
    │   Firebase Authentication
    │   ↓
    │   ✅ Success → Navigate to HomeActivity
    │   ❌ Failure → Show error message
    │
    └── User clicks "Sign up here"
        ↓
        Show sign-up overlay
        ↓
        User fills: username, email, password
        ↓
        Firebase Auth creates account
        ↓
        Cloud Function creates Firestore profile
        ↓
        Success → Close overlay, user can login
```

**Code Location:** `/app/src/main/java/com/example/afmobile/MainActivity.kt`

---

### 2️⃣ **Home Flow (HomeActivity + HomeFragment)**

```
HomeActivity
    ↓
Bottom Navigation (Home, Cart, Profile)
    ↓
HomeFragment (Default Screen)
    ├── Search Bar
    ├── Category Chips (All, WHITE, DARK, MILK, etc.)
    ├── Product Grid (RecyclerView)
    └── Swipe to Refresh
```

**Code Locations:**
- Activity: `/app/src/main/java/com/example/afmobile/HomeActivity.kt`
- Fragment: `/app/src/main/java/com/example/afmobile/HomeFragment.kt`

---

## 🔄 Product Data Flow (Firebase → App)

### **Architecture: Firebase + Room (Local Cache)**

```
┌─────────────────────────────────────────────────┐
│         Firebase Firestore (Cloud)              │
│         Collection: "products"                  │
│                                                 │
│  Document: 3NbKvPK9euzNcCS71DFr               │
│  ├── name: "Tobleron"                         │
│  ├── price: 100                               │
│  ├── category: "WHITE"                        │
│  ├── imageUrl: "https://..."                  │
│  ├── stockLevel: 20                           │
│  ├── description: "Test2"                     │
│  ├── sku: "Hi"                                │
│  └── salesCount: 0                            │
└─────────────────────────────────────────────────┘
                    ↓
        [Sync via ProductRepository]
                    ↓
┌─────────────────────────────────────────────────┐
│        Room Database (Local Cache)              │
│        Table: "products"                        │
│                                                 │
│  All products stored locally for:              │
│  ✅ Offline access                             │
│  ✅ Fast loading                               │
│  ✅ Search & filter                            │
└─────────────────────────────────────────────────┘
                    ↓
        [LiveData observation]
                    ↓
┌─────────────────────────────────────────────────┐
│              UI (HomeFragment)                  │
│  ✅ RecyclerView displays products             │
│  ✅ Real-time updates                          │
│  ✅ Search & category filtering                │
└─────────────────────────────────────────────────┘
```

---

## 📦 Product Sync Mechanism

### **Three Ways Products Are Synced:**

#### 1. **Initial Sync (App Launch)**
```kotlin
// HomeFragment.kt - Line 81
override fun onViewCreated(...) {
    // ...
    syncProducts() // Called immediately
}
```

#### 2. **Manual Sync (Swipe to Refresh)**
```kotlin
// HomeFragment.kt - Line 111
swipeRefreshLayout.setOnRefreshListener {
    syncProducts() // User pulls down to refresh
}
```

#### 3. **Automatic Background Sync (Every 15 minutes)**
```kotlin
// HomeFragment.kt - Line 194
PeriodicWorkRequestBuilder<ProductSyncWorker>(
    15, TimeUnit.MINUTES // Syncs every 15 min
)
```

**Worker Location:** `/app/src/main/java/com/example/afmobile/workers/ProductSyncWorker.kt`

---

## 🔍 Product Repository Flow

### **File:** `/app/src/main/java/com/example/afmobile/data/ProductRepository.kt`

```kotlin
suspend fun syncProductsFromFirebase(): Boolean {
    // 1. Query Firestore collection "products"
    val snapshot = firestore.collection("products").get().await()
    
    // 2. Parse documents into Product objects
    val products = snapshot.documents.mapNotNull { doc ->
        val firebaseProduct = doc.toObject(FirebaseProduct::class.java)
        firebaseProduct?.toProduct(doc.id)
    }
    
    // 3. Insert into local Room database
    productDao.insertProducts(products)
    
    // 4. LiveData automatically updates UI
    return true
}
```

---

## 📊 Data Models

### **Firestore Document Structure**
```json
{
  "name": "Tobleron",
  "description": "Test2",
  "price": 100,
  "category": "WHITE",
  "imageUrl": "https://firebasestorage.googleapis.com/...",
  "sku": "Hi",
  "stockLevel": 20,
  "salesCount": 0,
  "createdAt": Timestamp,
  "updatedAt": Timestamp
}
```

### **Room Database Model**
```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val sku: String,
    val stockLevel: Int,
    val salesCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)
```

**Location:** `/app/src/main/java/com/example/afmobile/data/Product.kt`

---

## 🎨 UI Components

### **Product Display (RecyclerView)**

**Adapter:** `/app/src/main/java/com/example/afmobile/adapters/ProductAdapter.kt`

```kotlin
class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductViewHolder>(ProductDiffCallback())

// Each item shows:
- Product Image (loaded with Glide)
- Product Name
- Price (formatted as ₱X.XX)
- Category
- Stock Level
```

**Layout:** `/app/src/main/res/layout/item_product.xml`

---

## 🔎 Features Implemented

### ✅ **Search Functionality**
```kotlin
// HomeFragment.kt
searchEditText.addTextChangedListener {
    val query = s.toString().trim()
    if (query.isNotEmpty()) {
        searchProducts(query) // Searches by name or description
    }
}
```

### ✅ **Category Filtering**
```kotlin
// Dynamic category chips created from database
productViewModel.allCategories.observe { categories ->
    // Creates: [All] [WHITE] [DARK] [MILK] ...
    updateCategoryChips(categories)
}

// Filter when clicked
filterByCategory(category)
```

### ✅ **Image Loading**
```kotlin
// Uses Glide library
Glide.with(context)
    .load(product.imageUrl)
    .placeholder(R.drawable.ic_launcher_background)
    .error(R.drawable.ic_launcher_foreground)
    .centerCrop()
    .into(productImage)
```

### ✅ **Swipe to Refresh**
```kotlin
swipeRefreshLayout.setOnRefreshListener {
    syncProducts()
}
```

---

## 📱 Database Queries

### **File:** `/app/src/main/java/com/example/afmobile/data/ProductDao.kt`

```kotlin
// Get all products
@Query("SELECT * FROM products ORDER BY updatedAt DESC")
fun getAllProducts(): LiveData<List<Product>>

// Filter by category
@Query("SELECT * FROM products WHERE category = :category")
fun getProductsByCategory(category: String): LiveData<List<Product>>

// Search products
@Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' 
       OR description LIKE '%' || :query || '%'")
fun searchProducts(query: String): LiveData<List<Product>>

// Get all categories (for chips)
@Query("SELECT DISTINCT category FROM products ORDER BY category")
fun getAllCategories(): LiveData<List<String>>
```

---

## 🎯 ViewModel Architecture

### **File:** `/app/src/main/java/com/example/afmobile/viewmodels/ProductViewModel.kt`

```kotlin
class ProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    val allCategories: LiveData<List<String>>
    
    fun syncProducts() {
        viewModelScope.launch {
            repository.syncProductsFromFirebase()
        }
    }
    
    fun getProductsByCategory(category: String): LiveData<List<Product>>
    fun searchProducts(query: String): LiveData<List<Product>>
}
```

**Pattern:** MVVM (Model-View-ViewModel)

---

## ⚡ Performance Optimizations

### 1. **Local Caching (Room Database)**
- ✅ Products cached locally
- ✅ Instant loading from local DB
- ✅ Offline access support

### 2. **Background Sync (WorkManager)**
- ✅ Syncs every 15 minutes automatically
- ✅ Only when network is available
- ✅ Battery-efficient periodic work

### 3. **LiveData Observation**
- ✅ Automatic UI updates
- ✅ Lifecycle-aware (no memory leaks)
- ✅ Reactive programming

### 4. **DiffUtil for RecyclerView**
- ✅ Efficient list updates
- ✅ Only changed items re-rendered
- ✅ Smooth animations

### 5. **Image Loading (Glide)**
- ✅ Automatic caching
- ✅ Memory management
- ✅ Placeholder & error handling

---

## 🔐 Firebase Security (Firestore Rules)

Products are publicly readable (for e-commerce browsing):

```javascript
// firestore.rules
match /products/{productId} {
  allow read: if true; // Anyone can browse products
  allow write: if request.auth != null; // Only authenticated users
}
```

---

## 📊 Current Firestore Data

**Your product in Firebase:**
```
Collection: products
Document ID: 3NbKvPK9euzNcCS71DFr

Fields:
✅ name: "Tobleron"
✅ description: "Test2"
✅ price: 100
✅ category: "WHITE"
✅ imageUrl: "https://firebasestorage.googleapis.com/v0/b/anf-chocolate..."
✅ sku: "Hi"
✅ stockLevel: 20
✅ salesCount: 0
✅ createdAt: February 14, 2026 at 1:21:30 AM UTC+8
✅ updatedAt: February 14, 2026 at 1:21:41 AM UTC+8
```

**This product WILL appear in your app** when you sync!

---

## 🚀 How to Test

### **Test the Complete Flow:**

1. **Build and Run App**
   ```bash
   cd /home/plantsed11/AndroidStudioProjects/AFMobile
   ./gradlew installDebug
   ```

2. **Login/Sign Up**
   - Use Firebase authentication
   - After login → Navigate to HomeActivity

3. **View Products**
   - HomeFragment loads automatically
   - Should see "Tobleron" product
   - Image, price (₱100), category (WHITE) displayed

4. **Test Features**
   - ✅ Search: Type "Tobleron" in search bar
   - ✅ Category: Click "WHITE" chip to filter
   - ✅ Swipe down to refresh
   - ✅ Click product (shows toast with details)

5. **Check Logs**
   ```bash
   adb logcat | grep ProductRepository
   ```
   Should see:
   ```
   D/ProductRepository: Starting product sync from Firebase...
   D/ProductRepository: Successfully synced 1 products
   ```

---

## 🐛 Potential Issues & Solutions

### Issue 1: Products Not Showing
**Cause:** Sync hasn't happened yet  
**Solution:** Pull down to refresh or wait for initial sync

### Issue 2: Images Not Loading
**Cause:** Firebase Storage permissions or URL invalid  
**Solution:** Check Firebase Storage rules, verify imageUrl is accessible

### Issue 3: Empty State Shown
**Cause:** No products in Firestore or sync failed  
**Solution:** 
- Check Firebase Console → Firestore → products collection
- Check logcat for sync errors

### Issue 4: Category Chip Missing
**Cause:** Category field mismatch  
**Solution:** Ensure category field exists in all Firestore documents

---

## 📈 Next Steps for Enhancement

### 1. **Product Detail Screen**
```kotlin
// TODO: In HomeFragment.kt - Line 217
private fun onProductClick(product: Product) {
    // Navigate to ProductDetailFragment
    // Show: full description, larger image, add to cart button
}
```

### 2. **Shopping Cart**
- Add CartFragment
- Store cart items in Room database
- Implement cart badge counter

### 3. **Admin Panel (Add/Edit Products)**
- Create AdminActivity
- Firebase Functions for CRUD operations
- Image upload to Firebase Storage

### 4. **Real-time Updates**
```kotlin
// Use Firestore listeners instead of periodic sync
firestore.collection("products")
    .addSnapshotListener { snapshot, error ->
        // Real-time updates when products change
    }
```

### 5. **Pagination**
- Implement paging for large product lists
- Load products in batches (20 at a time)

---

## ✅ Summary

### **YES! Products ARE being fetched from Firebase Firestore! 🎉**

**Architecture:**
```
Firebase Firestore (Cloud Database)
    ↓ [sync]
Room Database (Local Cache)
    ↓ [LiveData]
RecyclerView (UI)
```

**Your Product Flow:**
```
1. User logs in → MainActivity → HomeActivity
2. HomeFragment loads
3. syncProducts() called automatically
4. ProductRepository fetches from Firestore collection "products"
5. Stores in Room database
6. LiveData notifies UI
7. RecyclerView displays "Tobleron" and other products
8. Background worker syncs every 15 minutes
```

**Status:** ✅ **Fully Functional E-commerce Product Display System**

---

**Project:** AFMobile  
**Firebase Project:** anf-chocolate  
**Database:** Firestore + Room  
**Architecture:** MVVM with Repository Pattern  
**Last Updated:** February 14, 2026
