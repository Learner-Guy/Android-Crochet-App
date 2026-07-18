package com.berrys.crochetinventory.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OrderDao {
    // Order CRUD
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    LiveData<List<Order>> getAllOrders();

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY orderDate DESC")
    LiveData<List<Order>> getOrdersByStatus(String status);

    @Query("SELECT * FROM orders WHERE status != 'CANCELLED' AND status != 'REFUNDED' ORDER BY orderDate DESC")
    LiveData<List<Order>> getActiveOrders();

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    Order getOrderById(int orderId);

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    LiveData<Order> getOrderByIdLive(int orderId);

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    LiveData<Integer> getOrderCountByStatus(String status);

    // OrderItem CRUD
    @Insert
    void insertOrderItem(OrderItem orderItem);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    @Update
    void updateOrderItem(OrderItem orderItem);

    @Delete
    void deleteOrderItem(OrderItem orderItem);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    LiveData<List<OrderItem>> getOrderItems(int orderId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsSync(int orderId);

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    void deleteOrderItemsByOrderId(int orderId);

    // Business Profile
    @Insert
    void insertBusinessProfile(BusinessProfile profile);

    @Update
    void updateBusinessProfile(BusinessProfile profile);

    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    BusinessProfile getBusinessProfile();

    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    LiveData<BusinessProfile> getBusinessProfileLive();

    @Query("SELECT COUNT(*) FROM business_profile WHERE id = 1")
    int hasBusinessProfile();
}
