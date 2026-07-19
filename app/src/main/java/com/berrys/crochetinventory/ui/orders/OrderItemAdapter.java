package com.berrys.crochetinventory.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.OrderItem;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private final List<OrderItem> items;
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDeleteItem(int position);
    }

    public OrderItemAdapter(List<OrderItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.tvName.setText(item.getCustomItemName());

        // Show quantity and size if continuous
        String qtyText = "Qty: " + item.getQuantityUsed();
        if (item.getSizeUsed() > 0) {
            qtyText += " | Used: " + item.getSizeUsed() + " " + item.getSizeUnit();
        }
        holder.tvQty.setText(qtyText);

        holder.tvPrice.setText("₹" + item.getUnitPrice() + " each");
        holder.tvTotal.setText("₹" + String.format("%.2f", item.getTotalAmount()));

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvQty, tvPrice, tvTotal;
        final ImageView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQty = itemView.findViewById(R.id.tv_item_qty);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvTotal = itemView.findViewById(R.id.tv_item_total);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
