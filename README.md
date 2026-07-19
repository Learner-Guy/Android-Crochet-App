# 🧶 Crochet Inventory & Order Management

A modern Android application built with **Java** and **Room Database** for small crochet businesses to manage inventory, customer orders, categories, and business information—all completely offline.

Whether you're selling handmade crochet products locally or through social media, this app helps organize your business in one place.

---

# Features

## 📦 Inventory Management

- Add inventory items
- Edit existing items
- Delete inventory items
- Track available quantity
- Set low-stock thresholds
- Search inventory
- Category-based filtering
- Inventory images
- Notes for each item

---

## 🗂 Category Management

- Create unlimited categories
- Edit category names
- Delete categories
- Category icon picker
- Organize products efficiently

---

## 🛒 Order Management

- Create customer orders
- Edit orders
- Delete orders
- Add multiple products to an order
- View order details
- Order image attachments
- Track order status
- Manage order quantities

---

## 📊 Dashboard

- Total inventory count
- Total categories
- Total orders
- Low-stock summary
- Quick overview of business data

---

## ⚠ Low Stock Monitoring

Automatically identifies products whose quantity has fallen below the configured threshold.

---

## 🏢 Business Profile

Store business information such as:

- Business name
- Contact information
- Additional business details

---

## 🖼 Image Support

- Attach images to inventory items
- Attach images to customer orders
- Full-screen image viewer

---

## 📱 Material Design UI

- Clean interface
- RecyclerView-based lists
- Floating Action Buttons
- Responsive layouts
- Easy navigation

---

# Technology Stack

| Component | Technology |
|------------|------------|
| Language | Java |
| Platform | Android |
| Architecture | MVVM-inspired |
| Database | Room Database |
| Storage | SQLite |
| UI | Material Components |
| Navigation | Navigation Component |
| Build Tool | Gradle (Groovy DSL) |
| Image Handling | Android Image APIs |

---

# Project Structure

```
app
│
├── data
│   ├── AppDatabase
│   ├── InventoryDao
│   ├── OrderDao
│   ├── InventoryItem
│   ├── Order
│   ├── OrderItem
│   ├── Category
│   ├── BusinessProfile
│   ├── OrderStatus
│   ├── IconPack
│   └── DateConverter
│
├── ui
│   ├── dashboard
│   ├── inventory
│   ├── categories
│   ├── orders
│   ├── lowstock
│   └── components
│
├── MainActivity.java
│
└── AndroidManifest.xml
```

---

# Database

The application uses **Room Database** for offline persistence.

## Entities

### InventoryItem

Stores

- Product Name
- Category
- Quantity
- Low Stock Threshold
- Notes
- Product Image

---

### Category

Stores

- Category Name
- Category Icon

---

### Order

Stores

- Customer Name
- Order Date
- Delivery Date
- Status
- Notes
- Images

---

### OrderItem

Stores

- Linked Product
- Quantity
- Price (if applicable)

---

### BusinessProfile

Stores business details used throughout the application.

---

# Application Flow

```
Home Dashboard
        │
        ├────────────── Inventory
        │                 ├── Add Item
        │                 ├── Edit Item
        │                 ├── Delete Item
        │                 └── Search
        │
        ├────────────── Categories
        │                 ├── Add
        │                 ├── Edit
        │                 └── Delete
        │
        ├────────────── Orders
        │                 ├── Create
        │                 ├── Edit
        │                 ├── Order Items
        │                 ├── Images
        │                 └── Status
        │
        ├────────────── Low Stock
        │
        └────────────── Business Profile
```

---

# Current Features

- Offline-first architecture
- Room Database
- Inventory management
- Category management
- Order management
- Order details
- Order images
- Business profile
- Dashboard statistics
- Low-stock alerts
- Search inventory
- Material Design UI

---

# Future Roadmap

## Inventory

- Barcode scanning
- QR code support
- Product pricing
- Supplier management

## Orders

- Customer database
- Delivery tracking
- Payment status
- Invoice generation
- PDF receipts

## Reports

- Sales reports
- Monthly analytics
- Inventory valuation
- Profit & loss

## Cloud

- Google Drive backup
- Firebase Sync
- Multi-device synchronization

## User Experience

- Dark mode
- Notifications
- Backup & Restore
- Import/Export CSV
- Multi-language support

---

# Requirements

- Android Studio Hedgehog or newer
- JDK 17 (recommended)
- Android SDK 34
- Minimum SDK: API 24
- Gradle (Groovy DSL)

---

# Build

Clone the repository

```bash
git clone https://github.com/<username>/CrochetInventory.git
```

Open in Android Studio.

Build Debug APK

```bash
./gradlew assembleDebug
```

Build Release APK

```bash
./gradlew assembleRelease
```

---

# Why This App?

Managing a crochet business using spreadsheets or notebooks becomes difficult as inventory and customer orders grow.

This application centralizes:

- Products
- Categories
- Inventory
- Orders
- Business Information

into a single offline Android application.

---

# Recent Updates

### New Features

- ✅ Complete Order Management module
- ✅ Order Details screen
- ✅ Order Item management
- ✅ Business Profile support
- ✅ Image Viewer
- ✅ Inventory image support
- ✅ Additional database entities
- ✅ Improved navigation
- ✅ Better UI components

### Bug Fixes

- Fixed inventory refresh issues
- Improved Room database operations
- Fixed navigation edge cases
- Improved image handling
- Enhanced RecyclerView performance
- General stability improvements
- UI consistency fixes
- Minor crash fixes

---

# License

This project is available for educational and personal use. Add your preferred open-source license before public release.

---

# Author

Developed as a native Android application to simplify inventory and order management for small crochet businesses.
