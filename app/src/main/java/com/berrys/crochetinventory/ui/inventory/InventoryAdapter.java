package com.berrys.crochetinventory.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.AppDatabase;
import com.berrys.crochetinventory.data.InventoryItem;
import com.berrys.crochetinventory.data.IconPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.text.NumberFormat;
import java.util.Objects;

public class InventoryAdapter extends ListAdapter<InventoryItem, InventoryAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(InventoryItem item);
        void onDeleteClick(InventoryItem item);
    }

    public InventoryAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<InventoryItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull InventoryItem old, @NonNull InventoryItem next) {
                return old.getId() == next.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull InventoryItem old, @NonNull InventoryItem next) {
                return old.getName().equals(next.getName())
                        && old.getQuantity() == next.getQuantity()
                        && Objects.equals(old.getIconName(), next.getIconName())
                        && Objects.equals(old.getImagePath(), next.getImagePath());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = getItem(position);
        holder.bind(item);
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvCategory, tvSummary;
        private final TextView tvDetailColor, tvDetailQty, tvDetailCost, tvDetailSupplier, tvDetailLowStock, tvDetailNotes;
        private final ImageView ivItem;
        private final MaterialButton btnEdit, btnDelete;
        private final MaterialCardView cardView;
        private final LinearLayout layoutDetails;
        private boolean expanded = false;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvSummary = itemView.findViewById(R.id.tv_item_summary);
            ivItem = itemView.findViewById(R.id.iv_item_image);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cardView = itemView.findViewById(R.id.card_item);
            layoutDetails = itemView.findViewById(R.id.layout_details);
            tvDetailColor = itemView.findViewById(R.id.tv_detail_color);
            tvDetailQty = itemView.findViewById(R.id.tv_detail_quantity);
            tvDetailCost = itemView.findViewById(R.id.tv_detail_cost);
            tvDetailSupplier = itemView.findViewById(R.id.tv_detail_supplier);
            tvDetailLowStock = itemView.findViewById(R.id.tv_detail_low_stock);
            tvDetailNotes = itemView.findViewById(R.id.tv_detail_notes);
        }

        private void loadIcon(InventoryItem item, ImageView ivItem) {
            String iconName = item.getIconName();
            if (iconName != null && !iconName.isEmpty()) {
                int iconRes = IconPack.getIconResourceId(ivItem.getContext(), iconName);
                ivItem.setImageResource(iconRes != 0 ? iconRes : R.drawable.ic_inventory);
            } else {
                ivItem.setImageResource(R.drawable.ic_inventory);
            }
        }


        void bind(InventoryItem item) {
            tvName.setText(item.getName());
            tvCategory.setText(item.getCategory());

            // Summary line: Qty + Color
            String color = (item.getColor() != null && !item.getColor().isEmpty()) ? item.getColor() : "-";
            tvSummary.setText("Qty: " + item.getQuantity() + " " + item.getUnit() + " | Color: " + color);

            // Low stock alert border
            if (item.getQuantity() <= item.getLowStock()) {
                cardView.setStrokeColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                cardView.setStrokeWidth(3);
            } else {
                cardView.setStrokeWidth(0);
            }
            // Image takes priority over icon
            String imagePath = item.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Glide.with(ivItem.getContext()).load(imgFile).into(ivItem);
                } else {
                    // Image file missing, fall back to icon
                    loadIcon(item, ivItem);
                }
            } else {
                // No image, use icon
                loadIcon(item, ivItem);
            }

            // Image and icon loading


            // Expandable details
            tvDetailColor.setText("Color: " + color);
            tvDetailQty.setText("Quantity: " + item.getQuantity() + " " + item.getUnit());
            tvDetailCost.setText("Cost: " + NumberFormat.getCurrencyInstance().format(item.getCost()));
            tvDetailSupplier.setText("Supplier: " + (item.getSupplier() != null ? item.getSupplier() : "-"));
            tvDetailLowStock.setText("Low Stock Alert: " + item.getLowStock());
            tvDetailNotes.setText("Notes: " + (item.getNotes() != null && !item.getNotes().isEmpty() ? item.getNotes() : "-"));

            // Click to expand/collapse details
            cardView.setOnClickListener(v -> {
                expanded = !expanded;
                layoutDetails.setVisibility(expanded ? View.VISIBLE : View.GONE);
            });

            btnEdit.setOnClickListener(v -> listener.onEditClick(item));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }
}