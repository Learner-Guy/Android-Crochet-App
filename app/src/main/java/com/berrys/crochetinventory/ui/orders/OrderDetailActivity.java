package com.berrys.crochetinventory.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.Order;
import com.berrys.crochetinventory.data.OrderItem;
import com.berrys.crochetinventory.data.OrderStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView ivSampleImage;
    private TextView tvCustomerName, tvPhone, tvEmail, tvDescription,
            tvEstimatedPrice, tvDiscount, tvEffectivePrice, tvGst, tvGrandTotal,
            tvOrderDate, tvDeliveryDate, tvNotes;
    private Chip chipStatus;
    private LinearLayout layoutDetails;
    private MaterialButton btnEdit, btnGenerateEstimate, btnGenerateBill;
    private RecyclerView rvOrderItems;
    private OrderItemAdapter orderItemAdapter;

    private AppDatabase db;
    private int orderId = -1;
    private Order currentOrder;
    private List<OrderItem> orderItems = new ArrayList<>();
    private boolean detailsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Details");
        }

        db = AppDatabase.getInstance(this);
        orderId = getIntent().getIntExtra("order_id", -1);

        initViews();
        loadOrder();
    }

    private void initViews() {
        ivSampleImage = findViewById(R.id.iv_sample_image);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvDescription = findViewById(R.id.tv_description);
        tvEstimatedPrice = findViewById(R.id.tv_estimated_price);
        tvDiscount = findViewById(R.id.tv_discount);
        tvEffectivePrice = findViewById(R.id.tv_effective_price);
        tvGst = findViewById(R.id.tv_gst);
        tvGrandTotal = findViewById(R.id.tv_grand_total);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvDeliveryDate = findViewById(R.id.tv_delivery_date);
        tvNotes = findViewById(R.id.tv_notes);
        chipStatus = findViewById(R.id.chip_status);
        layoutDetails = findViewById(R.id.layout_details);
        btnEdit = findViewById(R.id.btn_edit_order);
        btnGenerateEstimate = findViewById(R.id.btn_generate_estimate);
        btnGenerateBill = findViewById(R.id.btn_generate_final_bill);
        rvOrderItems = findViewById(R.id.rv_order_items);

        // Image click - full screen
        ivSampleImage.setOnClickListener(v -> {
            if (currentOrder != null && currentOrder.getSampleImagePath() != null
                    && !currentOrder.getSampleImagePath().isEmpty()) {
                Intent intent = new Intent(this, ImageViewerActivity.class);
                intent.putExtra("image_path", currentOrder.getSampleImagePath());
                startActivity(intent);
            }
        });

        // Middle section click - expand/collapse details
        findViewById(R.id.card_summary).setOnClickListener(v -> {
            detailsExpanded = !detailsExpanded;
            layoutDetails.setVisibility(detailsExpanded ? View.VISIBLE : View.GONE);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditOrderActivity.class);
            intent.putExtra("order_id", orderId);
            startActivity(intent);
        });

        btnGenerateEstimate.setOnClickListener(v -> {
            generateEstimateBill();
        });

        btnGenerateBill.setOnClickListener(v -> {
            generateFinalBill();
        });

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        orderItemAdapter = new OrderItemAdapter(orderItems, null);
        rvOrderItems.setAdapter(orderItemAdapter);
    }

    private void loadOrder() {
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            currentOrder = db.orderDao().getOrderById(orderId);
            List<OrderItem> items = db.orderDao().getOrderItemsSync(orderId);

            runOnUiThread(() -> {
                if (currentOrder == null) {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                tvCustomerName.setText(currentOrder.getCustomerName());
                tvPhone.setText(currentOrder.getCustomerPhone() != null ? currentOrder.getCustomerPhone() : "-");
                tvEmail.setText(currentOrder.getCustomerEmail() != null ? currentOrder.getCustomerEmail() : "-");
                tvDescription.setText(currentOrder.getDescription());
                tvNotes.setText(currentOrder.getNotes() != null ? currentOrder.getNotes() : "-");

                NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                tvEstimatedPrice.setText("Estimated: " + currency.format(currentOrder.getEstimatedPrice()));
                tvDiscount.setText("Discount (" + currentOrder.getDiscountPercent() + "%): " + currency.format(currentOrder.getDiscountAmount()));
                tvEffectivePrice.setText("Effective: " + currency.format(currentOrder.getEffectivePrice()));
                tvGst.setText("GST (" + currentOrder.getGstPercent() + "%): " + currency.format(currentOrder.getTaxAmount()));
                tvGrandTotal.setText("Grand Total: " + currency.format(currentOrder.getGrandTotal()));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvOrderDate.setText("Order: " + (currentOrder.getOrderDate() != null ? sdf.format(currentOrder.getOrderDate()) : "-"));
                tvDeliveryDate.setText("Delivery: " + (currentOrder.getDeliveryDate() != null ? sdf.format(currentOrder.getDeliveryDate()) : "-"));

                OrderStatus status = OrderStatus.fromName(currentOrder.getStatus());
                chipStatus.setText(status.getDisplayName());
                chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor(status.getColorHex())));

                // Load image
                String imagePath = currentOrder.getSampleImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Glide.with(this).load(imgFile).into(ivSampleImage);
                    }
                }

                // Load order items
                orderItems.clear();
                if (items != null) {
                    orderItems.addAll(items);
                }
                orderItemAdapter.notifyDataSetChanged();

                // Show/hide bill buttons based on status
                btnGenerateEstimate.setVisibility(
                        currentOrder.isEstimateGenerated() ? View.GONE : View.VISIBLE);
                btnGenerateBill.setVisibility(
                        (currentOrder.getStatus().equals(OrderStatus.COMPLETED.name())
                                || currentOrder.getStatus().equals(OrderStatus.READY_TO_DELIVER.name())
                                || currentOrder.getStatus().equals(OrderStatus.DELIVERED.name()))
                                && !currentOrder.isFinalBillGenerated() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    private void generateEstimateBill() {
        // TODO: Generate estimate PDF
        Toast.makeText(this, "Generating estimate bill...", Toast.LENGTH_SHORT).show();
    }

    private void generateFinalBill() {
        // TODO: Generate final bill PDF
        Toast.makeText(this, "Generating final bill...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder(); // Refresh when returning from edit
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
