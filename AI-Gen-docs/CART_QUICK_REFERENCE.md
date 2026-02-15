# 🛒 Cart Functions Quick Reference

## Firebase Cart System - Complete API

---

## 📦 Core Functions

### **Add to Cart**
```kotlin
// In ProductDetailBottomSheet or anywhere
cartViewModel.addToCart(product, quantity) { success ->
    if (success) {
        // Item added successfully
    } else {
        // Failed (user not authenticated or error)
    }
}
```

### **Load Cart Items**
```kotlin
// Load all cart items for current user
cartViewModel.loadCartItems()

// Observe changes
cartViewModel.cartItemsWithProducts.observe(viewLifecycleOwner) { items ->
    // Update UI with cart items
}
```

### **Update Quantity**
```kotlin
// Increase or decrease quantity
cartViewModel.updateQuantity(cartItemId, newQuantity) { success ->
    if (success) {
        // Quantity updated
    } else {
        // Failed
    }
}
```

### **Remove from Cart**
```kotlin
// Remove single item
cartViewModel.removeFromCart(cartItemId) { success ->
    if (success) {
        // Item removed
    } else {
        // Failed
    }
}
```

### **Clear Cart**
```kotlin
// Remove all items
cartViewModel.clearCart { success ->
    if (success) {
        // Cart cleared
    } else {
        // Failed
    }
}
```

---

## 📊 Cart Data Access

### **Get Item Count**
```kotlin
val count = cartViewModel.getCartItemCount()
// Returns total quantity across all items
```

### **Get Total Price**
```kotlin
val total = cartViewModel.getCartTotalPrice()
// Returns sum of (price × quantity) for all items
```

### **Observe Cart Count**
```kotlin
cartViewModel.cartItemCount.observe(viewLifecycleOwner) { count ->
    // Update badge: "X items"
}
```

### **Observe Total Price**
```kotlin
cartViewModel.cartTotalPrice.observe(viewLifecycleOwner) { total ->
    // Update price display: "₱XX.XX"
}
```

---

## 🔄 Real-time Updates

Cart automatically updates in real-time using Firestore listeners!

```kotlin
// Setup once in init block of ViewModel
cartViewModel.setupCartListener()

// Now any changes to cart in Firestore
// will automatically update the UI
```

**No polling needed!** Changes sync instantly across:
- Multiple devices
- Multiple app instances
- Background/foreground states

---

## 🗄️ Firestore Structure

### **Collection:** `cart`

### **Document Fields:**
```javascript
{
  userId: String,           // Firebase Auth UID
  productId: String,        // Product document ID
  productName: String,      // Cached for display
  productPrice: Double,     // Cached for calculations
  productImageUrl: String,  // Cached for display
  quantity: Number,         // Item quantity (1+)
  addedAt: Timestamp,       // When added to cart
  updatedAt: Timestamp      // Last update time
}
```

### **Queries:**
```kotlin
// Get user's cart items
firestore.collection("cart")
    .whereEqualTo("userId", currentUserId)
    .orderBy("addedAt", Query.Direction.DESCENDING)
    .get()

// Check if product already in cart
firestore.collection("cart")
    .whereEqualTo("userId", currentUserId)
    .whereEqualTo("productId", productId)
    .limit(1)
    .get()
```

---

## 🔐 Security Rules

```javascript
match /cart/{cartItemId} {
  // Users can only access their own cart items
  allow read: if request.auth.uid == resource.data.userId;
  allow create: if request.auth.uid == request.resource.data.userId;
  allow update: if request.auth.uid == resource.data.userId;
  allow delete: if request.auth.uid == resource.data.userId;
}
```

**Key Points:**
- Must be authenticated to access cart
- Can only see/modify own cart items
- userId must match authenticated user

---

## 📱 UI Integration

### **Show Cart Badge**
```xml
<!-- In toolbar or bottom nav -->
<TextView
    android:id="@+id/cartBadge"
    android:text="0" />
```

```kotlin
cartViewModel.cartItemCount.observe(this) { count ->
    cartBadge.text = count.toString()
    cartBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
}
```

### **Display Cart Items**
```kotlin
// Setup RecyclerView with CartAdapter
val cartAdapter = CartAdapter(
    onQuantityChange = { id, qty -> updateQuantity(id, qty) },
    onRemoveClick = { id -> removeItem(id) }
)

recyclerView.adapter = cartAdapter

// Observe and submit data
cartViewModel.cartItemsWithProducts.observe(this) { items ->
    cartAdapter.submitList(items)
}
```

### **Show Empty State**
```kotlin
cartViewModel.cartItems.observe(this) { items ->
    if (items.isEmpty()) {
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    } else {
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}
```

---

## 🎯 Common Patterns

### **Add to Cart from Product Detail**
```kotlin
fun onAddToCartClick() {
    val product = currentProduct ?: return
    val quantity = selectedQuantity
    
    // Validate stock
    if (quantity > product.stockLevel) {
        showError("Not enough stock")
        return
    }
    
    // Add to cart
    cartViewModel.addToCart(product, quantity) { success ->
        if (success) {
            showSuccess("Added to cart!")
            navigateToCart() // or dismiss()
        } else {
            showError("Please sign in to add items to cart")
        }
    }
}
```

### **Cart Item with Stock Warning**
```kotlin
fun bindCartItem(item: CartItemWithProduct) {
    val product = item.product
    val cartItem = item.cartItem
    
    // Check availability
    if (product == null || product.stockLevel == 0) {
        stockWarning.text = "Out of stock"
        stockWarning.visibility = View.VISIBLE
        quantityControls.isEnabled = false
    } else if (cartItem.quantity > product.stockLevel) {
        stockWarning.text = "Only ${product.stockLevel} available"
        stockWarning.visibility = View.VISIBLE
    } else {
        stockWarning.visibility = View.GONE
    }
}
```

### **Checkout Flow**
```kotlin
fun onCheckoutClick() {
    val items = cartViewModel.cartItems.value ?: emptyList()
    
    if (items.isEmpty()) {
        showError("Cart is empty")
        return
    }
    
    // Validate all items have stock
    val itemsWithProducts = cartViewModel.cartItemsWithProducts.value ?: emptyList()
    val unavailable = itemsWithProducts.filter { !it.isAvailable() }
    
    if (unavailable.isNotEmpty()) {
        showError("Some items are out of stock. Please review your cart.")
        return
    }
    
    // Proceed to checkout
    val total = cartViewModel.getCartTotalPrice()
    navigateToCheckout(items, total)
}
```

---

## 🧪 Testing

### **Test Add to Cart**
```kotlin
@Test
fun testAddToCart() {
    val product = createTestProduct()
    cartViewModel.addToCart(product, 2) { success ->
        assertTrue(success)
        assertEquals(2, cartViewModel.getCartItemCount())
    }
}
```

### **Test Update Quantity**
```kotlin
@Test
fun testUpdateQuantity() {
    // Add item first
    addItemToCart()
    val item = getFirstCartItem()
    
    // Update quantity
    cartViewModel.updateQuantity(item.id, 5) { success ->
        assertTrue(success)
        assertEquals(5, item.quantity)
    }
}
```

### **Test Stock Validation**
```kotlin
@Test
fun testStockValidation() {
    val product = createTestProduct(stockLevel = 3)
    
    // Try to add more than available
    cartViewModel.addToCart(product, 5) { success ->
        assertFalse(success) // Should fail or cap at 3
    }
}
```

---

## 🐛 Troubleshooting

### **Cart not loading?**
```kotlin
// Check authentication
val user = FirebaseAuth.getInstance().currentUser
if (user == null) {
    Log.e(TAG, "User not authenticated")
    // Redirect to login
}

// Check Firestore connection
// Check logs: adb logcat | grep CartRepository
```

### **Items not appearing?**
```kotlin
// Verify userId matches
Log.d(TAG, "Current user: ${auth.currentUser?.uid}")
Log.d(TAG, "Cart item userId: ${cartItem.userId}")

// Check Firestore rules
// Verify cart item has correct userId field
```

### **Realtime updates not working?**
```kotlin
// Make sure listener is setup
cartViewModel.setupCartListener()

// Check Firestore console for write activity
// Verify device has internet connection
```

---

## 📚 Key Files

### **Data Layer:**
- `CartItem.kt` - Data models
- `CartRepository.kt` - Firebase operations

### **ViewModel:**
- `CartViewModel.kt` - UI state management

### **UI Layer:**
- `CartFragment.kt` - Cart screen
- `CartAdapter.kt` - RecyclerView adapter
- `ProductDetailBottomSheet.kt` - Add to cart

### **Layouts:**
- `fragment_cart.xml` - Cart screen layout
- `item_cart.xml` - Cart item layout

### **Firebase:**
- `firestore.rules` - Security rules

---

## 🎯 Quick Commands

### **Load Cart**
```kotlin
cartViewModel.loadCartItems()
```

### **Add Item**
```kotlin
cartViewModel.addToCart(product, qty) { success -> }
```

### **Update Item**
```kotlin
cartViewModel.updateQuantity(id, newQty) { success -> }
```

### **Remove Item**
```kotlin
cartViewModel.removeFromCart(id) { success -> }
```

### **Clear All**
```kotlin
cartViewModel.clearCart { success -> }
```

### **Get Count**
```kotlin
val count = cartViewModel.getCartItemCount()
```

### **Get Total**
```kotlin
val total = cartViewModel.getCartTotalPrice()
```

---

## ✅ Status

**Implementation:** ✅ Complete  
**Build:** ✅ Success  
**Deployment:** ✅ Firestore rules deployed  
**Testing:** ⏳ Pending user testing  

**Ready to use!** 🎉

---

**Last Updated:** February 15, 2026  
**Project:** AFMobile  
**Firebase Project:** anf-chocolate
