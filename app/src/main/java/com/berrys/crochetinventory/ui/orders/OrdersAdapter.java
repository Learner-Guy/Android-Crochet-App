package com.berrys.crochetinventory.ui.orders;

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
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.Order;
import com.berrys.crochetinventory.data.OrderStatus;
import com.google.android.material.chip.Chip;
import java.io.File;

public class OrdersAdapter extends ListAdapter<Order, OrdersAdapter.ViewHolder> {

    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrdersAdapter(OnOrderClickListener listener) {
        super(new DiffUtil.ItemCallback<Order>() {
            @Override
            public boolean areItemsTheSame(@NonNull Order old, @NonNull Order next) {
                return old.getId() == next.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull Order old, @NonNull Order next) {
                return old.getStatus().equals(next.getStatus())
                        && old.getCustomerName().equals(next.getCustomerName())
                        && java.util.Objects.equals(old.getSampleImagePath(), next.getSampleImagePath());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivOrderImage;
        private final TextView tvCustomer, tvDescription, tvAmount;
        private final Chip chipStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ivOrderImage = itemView.findViewById(R.id.iv_order_image);
            tvCustomer = itemView.findViewById(R.id.tv_customer_name);
            tvDescription = itemView.findViewById(R.id.tv_order_description);
            tvAmount = itemView.findViewById(R.id.tv_order_amount);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }

        void bind(Order order) {
            tvCustomer.setText(order.getCustomerName());
            tvDescription.setText(order.getDescription());
            tvAmount.setText("₹" + String.format("%.2f", order.getGrandTotal()));

            // Status chip with color
            OrderStatus status = OrderStatus.fromName(order.getStatus());
            chipStatus.setText(status.getDisplayName());
            chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(status.getColorHex())));

            // Load sample image
            String imagePath = order.getSampleImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Glide.with(ivOrderImage.getContext())
                            .load(imgFile)
                            .placeholder(R.drawable.ic_inventory)
                            .into(ivOrderImage);
                } else {
                    ivOrderImage.setImageResource(R.drawable.ic_inventory);
                }
            } else {
                ivOrderImage.setImageResource(R.drawable.ic_inventory);
            }

            // Click to open order detail
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }
    }
}