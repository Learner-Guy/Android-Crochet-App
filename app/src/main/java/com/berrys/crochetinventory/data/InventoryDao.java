package com.berrys.crochetinventory.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    void insert(InventoryItem item);

    @Update
    void update(InventoryItem item);

    @Delete
    void delete(InventoryItem item);

    @Query("SELECT * FROM inventory ORDER BY name ASC")
    LiveData<List<InventoryItem>> getAllItems();

    @Query("SELECT * FROM inventory WHERE category = :category ORDER BY name ASC")
    LiveData<List<InventoryItem>> getItemsByCategory(String category);

    @Query("SELECT * FROM inventory WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    LiveData<List<InventoryItem>> searchItems(String searchQuery);

    @Query("SELECT * FROM inventory WHERE quantity <= lowStock ORDER BY quantity ASC")
    LiveData<List<InventoryItem>> getLowStockItems();

    @Query("SELECT COUNT(*) FROM inventory")
    LiveData<Integer> getTotalItemCount();

    @Query("SELECT COUNT(*) FROM inventory WHERE quantity <= lowStock")
    LiveData<Integer> getLowStockCount();

    @Query("SELECT DISTINCT category FROM inventory")
    LiveData<List<String>> getAllCategories();

    // Category management
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategoriesList();

    @Query("SELECT name FROM categories ORDER BY name ASC")
    LiveData<List<String>> getCategoryNames();

    @Query("SELECT COUNT(*) FROM inventory WHERE category = :categoryName")
    int getItemCountForCategory(String categoryName);

    @Query("SELECT * FROM inventory WHERE id = :itemId LIMIT 1")
    InventoryItem getItemById(int itemId);

    @Update
    void updateCategory(Category category);
}