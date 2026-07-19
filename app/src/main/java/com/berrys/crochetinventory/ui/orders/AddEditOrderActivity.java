package com.berrys.crochetinventory.ui.orders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.berrys.crochetinventory.data.Order;
import com.berrys.crochetinventory.data.OrderItem;
import com.berrys.crochetinventory.data.OrderStatus;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditOrderActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText etCustomerName, etCustomerPhone, etCustomerEmail,
            etDescription, etEstimatedPrice, etDiscountPercent, etGstPercent,
            etNotes;
    private AutoCompleteTextView actStatus;
    private ImageView ivSampleImage;
    private MaterialButton btnSelectImage, btnRemoveImage, btnSave, btnPickDate, btnAddItem, btnCancel;
    private TextInputEditText etDeliveryDate;

    private AppDatabase db;
    private String sampleImagePath = "";
    private String oldImagePath = "";
    private int orderId = -1;
    private Date deliveryDate;

    private RecyclerView rvOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);
        initViews();
        setupStatusDropdown();
        setupOrderItemsRecycler();

        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId != -1) {
            setTitle("Edit Order");
            loadOrder(orderId);
        } else {
            setTitle("New Order");
            deliveryDate = new Date();
            updateDeliveryDateDisplay();
        }

        btnSelectImage.setOnClickListener(v -> pickImage());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnAddItem.setOnClickListener(v -> showAddItemDialog());
        btnSave.setOnClickListener(v -> saveOrder());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etCustomerName = findViewById(R.id.et_customer_name);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etCustomerEmail = findViewById(R.id.et_customer_email);
        etDescription = findViewById(R.id.et_description);
        etEstimatedPrice = findViewById(R.id.et_estimated_price);
        etDiscountPercent = findViewById(R.id.et_discount_percent);
        etGstPercent = findViewById(R.id.et_gst_percent);
        etNotes = findViewById(R.id.et_notes);
        actStatus = findViewById(R.id.act_status);
        ivSampleImage = findViewById(R.id.iv_sample_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnRemoveImage = findViewById(R.id.btn_remove_image);
        btnSave = findViewById(R.id.btn_save_order);
        btnPickDate = findViewById(R.id.btn_pick_date);
        etDeliveryDate = findViewById(R.id.et_delivery_date);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnAddItem = findViewById(R.id.btn_add_item);
        btnCancel = findViewById(R.id.btn_cancel_order);
    }

    private void setupStatusDropdown() {
        String[] statuses = new String[OrderStatus.values().length];
        for (int i = 0; i < OrderStatus.values().length; i++) {
            statuses[i] = OrderStatus.values()[i].getDisplayName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statuses);
        actStatus.setAdapter(adapter);
        actStatus.setText(OrderStatus.PENDING_CONFIRMATION.getDisplayName(), false);
    }

    private void setupOrderItemsRecycler() {
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        orderItemAdapter = new OrderItemAdapter(orderItems, position -> {
            orderItems.remove(position);
            orderItemAdapter.notifyDataSetChanged();
        });
        rvOrderItems.setAdapter(orderItemAdapter);
    }

    private void showAddItemDialog() {
        AddOrderItemDialog dialog = new AddOrderItemDialog();
        dialog.setDatabase(db);
        dialog.setListener((inventoryItem, quantity, sizeUsed) -> {
            OrderItem item = new OrderItem(
                -1,
                inventoryItem.getId(),
                inventoryItem.getName(),
                quantity,
                inventoryItem.getCost(),
                ""
            );
            item.setSizeUsed(sizeUsed);
            item.setSizeUnit(inventoryItem.getSizeUnit());
            orderItems.add(item);
            orderItemAdapter.notifyDataSetChanged();
        });
        dialog.show(getSupportFragmentManager(), "add_order_item");
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (deliveryDate != null) {
            cal.setTime(deliveryDate);
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            deliveryDate = cal.getTime();
            updateDeliveryDateDisplay();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDeliveryDateDisplay() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        etDeliveryDate.setText(sdf.format(deliveryDate));
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void removeImage() {
        if (sampleImagePath != null && !sampleImagePath.isEmpty()) {
            oldImagePath = sampleImagePath;
        }
        sampleImagePath = "";
        ivSampleImage.setImageResource(R.drawable.ic_inventory);
        btnRemoveImage.setVisibility(android.view.View.GONE);
        btnSelectImage.setText("Select Sample Image");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String savedPath = saveImageToInternalStorage(uri);
                if (savedPath != null) {
                    if (sampleImagePath != null && !sampleImagePath.isEmpty() && !sampleImagePath.equals(savedPath)) {
                        deleteImageFile(sampleImagePath);
                    }
                    sampleImagePath = savedPath;
                    Glide.with(this).load(new File(sampleImagePath)).into(ivSampleImage);
                    btnRemoveImage.setVisibility(android.view.View.VISIBLE);
                    btnSelectImage.setText("Change Image");
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            File directory = new File(getFilesDir(), "order_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = "order_" + System.currentTimeMillis() + ".jpg";
            File destFile = new File(directory, fileName);
            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 FileOutputStream out = new FileOutputStream(destFile)) {
                if (in == null) return null;
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteImageFile(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) file.delete();
        }
    }

    private void loadOrder(int id) {
        new Thread(() -> {
            final Order order = db.orderDao().getOrderById(id);
            if (order != null) {
                runOnUiThread(() -> {
                    etCustomerName.setText(order.getCustomerName());
                    etCustomerPhone.setText(order.getCustomerPhone() != null ? order.getCustomerPhone() : "");
                    etCustomerEmail.setText(order.getCustomerEmail() != null ? order.getCustomerEmail() : "");
                    etDescription.setText(order.getDescription() != null ? order.getDescription() : "");
                    etEstimatedPrice.setText(String.valueOf(order.getEstimatedPrice()));
                    etDiscountPercent.setText(String.valueOf(order.getDiscountPercent()));
                    etGstPercent.setText(String.valueOf(order.getGstPercent()));
                    etNotes.setText(order.getNotes() != null ? order.getNotes() : "");

                    OrderStatus status = OrderStatus.fromName(order.getStatus());
                    actStatus.setText(status.getDisplayName(), false);

                    deliveryDate = order.getDeliveryDate();
                    if (deliveryDate != null) {
                        updateDeliveryDateDisplay();
                    }

                    sampleImagePath = order.getSampleImagePath() != null ? order.getSampleImagePath() : "";
                    if (!sampleImagePath.isEmpty()) {
                        File imgFile = new File(sampleImagePath);
                        if (imgFile.exists()) {
                            Glide.with(this).load(imgFile).into(ivSampleImage);
                            btnRemoveImage.setVisibility(android.view.View.VISIBLE);
                            btnSelectImage.setText("Change Image");
                        }
                    }
                });

                List<OrderItem> items = db.orderDao().getOrderItemsSync(id);
                runOnUiThread(() -> {
                    orderItems.clear();
                    if (items != null) {
                        orderItems.addAll(items);
                    }
                    orderItemAdapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void saveOrder() {
        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String estimatedPriceStr = etEstimatedPrice.getText().toString().trim();
        String discountStr = etDiscountPercent.getText().toString().trim();
        String gstStr = etGstPercent.getText().toString().trim();

        if (customerName.isEmpty() || description.isEmpty() || estimatedPriceStr.isEmpty()) {
            Toast.makeText(this, "Customer name, description, and estimated price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double estimatedPrice = Double.parseDouble(estimatedPriceStr);
        double discountPercent = discountStr.isEmpty() ? 0 : Double.parseDouble(discountStr);
        double gstPercent = gstStr.isEmpty() ? 0 : Double.parseDouble(gstStr);

        double discountAmount = estimatedPrice * discountPercent / 100;
        double effectivePrice = estimatedPrice - discountAmount;
        double taxAmount = effectivePrice * gstPercent / 100;
        double grandTotal = effectivePrice + taxAmount;

        String statusDisplay = actStatus.getText().toString();
        String statusName = OrderStatus.PENDING_CONFIRMATION.name();
        for (OrderStatus s : OrderStatus.values()) {
            if (s.getDisplayName().equals(statusDisplay)) {
                statusName = s.name();
                break;
            }
        }

        Order order = new Order(
                customerName, customerPhone,
                etCustomerEmail.getText().toString().trim(),
                description, estimatedPrice,
                new Date(), deliveryDate,
                etNotes.getText().toString().trim()
        );
        order.setDiscountPercent(discountPercent);
        order.setDiscountAmount(discountAmount);
        order.setEffectivePrice(effectivePrice);
        order.setGstApplicable(gstPercent > 0);
        order.setGstPercent(gstPercent);
        order.setTaxAmount(taxAmount);
        order.setGrandTotal(grandTotal);
        order.setStatus(statusName);
        order.setSampleImagePath(sampleImagePath);

        new Thread(() -> {
            long newOrderId;
            if (orderId != -1) {
                order.setId(orderId);
                db.orderDao().update(order);
                newOrderId = orderId;
                if (oldImagePath != null && !oldImagePath.isEmpty() &&
                    (sampleImagePath == null || sampleImagePath.isEmpty() || !sampleImagePath.equals(oldImagePath))) {
                    deleteImageFile(oldImagePath);
                }
                db.orderDao().deleteOrderItemsByOrderId(orderId);
            } else {
                newOrderId = db.orderDao().insert(order);
            }

            // Save order items and deduct inventory
            for (OrderItem item : orderItems) {
                item.setOrderId((int) newOrderId);
                db.orderDao().insertOrderItem(item);

                // Deduct from inventory
                if (item.getInventoryItemId() != -1) {
                    InventoryItem invItem = db.inventoryDao().getItemById(item.getInventoryItemId());
                    if (invItem != null) {
                        // Deduct quantity
                        invItem.setQuantity(invItem.getQuantity() - item.getQuantityUsed());

                        // For continuous items, also deduct size
                        if (invItem.isContinuous() && item.getSizeUsed() > 0) {
                            invItem.setSizeValue(invItem.getSizeValue() - item.getSizeUsed());
                            if (invItem.getSizeValue() <= 0) {
                                invItem.setSizeValue(0);
                                invItem.setQuantity(0);
                            }
                        }

                        db.inventoryDao().update(invItem);
                    }
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Order saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
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
