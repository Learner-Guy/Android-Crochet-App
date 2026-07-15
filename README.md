# Crochet Inventory

A lightweight Android application for managing crochet inventory, categories, and low-stock items. Designed for small crochet businesses and hobbyists who need a simple offline inventory management solution.

---

## Features

- Dashboard with inventory statistics
- Add, edit and delete inventory items
- Category management
- Low stock tracking
- Search inventory
- Filter inventory by category
- Material Design UI
- Offline storage using Room Database
- Fast RecyclerView-based lists

---

## Screens

- Dashboard
- Inventory
- Categories
- Low Stock

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java | Application development |
| Android SDK | Native Android development |
| Room Database | Local database |
| LiveData | Reactive UI updates |
| Navigation Component | Screen navigation |
| RecyclerView | Item listing |
| View Binding | View access |
| Material Design Components | UI |

---

## Requirements

- Android Studio Hedgehog or newer
- JDK 8+
- Android SDK 34
- Minimum Android Version: API 24 (Android 7.0)

---

## Project Structure

```
app/
 ├── data/
 │    ├── AppDatabase
 │    ├── InventoryDao
 │    ├── InventoryItem
 │    ├── Category
 │    └── DateConverter
 │
 ├── ui/
 │    ├── dashboard/
 │    ├── inventory/
 │    ├── categories/
 │    ├── lowstock/
 │    └── components/
 │
 ├── MainActivity.java
 └── AndroidManifest.xml
```

---

## Database

The application uses Room Database for offline persistence.

### Main Entities

### InventoryItem

Stores inventory information including:

- Item Name
- Category
- Quantity
- Low Stock Threshold
- Notes
- Image (optional)
- Created Date

### Category

Stores user-defined inventory categories with customizable icons.

---

## Application Flow

```
Dashboard
      │
      ├── Inventory
      │      ├── Add Item
      │      ├── Edit Item
      │      ├── Delete Item
      │      └── Search / Filter
      │
      ├── Categories
      │      ├── Create
      │      ├── Edit
      │      └── Select Icon
      │
      └── Low Stock
             └── View & Update Items
```

---

## Dependencies

- AndroidX AppCompat
- Material Components
- RecyclerView
- ConstraintLayout
- Navigation Component
- Room Database
- Lifecycle ViewModel
- LiveData
- Glide

---

## Build

Clone the repository:

```bash
git clone <repository-url>
```

Open the project in Android Studio.

Build the APK:

```
Build
 └── Generate APK(s)
```

or

```bash
./gradlew assembleDebug
```

---

## APK Output

The generated APK is named:

```
CrochetInventory.apk
```

---

## Current Capabilities

- Offline inventory management
- Dashboard statistics
- Category-based organization
- Search functionality
- Low stock monitoring
- Material Design interface

---

## Future Enhancements

- Barcode/QR code scanning
- Cloud backup and synchronization
- Inventory export (CSV/PDF)
- Image compression
- Dark mode
- Sales tracking
- Customer management
- Order management
- Expense tracking
- Analytics and reports

---

## License

This project is intended for learning and personal/business use. Update this section with your preferred license before public release.

---

## Author

Developed as an Android inventory management application for crochet businesses.
