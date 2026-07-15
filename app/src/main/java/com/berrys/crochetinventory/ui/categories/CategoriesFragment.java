package com.berrys.crochetinventory.ui.categories;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.Category;
import com.berrys.crochetinventory.data.IconPack;
import com.berrys.crochetinventory.ui.components.IconPickerDialog;
import com.berrys.crochetinventory.ui.inventory.AddEditItemActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {
    private static final String PREFS_NAME = "crochet_prefs";
    private static final String KEY_ICONS_FIXED = "icons_fixed_v2";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase db;
    private boolean deleteMode = false;
    private String pendingIconName = IconPack.getDefaultCategoryIcon();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        db = AppDatabase.getInstance(requireContext());
        recyclerView = view.findViewById(R.id.recycler_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        loadCategories();

        FloatingActionButton fab = view.findViewById(R.id.fab_category_action);
        fab.setOnClickListener(v -> showAddOptionsMenu(fab));

        return view;
    }

    private void loadCategories() {
        db.inventoryDao().getAllCategoriesList().observe(getViewLifecycleOwner(),
                categories -> {
                    if (categories == null || categories.isEmpty()) {
                        seedDefaultCategories();
                    } else {
                        SharedPreferences prefs = requireContext()
                                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        if (!prefs.getBoolean(KEY_ICONS_FIXED, false)) {
                            fixAllCategoryIcons(categories);
                            prefs.edit().putBoolean(KEY_ICONS_FIXED, true).apply();
                        }
                        adapter.submitList(categories);
                    }
                });
    }

    private void fixAllCategoryIcons(List<Category> categories) {
        new Thread(() -> {
            for (Category cat : categories) {
                String properIcon = matchIconToName(cat.getName());
                if (!properIcon.equals(cat.getIconName())) {
                    cat.setIconName(properIcon);
                    db.inventoryDao().updateCategory(cat);
                }
            }
        }).start();
    }

    private void seedDefaultCategories() {
        new Thread(() -> {
            Map<String, String> categoryIcons = new HashMap<>();
            categoryIcons.put("Yarn", "ic_yarn");
            categoryIcons.put("Hooks", "ic_hooks");
            categoryIcons.put("Stuffing", "ic_heart");
            categoryIcons.put("Safety Eyes", "ic_button");
            categoryIcons.put("Buttons", "ic_button");
            categoryIcons.put("Beads", "ic_bead");
            categoryIcons.put("Packaging", "ic_inventory");
            categoryIcons.put("Other", "ic_star");

            for (String name : categoryIcons.keySet()) {
                String icon = categoryIcons.get(name);
                db.inventoryDao().insertCategory(new Category(name, icon));
            }
        }).start();
    }

    private void showAddOptionsMenu(View anchor) {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(requireContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.category_fab_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_add_item) {
                startActivity(new Intent(requireContext(), AddEditItemActivity.class));
                return true;
            } else if (id == R.id.action_add_category) {
                showAddCategoryDialog();
                return true;
            } else if (id == R.id.action_toggle_delete) {
                deleteMode = !deleteMode;
                adapter.setDeleteMode(deleteMode);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showAddCategoryDialog() {
        pendingIconName = IconPack.getDefaultCategoryIcon();

        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Category name");
        layout.addView(input);

        android.widget.LinearLayout iconRow = new android.widget.LinearLayout(requireContext());
        iconRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        iconRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        android.widget.LinearLayout.LayoutParams iconRowParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        iconRowParams.setMargins(0, 16, 0, 0);
        iconRow.setLayoutParams(iconRowParams);

        ImageView iconPreview = new ImageView(requireContext());
        android.widget.LinearLayout.LayoutParams previewParams = new android.widget.LinearLayout.LayoutParams(96, 96);
        previewParams.setMargins(0, 0, 24, 0);
        iconPreview.setLayoutParams(previewParams);
        updateIconPreview(iconPreview, pendingIconName);
        iconRow.addView(iconPreview);

        com.google.android.material.button.MaterialButton pickIconBtn = new com.google.android.material.button.MaterialButton(requireContext());
        pickIconBtn.setText("Choose Icon");
        pickIconBtn.setOnClickListener(v -> showIconPickerDialog(iconPreview));
        iconRow.addView(pickIconBtn);

        layout.addView(iconRow);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Category")
                .setView(layout)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        final String iconToSave = pendingIconName;
                        new Thread(() -> db.inventoryDao().insertCategory(
                                new Category(name, iconToSave))).start();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showIconPickerDialog(ImageView iconPreviewToUpdate) {
        IconPickerDialog dialog = new IconPickerDialog();
        dialog.setIcons(IconPack.CATEGORY_ICONS);
        dialog.setCurrentIcon(pendingIconName);
        dialog.setListener(iconName -> {
            pendingIconName = iconName;
            new Handler(Looper.getMainLooper()).post(() -> {
                updateIconPreview(iconPreviewToUpdate, pendingIconName);
            });
        });
        dialog.show(getParentFragmentManager(), "icon_picker");
    }

    private void updateIconPreview(ImageView imageView, String iconName) {
        int resId = IconPack.getIconResourceId(requireContext(), iconName);
        imageView.setImageResource(resId != 0 ? resId : R.drawable.ic_category);
        imageView.invalidate();
        imageView.requestLayout();
    }

    private String matchIconToName(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("yarn")) return "ic_yarn";
        if (lower.contains("hook")) return "ic_hooks";
        if (lower.contains("stuff")) return "ic_heart";
        if (lower.contains("eye")) return "ic_button";
        if (lower.contains("button")) return "ic_button";
        if (lower.contains("bead")) return "ic_bead";
        if (lower.contains("pack")) return "ic_inventory";
        if (lower.contains("bag")) return "ic_inventory";
        if (lower.contains("scissor")) return "ic_scissors";
        if (lower.contains("flower")) return "ic_flower";
        if (lower.contains("star")) return "ic_star";
        if (lower.contains("heart")) return "ic_heart";
        if (lower.contains("leaf")) return "ic_leaf";
        return IconPack.getDefaultCategoryIcon();
    }

    private void confirmDeleteCategory(Category category) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Delete \"" + category.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> db.inventoryDao().deleteCategory(category)).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<Category> categories = new ArrayList<>();
        private boolean deleteMode = false;

        void submitList(List<Category> list) {
            this.categories = new ArrayList<>(list);
            notifyDataSetChanged();
        }

        void setDeleteMode(boolean enabled) {
            this.deleteMode = enabled;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.bind(category);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvName;
            final ImageView ivIcon;
            final MaterialCardView card;
            final com.google.android.material.button.MaterialButton btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_category_name);
                ivIcon = itemView.findViewById(R.id.iv_category_icon);
                card = itemView.findViewById(R.id.card_category);
                btnDelete = itemView.findViewById(R.id.btn_delete_category);
            }

            void bind(Category category) {
                tvName.setText(category.getName());

                int iconRes = IconPack.getIconResourceId(itemView.getContext(), category.getIconName());
                ivIcon.setImageResource(iconRes != 0 ? iconRes : R.drawable.ic_category);

                card.setOnClickListener(v -> {
                    if (!deleteMode) {
                        Intent intent = new Intent(requireContext(), AddEditItemActivity.class);
                        intent.putExtra("preselected_category", category.getName());
                        startActivity(intent);
                    }
                });

                btnDelete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
                btnDelete.setOnClickListener(v -> confirmDeleteCategory(category));
            }
        }
    }
}