# 🧪 Checkout System Testing Guide

## Quick Test Steps

### 1. **Prepare Test Environment**
```bash
# Deploy Firestore indexes
cd /home/plantsed11/AndroidStudioProjects/AFMobile
firebase deploy --only firestore:indexes

# Build and install app
./gradlew installDebug
```

### 2. **Test Checkout Flow**

#### Step 1: Add Items to Cart
1. Open app
2. Browse products on Home tab
3. Tap a product
4. Click "Add to Cart"
5. Add 2-3 different products

#### Step 2: Go to Checkout
1. Go to Cart tab
2. Verify items are displayed
3. Click "Checkout" button
4. CheckoutActivity should open

#### Step 3: Review Order
- ✅ Check delivery address is shown
- ✅ Verify all cart items are listed
- ✅ Confirm subtotal is correct
- ✅ Confirm delivery fee = ₱50.00
- ✅ Verify total = subtotal + delivery fee

#### Step 4: Place Order
1. Click "Place Order" button
2. Confirm order in dialog
3. Payment dialog should appear
4. Note the Order ID

#### Step 5: Confirm Payment
1. Click "Pay Now" button
2. Success message should appear
3. Cart should be cleared
4. Navigate to Orders tab

#### Step 6: View Order
- ✅ Order appears in Orders tab
- ✅ Status shows "Payment received - Waiting for admin approval"
- ✅ Total amount is correct
- ✅ Tap order to see details

---

## 🔍 Verification Checklist

### Cart Fragment
- [ ] Items display correctly
- [ ] Quantities can be changed
- [ ] Items can be removed
- [ ] Subtotal updates dynamically
- [ ] "Checkout" button is enabled when cart has items
- [ ] "Checkout" button navigates to CheckoutActivity

### Checkout Activity
- [ ] User's name, address, phone displayed
- [ ] All cart items are shown
- [ ] Prices are formatted correctly (₱XX.XX)
- [ ] Subtotal calculation is correct
- [ ] Delivery fee is ₱50.00
- [ ] Total is correct
- [ ] "Place Order" is disabled if no address
- [ ] Order confirmation dialog appears

### Payment Dialog
- [ ] Order ID is displayed
- [ ] Total amount is shown
- [ ] "Pay Now" button works
- [ ] "Pay Later" button works
- [ ] Dialog can't be dismissed accidentally

### Orders Fragment
- [ ] Orders are displayed newest first
- [ ] Order cards show correct info
- [ ] Status colors are correct
- [ ] "Pay Now" button appears for unpaid orders
- [ ] "Pay Now" button hidden for paid orders
- [ ] Tap order shows details dialog
- [ ] Swipe to refresh works

### Order Details Dialog
- [ ] Order ID displayed
- [ ] Date/time formatted correctly
- [ ] Status description is clear
- [ ] Delivery address shown
- [ ] Phone number shown
- [ ] All items listed
- [ ] Price breakdown is correct
- [ ] Total matches order

---

## 🐛 Common Issues & Solutions

### Issue: "Please set delivery address"
**Solution:** Go to Profile → My Address → Set address

### Issue: Cart not clearing after payment
**Solution:** Check CartViewModel.clearCart() is being called

### Issue: Orders not showing
**Solution:** 
1. Check Firestore index is deployed
2. Verify user is signed in
3. Check orders collection in Firebase Console

### Issue: Payment dialog doesn't appear
**Solution:** Check order creation succeeded in logs

### Issue: "Pay Now" button doesn't work
**Solution:** Check Firebase permissions for orders collection

---

## 📱 Test Scenarios

### Scenario 1: Normal Checkout Flow
```
Add items → Cart → Checkout → Place Order → Pay Now → Success
Expected: Order created with PAID status, cart cleared
```

### Scenario 2: Pay Later Flow
```
Add items → Cart → Checkout → Place Order → Pay Later
Expected: Order created with PENDING status, cart NOT cleared
Then: Orders → Find order → Pay Now → Success
Expected: Order updated to PAID status
```

### Scenario 3: Empty Cart Checkout
```
Empty cart → Click Checkout
Expected: Toast "Your cart is empty"
```

### Scenario 4: No Address Set
```
Add items → Checkout → No address set
Expected: "Please set delivery address" button disabled
```

### Scenario 5: Multiple Orders
```
Create 3-4 orders with different items
Expected: All orders visible in Orders tab, sorted by date
```

---

## 🔥 Firebase Console Checks

### 1. Check Orders Collection
```
Firebase Console → Firestore Database → orders
- Verify orders are being created
- Check all fields are populated
- Confirm timestamps are correct
```

### 2. Check Order Status
```
Find your order → Check fields:
- paymentStatus: "PAID"
- orderStatus: "PAID"
- paidAt: timestamp exists
- userId: matches your user ID
```

### 3. Check Indexes
```
Firebase Console → Firestore Database → Indexes
- Should see: orders (userId ASC, createdAt DESC)
- Status should be: Enabled
```

---

## 📊 Expected Data Flow

### 1. Order Creation
```kotlin
// In Firestore after placing order
{
  userId: "abc123",
  userName: "John Doe",
  userEmail: "john@example.com",
  deliveryAddress: "123 Main St",
  phoneNumber: "+1234567890",
  items: [
    {
      productId: "prod1",
      productName: "Product 1",
      productPrice: 100.0,
      quantity: 2
    }
  ],
  subtotal: 200.0,
  deliveryFee: 50.0,
  totalAmount: 250.0,
  paymentStatus: "PENDING",
  orderStatus: "PENDING",
  createdAt: Timestamp,
  paidAt: null
}
```

### 2. After Payment
```kotlin
// Updated fields
{
  paymentStatus: "PAID",
  orderStatus: "PAID",
  paidAt: Timestamp
}
```

---

## ✅ Success Criteria

All tests pass when:
1. ✅ Can add items to cart
2. ✅ Can navigate to checkout
3. ✅ Order is created successfully
4. ✅ Payment dialog appears
5. ✅ Payment confirmation works
6. ✅ Cart is cleared after payment
7. ✅ Order appears in Orders tab
8. ✅ Order details are correct
9. ✅ Can pay unpaid orders later
10. ✅ Status colors are correct

---

## 🎯 Test Accounts

### Test User 1
- Email: test1@example.com
- Should have: Address set, phone number
- Test: Complete checkout flow

### Test User 2
- Email: test2@example.com
- Should have: No address
- Test: Address validation

---

## 📝 Test Log Template

```
Date: _______________
Tester: _______________

[ ] Cart functionality
[ ] Checkout screen
[ ] Place order
[ ] Payment flow
[ ] Orders display
[ ] Order details
[ ] Pay later functionality

Issues Found:
1. _______________
2. _______________

Notes:
_______________
```

---

## 🚀 Ready to Test!

Run these commands:
```bash
# 1. Deploy indexes
firebase deploy --only firestore:indexes

# 2. Build app
./gradlew clean build

# 3. Install on device/emulator
./gradlew installDebug

# 4. Start testing!
```

---

**Happy Testing! 🎉**
