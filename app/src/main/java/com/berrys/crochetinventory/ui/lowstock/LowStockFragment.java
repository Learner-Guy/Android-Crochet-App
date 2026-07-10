package com.berrys.crochetinventory.ui.lowstock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.berrys.crochetinventory.ui.inventory.AddEditItemActivity;
import com.berrys.crochetinventory.ui.inventory.InventoryAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LowStockFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_low_stock, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_low_stock);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        InventoryAdapter adapter = new InventoryAdapter(new InventoryAdapter.OnItemClickListener() {
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
                        .setMessage("Delete " + item.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            new Thread(() -> AppDatabase.getInstance(requireContext())
                                    .inventoryDao().delete(item)).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        AppDatabase.getInstance(requireContext()).inventoryDao()
                .getLowStockItems()
                .observe(getViewLifecycleOwner(), adapter::submitList);

        return view;
    }
}