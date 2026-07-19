package com.berrys.crochetinventory.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
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
        void onItemAdded(InventoryItem item, int quantity, double sizeUsed);
    }

    private OnItemAddedListener listener;
    private AppDatabase db;
    private List<InventoryItem> inventoryItems = new ArrayList<>();
    private InventoryItem selectedItem = null;

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
        TextView tvItemInfo = view.findViewById(R.id.tv_item_info);
        TextView tvSizeAvailable = view.findViewById(R.id.tv_size_available);
        TextInputEditText etQuantity = view.findViewById(R.id.et_quantity);
        TextInputEditText etSizeUsed = view.findViewById(R.id.et_size_used);
        View layoutSizeUsed = view.findViewById(R.id.layout_size_used);

        if (db != null) {
            db.inventoryDao().getAllItems().observe((LifecycleOwner) requireContext(), items -> {
                inventoryItems.clear();
                if (items != null) {
                    inventoryItems.addAll(items);
                }
                List<String> itemNames = new ArrayList<>();
                for (InventoryItem item : inventoryItems) {
                    String display = item.getName();
                    if (item.isContinuous() && item.getSizeValue() > 0) {
                        display += " (" + item.getSizeValue() + " " + item.getSizeUnit() + " available)";
                    } else {
                        display += " (" + item.getQuantity() + " " + item.getUnit() + " available)";
                    }
                    itemNames.add(display);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, itemNames);
                actItem.setAdapter(adapter);
            });
        }

        actItem.setOnItemClickListener((parent, v, position, id) -> {
            if (position < inventoryItems.size()) {
                selectedItem = inventoryItems.get(position);
                tvItemInfo.setText("Cost: ₹" + selectedItem.getCost() + " per " + selectedItem.getUnit());
                tvItemInfo.setVisibility(View.VISIBLE);

                if (selectedItem.isContinuous()) {
                    layoutSizeUsed.setVisibility(View.VISIBLE);
                    etSizeUsed.setHint("Amount used (" + selectedItem.getSizeUnit() + ")");
                    tvSizeAvailable.setText("Available: " + selectedItem.getSizeValue() + " " + selectedItem.getSizeUnit());
                    tvSizeAvailable.setVisibility(View.VISIBLE);
                } else {
                    layoutSizeUsed.setVisibility(View.GONE);
                    tvSizeAvailable.setVisibility(View.GONE);
                }
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle("Add Inventory Item")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    if (selectedItem == null) return;
                    String qtyStr = etQuantity.getText().toString().trim();
                    if (qtyStr.isEmpty()) return;

                    int quantity = Integer.parseInt(qtyStr);
                    double sizeUsed = 0;

                    // Validate quantity
                    if (quantity > selectedItem.getQuantity()) {
                        android.widget.Toast.makeText(requireContext(),
                            "Not enough stock. Available: " + selectedItem.getQuantity(),
                            android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // For continuous items, validate size used
                    if (selectedItem.isContinuous()) {
                        String sizeStr = etSizeUsed.getText().toString().trim();
                        if (sizeStr.isEmpty()) {
                            android.widget.Toast.makeText(requireContext(),
                                "Please enter amount used",
                                android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sizeUsed = Double.parseDouble(sizeStr);
                        if (sizeUsed > selectedItem.getSizeValue()) {
                            android.widget.Toast.makeText(requireContext(),
                                "Not enough material. Available: " + selectedItem.getSizeValue() + " " + selectedItem.getSizeUnit(),
                                android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (listener != null) {
                        listener.onItemAdded(selectedItem, quantity, sizeUsed);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}
