package com.berrys.crochetinventory.ui.inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            etSupplier, etLowStock, etNotes, etSizeValue;
    private AutoCompleteTextView actCategory, actItemType, actSizeUnit;
    private LinearLayout layoutSize;
    private ImageView ivPreview;
    private MaterialButton btnSave, btnSelectImage, btnRemoveImage;
    private ImageView ivSelectedIcon;
    private String selectedIconName = IconPack.getDefaultItemIcon();

    private AppDatabase db;
    private String imagePath = "";
    private String oldImagePath = "";
    private int itemId = -1;

    private static final String[] ITEM_TYPES = {"DISCRETE", "CONTINUOUS"};
    private static final String[] SIZE_UNITS = {"mm", "cm", "m", "g", "kg", "yards", "inches", "feet"};

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
        setupItemTypeDropdown();
        setupSizeUnitDropdown();

        // Icon picker
        ivSelectedIcon = findViewById(R.id.iv_selected_icon);
        MaterialButton btnPickIcon = findViewById(R.id.btn_pick_icon);

        btnPickIcon.setOnClickListener(v -> {
            IconPickerDialog dialog = new IconPickerDialog();
            dialog.setIcons(IconPack.ITEM_ICONS);
            dialog.setCurrentIcon(selectedIconName);
            dialog.setListener(iconName -> {
                selectedIconName = iconName;
                int resId = IconPack.getIconResourceId(this, iconName);
                ivSelectedIcon.setImageResource(resId != 0 ? resId : R.drawable.ic_inventory);
            });
            dialog.show(getSupportFragmentManager(), "item_icon_picker");
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
            actItemType.setText("DISCRETE", false);
        }

        btnSelectImage.setOnClickListener(v -> pickImage());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnSave.setOnClickListener(v -> saveItem());
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        actCategory = findViewById(R.id.act_category);
        actItemType = findViewById(R.id.act_item_type);
        layoutSize = findViewById(R.id.layout_size);
        etSizeValue = findViewById(R.id.et_size_value);
        actSizeUnit = findViewById(R.id.act_size_unit);
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
        btnRemoveImage = findViewById(R.id.btn_remove_image);
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

    private void setupItemTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ITEM_TYPES);
        actItemType.setAdapter(adapter);
        actItemType.setOnItemClickListener((parent, view, position, id) -> {
            String selected = ITEM_TYPES[position];
            if ("CONTINUOUS".equals(selected)) {
                layoutSize.setVisibility(View.VISIBLE);
            } else {
                layoutSize.setVisibility(View.GONE);
                etSizeValue.setText("");
                actSizeUnit.setText("", false);
            }
        });
    }

    private void setupSizeUnitDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SIZE_UNITS);
        actSizeUnit.setAdapter(adapter);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void removeImage() {
        // Mark old image for deletion on save
        if (imagePath != null && !imagePath.isEmpty()) {
            oldImagePath = imagePath;
        }
        imagePath = "";
        ivPreview.setImageResource(R.drawable.ic_inventory);
        btnRemoveImage.setVisibility(View.GONE);
        btnSelectImage.setText("Select Image");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String savedPath = saveImageToInternalStorage(uri);
                if (savedPath != null) {
                    // Delete previous image if it exists and is different
                    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals(savedPath)) {
                        deleteImageFile(imagePath);
                    }
                    imagePath = savedPath;
                    Glide.with(this).load(new File(imagePath)).into(ivPreview);
                    btnRemoveImage.setVisibility(View.VISIBLE);
                    btnSelectImage.setText("Change Image");
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

    private void deleteImageFile(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void loadItem(int id) {
        new Thread(() -> {
            final InventoryItem item = db.inventoryDao().getItemById(id);

            if (item != null) {
                selectedIconName = item.getIconName() != null ? item.getIconName() : IconPack.getDefaultItemIcon();
                imagePath = item.getImagePath() != null ? item.getImagePath() : "";
                oldImagePath = imagePath;

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

                    // Load item type and size
                    String itemType = item.getItemType() != null ? item.getItemType() : "DISCRETE";
                    actItemType.setText(itemType, false);
                    if ("CONTINUOUS".equals(itemType)) {
                        layoutSize.setVisibility(View.VISIBLE);
                        etSizeValue.setText(String.valueOf(item.getSizeValue()));
                        actSizeUnit.setText(item.getSizeUnit() != null ? item.getSizeUnit() : "", false);
                    } else {
                        layoutSize.setVisibility(View.GONE);
                    }

                    // Load image preview
                    if (imagePath != null && !imagePath.isEmpty()) {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Glide.with(AddEditItemActivity.this).load(imgFile).into(ivPreview);
                            btnRemoveImage.setVisibility(View.VISIBLE);
                            btnSelectImage.setText("Change Image");
                        } else {
                            imagePath = "";
                            btnRemoveImage.setVisibility(View.GONE);
                            btnSelectImage.setText("Select Image");
                        }
                    } else {
                        btnRemoveImage.setVisibility(View.GONE);
                        btnSelectImage.setText("Select Image");
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
        String itemType = actItemType.getText().toString().trim();

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

        // Set size fields
        item.setItemType(itemType.isEmpty() ? "DISCRETE" : itemType);
        if ("CONTINUOUS".equals(item.getItemType())) {
            String sizeValueStr = etSizeValue.getText().toString().trim();
            double sizeValue = sizeValueStr.isEmpty() ? 0 : Double.parseDouble(sizeValueStr);
            item.setSizeValue(sizeValue);
            item.setSizeUnit(actSizeUnit.getText() != null ? actSizeUnit.getText().toString().trim() : "");
        } else {
            item.setSizeValue(0);
            item.setSizeUnit("");
        }

        new Thread(() -> {
            if (itemId != -1) {
                item.setId(itemId);
                db.inventoryDao().update(item);

                // Delete old image if it was removed
                if (oldImagePath != null && !oldImagePath.isEmpty() && 
                    (imagePath == null || imagePath.isEmpty() || !imagePath.equals(oldImagePath))) {
                    deleteImageFile(oldImagePath);
                }
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
