package com.example.crochetinventory.ui.inventory;

import android.app.Activity;
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
import com.bumptech.glide.Glide;
import com.example.crochetinventory.R;
import com.example.crochetinventory.data.AppDatabase;
import com.example.crochetinventory.data.InventoryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Date;

public class AddEditItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText etName, etColor, etQuantity, etUnit, etCost,
            etSupplier, etLowStock, etNotes;
    private AutoCompleteTextView actCategory;
    private ImageView ivPreview;
    private MaterialButton btnSave, btnSelectImage;

    private AppDatabase db;
    private String imagePath = "";
    private int itemId = -1;

    private final String[] categories = {"Yarn", "Hooks", "Stuffing", "Safety Eyes",
            "Buttons", "Beads", "Packaging", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);
        initViews();
        setupCategoryDropdown();

        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) {
            setTitle("Edit Item");
            loadItem(itemId);
        } else {
            setTitle("Add Item");
        }

        btnSelectImage.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> saveItem());
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        actCategory = findViewById(R.id.act_category);
        etColor = findViewById(R.id.et_color);
        etQuantity = findViewById(R.id.et_quantity);
        etUnit = findViewById(R.id.et_unit);
        etCost = findViewById(R.id.et_cost);
        etSupplier = findViewById(R.id.et_supplier);
        etLowStock = findViewById(R.id.et_low_stock);
        etNotes = findViewById(R.id.et_notes);
        ivPreview = findViewById(R.id.iv_preview);
        btnSave = findViewById(R.id.btn_save);
        btnSelectImage = findViewById(R.id.btn_select_image);
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(adapter);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                imagePath = uri.toString();
                Glide.with(this).load(uri).into(ivPreview);
            }
        }
    }

    private void loadItem(int id) {
        new Thread(() -> {
            InventoryItem item = db.inventoryDao().getAllItems().getValue() != null ?
                    db.inventoryDao().getAllItems().getValue().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElse(null) : null;

            if (item != null) {
                final InventoryItem finalItem = item;
                runOnUiThread(() -> {
                    etName.setText(finalItem.getName());
                    actCategory.setText(finalItem.getCategory(), false);
                    etColor.setText(finalItem.getColor());
                    etQuantity.setText(String.valueOf(finalItem.getQuantity()));
                    etUnit.setText(finalItem.getUnit());
                    etCost.setText(String.valueOf(finalItem.getCost()));
                    etSupplier.setText(finalItem.getSupplier());
                    etLowStock.setText(String.valueOf(finalItem.getLowStock()));
                    etNotes.setText(finalItem.getNotes());
                    imagePath = finalItem.getImagePath();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        Glide.with(this).load(imagePath).into(ivPreview);
                    }
                });
            }
        }).start();
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String category = actCategory.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();
        String supplier = etSupplier.getText().toString().trim();
        String lowStockStr = etLowStock.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Name, Category, and Quantity are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double cost = costStr.isEmpty() ? 0.0 : Double.parseDouble(costStr);
        int lowStock = lowStockStr.isEmpty() ? 5 : Integer.parseInt(lowStockStr);

        InventoryItem item = new InventoryItem(
                name, category, color, quantity, unit.isEmpty() ? "pcs" : unit,
                cost, supplier, lowStock, notes, imagePath, new Date()
        );

        new Thread(() -> {
            if (itemId != -1) {
                item.setId(itemId);
                db.inventoryDao().update(item);
            } else {
                db.inventoryDao().insert(item);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
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