package com.berrys.crochetinventory.ui.categories;

import android.content.Intent;
import android.os.Bundle;
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
import com.berrys.crochetinventory.ui.inventory.AddEditItemActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase db;
    private boolean deleteMode = false;

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
                        adapter.submitList(categories);
                    }
                });
    }

    private void seedDefaultCategories() {
        new Thread(() -> {
            String[] defaults = {"Yarn", "Hooks", "Stuffing", "Safety Eyes",
                    "Buttons", "Beads", "Packaging", "Other"};
            for (String name : defaults) {
                db.inventoryDao().insertCategory(new Category(name, IconPack.getDefaultCategoryIcon()));
            }
        }).start();
    }

    private void showAddOptionsMenu(View anchor) {
        if (anchor == null || !isAdded()) return;

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

        popup.setOnDismissListener(menu -> {
            // Clean up if needed
        });

        popup.show();
    }

    private void showAddCategoryDialog() {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Category name");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        new Thread(() -> db.inventoryDao().insertCategory(
                                new Category(name, IconPack.getDefaultCategoryIcon()))).start();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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