src/main/java/com/crochetapp/
│
├── config/                # Security & MongoDB configuration
│   ├── SecurityConfig.java
│   ├── MongoConfig.java
│
├── controller/            # REST API endpoints
│   ├── AuthController.java
│   ├── StockController.java
│   ├── OrderController.java
│   ├── BillController.java
│   ├── ImageController.java
│
├── service/               # Business logic
│   ├── AuthService.java
│   ├── StockService.java
│   ├── OrderService.java
│   ├── BillService.java
│   ├── ImageService.java
│   ├── CleanupScheduler.java
│
├── repository/            # MongoDB repositories
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── StockRepository.java
│   ├── OrderRepository.java
│   ├── BillRepository.java
│
├── model/                 # Domain models
│   ├── User.java
│   ├── Role.java
│   ├── Stock.java
│   ├── Order.java
│   ├── Bill.java
│   ├── Category.java
│
└── CrochetAppApplication.java  # Main entry point
