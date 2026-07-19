package com.berrys.crochetinventory.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {InventoryItem.class, Category.class, Order.class, OrderItem.class, BusinessProfile.class}, version = 5, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract InventoryDao inventoryDao();
    public abstract OrderDao orderDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "iconName TEXT)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE inventory ADD COLUMN iconName TEXT");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "customerName TEXT, " +
                    "customerPhone TEXT, " +
                    "customerEmail TEXT, " +
                    "description TEXT, " +
                    "estimatedPrice REAL NOT NULL DEFAULT 0, " +
                    "finalPrice REAL NOT NULL DEFAULT 0, " +
                    "discountPercent REAL NOT NULL DEFAULT 0, " +
                    "discountAmount REAL NOT NULL DEFAULT 0, " +
                    "effectivePrice REAL NOT NULL DEFAULT 0, " +
                    "isGstApplicable INTEGER NOT NULL DEFAULT 0, " +
                    "gstPercent REAL NOT NULL DEFAULT 0, " +
                    "taxAmount REAL NOT NULL DEFAULT 0, " +
                    "grandTotal REAL NOT NULL DEFAULT 0, " +
                    "status TEXT, " +
                    "sampleImagePath TEXT, " +
                    "orderDate INTEGER, " +
                    "deliveryDate INTEGER, " +
                    "completedDate INTEGER, " +
                    "cancellationReason TEXT, " +
                    "cancelledDate INTEGER, " +
                    "notes TEXT, " +
                    "isEstimateGenerated INTEGER NOT NULL DEFAULT 0, " +
                    "isFinalBillGenerated INTEGER NOT NULL DEFAULT 0, " +
                    "estimateBillPath TEXT, " +
                    "finalBillPath TEXT)");

            database.execSQL("CREATE TABLE order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "orderId INTEGER NOT NULL, " +
                    "inventoryItemId INTEGER NOT NULL DEFAULT -1, " +
                    "customItemName TEXT, " +
                    "quantityUsed INTEGER NOT NULL DEFAULT 0, " +
                    "unitPrice REAL NOT NULL DEFAULT 0, " +
                    "totalAmount REAL NOT NULL DEFAULT 0, " +
                    "notes TEXT, " +
                    "FOREIGN KEY(orderId) REFERENCES orders(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(inventoryItemId) REFERENCES inventory(id) ON DELETE SET NULL)");

            database.execSQL("CREATE INDEX index_order_items_orderId ON order_items(orderId)");
            database.execSQL("CREATE INDEX index_order_items_inventoryItemId ON order_items(inventoryItemId)");

            database.execSQL("CREATE TABLE business_profile (" +
                    "id INTEGER PRIMARY KEY NOT NULL, " +
                    "businessName TEXT, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "gstNumber TEXT, " +
                    "logoPath TEXT, " +
                    "termsAndConditions TEXT, " +
                    "billPrefix TEXT, " +
                    "lastBillNumber INTEGER NOT NULL DEFAULT 0, " +
                    "upiId TEXT)");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add size fields to inventory table
            database.execSQL("ALTER TABLE inventory ADD COLUMN itemType TEXT DEFAULT 'DISCRETE'");
            database.execSQL("ALTER TABLE inventory ADD COLUMN sizeValue REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE inventory ADD COLUMN sizeUnit TEXT DEFAULT ''");

            // Add sizeUsed to order_items
            database.execSQL("ALTER TABLE order_items ADD COLUMN sizeUsed REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE order_items ADD COLUMN sizeUnit TEXT DEFAULT ''");
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "crochet_inventory_db"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
