package com.berrys.crochetinventory.ui.inventory;

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

import java.util.ArrayList;
import java.util.Date;

import com.berrys.crochetinventory.data.IconPack;
import com.berrys.crochetinventory.ui.components.IconPickerDialog;
import com.bumptech.glide.Glide;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddEditItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayAdapter<String> categoryAdapter;
    private TextInputEditText etName, etColor, etQuantity, etUnit, etCost,
            etSupplier, etLowStock, etNotes;
    private AutoCompleteTextView actCategory;
    private ImageView ivPreview;
    private MaterialButton btnSave, btnSelectImage;
    private ImageView ivSelectedIcon;
    private String selectedIconName = IconPack.getDefaultItemIcon();

    private AppDatabase db;
    private String imagePath = "";
    private int itemId = -1;


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


        // Icon picker
        ivSelectedIcon = findViewById(R.id.iv_selected_icon);
        MaterialButton btnPickIcon = findViewById(R.id.btn_pick_icon);

        btnPickIcon.setOnClickListener(v -> {
            IconPickerDialog dialog = IconPickerDialog.newItemPicker(selectedIconName, iconName -> {
                selectedIconName = iconName;
                int resId = IconPack.getIconResourceId(this, iconName);
                ivSelectedIcon.setImageResource(resId != 0 ? resId : R.drawable.ic_inventory);
            });
            dialog.show(getSupportFragmentManager(), "icon_picker");
        });

        // Pre-selected category from CategoriesFragment
        String preselectedCategory = getIntent().getStringExtra("preselected_category");
        if (preselectedCategory != null && !preselectedCategory.isEmpty()) {
            actCategory.setText(preselectedCategory, false);
        }

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
        categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        actCategory.setAdapter(categoryAdapter);

        // Load categories from database
        db.inventoryDao().getCategoryNames().observe(this, names -> {
            categoryAdapter.clear();
            if (names != null) {
                categoryAdapter.addAll(names);
            }
            categoryAdapter.notifyDataSetChanged();
        });
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
                String savedPath = saveImageToInternalStorage(uri);
                if (savedPath != null) {
                    imagePath = savedPath;
                    Glide.with(this).load(new File(imagePath)).into(ivPreview);
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            File directory = new File(getFilesDir(), "item_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "item_" + System.currentTimeMillis() + ".jpg";
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

    private void loadItem(int id) {
        new Thread(() -> {
            final InventoryItem item = db.inventoryDao().getItemById(id);

            if (item != null) {
                selectedIconName = item.getIconName() != null ? item.getIconName() : IconPack.getDefaultItemIcon();

                runOnUiThread(() -> {
                    etName.setText(item.getName());
                    actCategory.setText(item.getCategory(), false);
                    etColor.setText(item.getColor() != null ? item.getColor() : "");
                    etQuantity.setText(String.valueOf(item.getQuantity()));
                    etUnit.setText(item.getUnit() != null ? item.getUnit() : "");
                    etCost.setText(String.valueOf(item.getCost()));
                    etSupplier.setText(item.getSupplier() != null ? item.getSupplier() : "");
                    etLowStock.setText(String.valueOf(item.getLowStock()));
                    etNotes.setText(item.getNotes() != null ? item.getNotes() : "");
                    imagePath = item.getImagePath();

                    // Load image preview
                    if (imagePath != null && !imagePath.isEmpty()) {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Glide.with(AddEditItemActivity.this).load(imgFile).into(ivPreview);
                        }
                    }

                    // Load icon
                    int iconRes = IconPack.getIconResourceId(AddEditItemActivity.this, selectedIconName);
                    ivSelectedIcon.setImageResource(iconRes != 0 ? iconRes : R.drawable.ic_inventory);
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
                name, category, color, quantity,
                unit.isEmpty() ? "pcs" : unit,
                cost, supplier, lowStock, notes,
                imagePath, selectedIconName, new Date()
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