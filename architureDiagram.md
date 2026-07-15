# Architecture Diagram

# Crochet Inventory App Architecture

```text
                           +----------------------+
                           |      User            |
                           +----------+-----------+
                                      |
                                      |
                           Material UI Screens
                                      |
        --------------------------------------------------------
        |             |              |              |
        |             |              |              |
+---------------+ +---------------+ +---------------+ +----------------+
| Dashboard     | | Inventory     | | Categories    | | Low Stock      |
| Fragment      | | Fragment      | | Fragment      | | Fragment       |
+-------+-------+ +-------+-------+ +-------+-------+ +--------+-------+
        |                 |                 |                  |
        --------------------------------------------------------
                          |
                          |
                  MainActivity / Navigation
                          |
                          |
                ViewModel (Lifecycle Aware)
                          |
                          |
                  Repository Layer (Optional)
                          |
                          |
                     Room Database
                          |
          ---------------------------------
          |                               |
          |                               |
     InventoryDao                  CategoryDao
          |                               |
          |                               |
     InventoryItem Entity          Category Entity
          |                               |
          -----------+---------------------
                      |
                 SQLite Database
                      |
             Local Device Storage
```

---

# Layered Architecture

```text
+---------------------------------------------------------+
|                     Presentation Layer                  |
|---------------------------------------------------------|
| Activities                                              |
| Fragments                                               |
| RecyclerViews                                           |
| Adapters                                                |
| Material Components                                     |
+---------------------------------------------------------+

                        │
                        ▼

+---------------------------------------------------------+
|                     ViewModel Layer                     |
|---------------------------------------------------------|
| DashboardViewModel                                      |
| InventoryViewModel                                      |
| CategoryViewModel                                       |
| LowStockViewModel                                       |
+---------------------------------------------------------+

                        │
                        ▼

+---------------------------------------------------------+
|                    Repository Layer                     |
|---------------------------------------------------------|
| Handles communication between UI and database           |
+---------------------------------------------------------+

                        │
                        ▼

+---------------------------------------------------------+
|                     Data Access Layer                   |
|---------------------------------------------------------|
| InventoryDao                                            |
| CategoryDao                                             |
+---------------------------------------------------------+

                        │
                        ▼

+---------------------------------------------------------+
|                     Database Layer                      |
|---------------------------------------------------------|
| Room Database                                           |
| InventoryItem Entity                                    |
| Category Entity                                         |
| DateConverter                                           |
+---------------------------------------------------------+

                        │
                        ▼

+---------------------------------------------------------+
|                  SQLite Local Storage                   |
+---------------------------------------------------------+
```

---

# Inventory Flow

```text
User
 │
 ▼
Inventory Screen
 │
 ▼
Inventory ViewModel
 │
 ▼
Repository
 │
 ▼
InventoryDao
 │
 ▼
Room Database
 │
 ▼
SQLite
 │
 ▼
Updated LiveData
 │
 ▼
RecyclerView refreshes automatically
```

---

# Category Flow

```text
User
 │
 ▼
Category Screen
 │
 ▼
Category ViewModel
 │
 ▼
Repository
 │
 ▼
CategoryDao
 │
 ▼
Room Database
 │
 ▼
SQLite
 │
 ▼
Updated Category List
```

---

# Low Stock Detection

```text
Inventory Item
       │
       ▼
Quantity <= Threshold ?
       │
   Yes │ No
       │
       ▼
Low Stock DAO Query
       │
       ▼
Low Stock Fragment
       │
       ▼
Displayed to User
```

---

# Dashboard Statistics

```text
InventoryDao
       │
       ├── Total Items
       ├── Total Categories
       ├── Low Stock Count
       └── Inventory Value (Future)
                 │
                 ▼
         Dashboard ViewModel
                 │
                 ▼
          Dashboard Fragment
```

---

# Package Structure

```text
com.crochet.inventory
│
├── data
│   ├── AppDatabase
│   ├── dao
│   │     ├── InventoryDao
│   │     └── CategoryDao
│   ├── entity
│   │     ├── InventoryItem
│   │     └── Category
│   └── converter
│         └── DateConverter
│
├── repository
│
├── ui
│   ├── dashboard
│   ├── inventory
│   ├── categories
│   ├── lowstock
│   ├── adapter
│   └── dialog
│
├── viewmodel
│
├── util
│
└── MainActivity
```

---

# Technology Stack

| Layer | Technology |
|--------|------------|
| UI | Material Design, RecyclerView |
| Language | Java |
| Architecture | MVVM |
| Navigation | Navigation Component |
| Persistence | Room Database |
| Local Storage | SQLite |
| Lifecycle | LiveData, ViewModel |
| Build Tool | Gradle |

---

# Architecture Principles

- MVVM (Model–View–ViewModel)
- Single Responsibility Principle
- Offline-first design
- Lifecycle-aware components
- Local persistence using Room
- Modular feature-based package structure
- Material Design UI
- Easy extensibility for future cloud synchronization
