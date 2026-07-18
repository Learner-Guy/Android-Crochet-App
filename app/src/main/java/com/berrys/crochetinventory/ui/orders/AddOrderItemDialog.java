package com.berrys.crochetinventory.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AddOrderItemDialog extends DialogFragment {

    public interface OnItemAddedListener {
        void onItemAdded(String itemName, int quantity, double unitPrice);
    }

    private OnItemAddedListener listener;
    private AppDatabase db;
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public void setListener(OnItemAddedListener listener) {
        this.listener = listener;
    }

    public void setDatabase(AppDatabase db) {
        this.db = db;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_order_item, null);

        AutoCompleteTextView actItem = view.findViewById(R.id.act_item_name);
        TextInputEditText etQuantity = view.findViewById(R.id.et_quantity);
        TextInputEditText etPrice = view.findViewById(R.id.et_unit_price);

        // Load inventory items for dropdown
        if (db != null) {
            db.inventoryDao().getAllItems().observe((LifecycleOwner) requireContext(), items -> {
                inventoryItems.clear();
                if (items != null) {
                    inventoryItems.addAll(items);
                }
                List<String> itemNames = new ArrayList<>();
                for (InventoryItem item : inventoryItems) {
                    itemNames.add(item.getName() + " (" + item.getQuantity() + " " + item.getUnit() + " available)");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, itemNames);
                actItem.setAdapter(adapter);
            });
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Add Inventory Item")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String selected = actItem.getText().toString().trim();
                    String qtyStr = etQuantity.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (selected.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
                        return;
                    }

                    // Extract item name from "ItemName (Qty available)"
                    String itemName = selected;
                    int parenIndex = selected.indexOf(" (");
                    if (parenIndex > 0) {
                        itemName = selected.substring(0, parenIndex);
                    }

                    int quantity = Integer.parseInt(qtyStr);
                    double unitPrice = Double.parseDouble(priceStr);

                    if (listener != null) {
                        listener.onItemAdded(itemName, quantity, unitPrice);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}
