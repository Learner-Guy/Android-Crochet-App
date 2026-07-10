package com.example.crochetinventory.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.crochetinventory.R;
import com.example.crochetinventory.data.InventoryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

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
                return old.getName().equals(next.getName()) && old.getQuantity() == next.getQuantity();
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
        private final TextView tvName, tvCategory, tvQuantity, tvColor;
        private final ImageView ivItem;
        private final MaterialButton btnEdit, btnDelete;
        private final MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvColor = itemView.findViewById(R.id.tv_item_color);
            ivItem = itemView.findViewById(R.id.iv_item_image);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cardView = itemView.findViewById(R.id.card_item);
        }

        void bind(InventoryItem item) {
            tvName.setText(item.getName());
            tvCategory.setText(item.getCategory());
            tvQuantity.setText(item.getQuantity() + " " + item.getUnit());
            tvColor.setText(item.getColor());

            if (item.getQuantity() <= item.getLowStock()) {
                cardView.setStrokeColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                cardView.setStrokeWidth(3);
            } else {
                cardView.setStrokeWidth(0);
            }

            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImagePath())
                        .placeholder(R.drawable.ic_inventory)
                        .into(ivItem);
            } else {
                ivItem.setImageResource(R.drawable.ic_inventory);
            }

            btnEdit.setOnClickListener(v -> listener.onEditClick(item));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }
}