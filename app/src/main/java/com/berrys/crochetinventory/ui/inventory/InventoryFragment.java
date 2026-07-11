package com.berrys.crochetinventory.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private SearchView searchView;
    private Spinner categoryFilter;
    private AppDatabase db;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        db = AppDatabase.getInstance(requireContext());
        recyclerView = view.findViewById(R.id.recycler_inventory);
        searchView = view.findViewById(R.id.search_view);
        categoryFilter = view.findViewById(R.id.spinner_category);

        setupRecyclerView();
        setupCategoryFilter();
        setupSearch();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_inventory);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditItemActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InventoryAdapter(new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(InventoryItem item) {
                Intent intent = new Intent(requireContext(), AddEditItemActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(InventoryItem item) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + item.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            new Thread(() -> db.inventoryDao().delete(item)).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupCategoryFilter() {
        spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilter.setAdapter(spinnerAdapter);

        categoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterItems();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        db.inventoryDao().getCategoryNames().observe(getViewLifecycleOwner(), names -> {
            if (!isAdded()) return;

            spinnerAdapter.clear();
            spinnerAdapter.add("All");
            if (names != null) {
                spinnerAdapter.addAll(names);
            }
            spinnerAdapter.notifyDataSetChanged();

            // Load all items after categories loaded
            loadAllItems();
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Only filter if spinner is ready
                if (categoryFilter.getSelectedItem() != null) {
                    filterItems();
                }
                return true;
            }
        });
    }

    private void filterItems() {
        if (!isAdded() || categoryFilter == null) return;

        Object selectedItem = categoryFilter.getSelectedItem();
        String category = (selectedItem != null) ? selectedItem.toString() : "All";

        String query = searchView.getQuery() != null ? searchView.getQuery().toString().trim() : "";

        LiveData<List<InventoryItem>> data;

        if (!query.isEmpty()) {
            data = db.inventoryDao().searchItems(query);
        } else if (!category.equals("All")) {
            data = db.inventoryDao().getItemsByCategory(category);
        } else {
            data = db.inventoryDao().getAllItems();
        }

        data.observe(getViewLifecycleOwner(), items -> {
            if (isAdded() && adapter != null) {
                adapter.submitList(items != null ? items : new ArrayList<>());
            }
        });
    }

    private void loadAllItems() {
        if (!isAdded()) return;

        db.inventoryDao().getAllItems().observe(getViewLifecycleOwner(), items -> {
            if (isAdded() && adapter != null) {
                adapter.submitList(items != null ? items : new ArrayList<>());
            }
        });
    }
}