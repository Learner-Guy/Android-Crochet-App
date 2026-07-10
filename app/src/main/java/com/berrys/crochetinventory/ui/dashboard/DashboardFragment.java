package com.example.crochetinventory.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.crochetinventory.R;
import com.example.crochetinventory.data.AppDatabase;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {
    private TextView tvTotalItems, tvLowStockCount, tvCategoriesCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvLowStockCount = view.findViewById(R.id.tv_low_stock_count);
        tvCategoriesCount = view.findViewById(R.id.tv_categories_count);

        AppDatabase db = AppDatabase.getInstance(requireContext());

        db.inventoryDao().getTotalItemCount().observe(getViewLifecycleOwner(), count -> {
            tvTotalItems.setText(String.valueOf(count != null ? count : 0));
        });

        db.inventoryDao().getLowStockCount().observe(getViewLifecycleOwner(), count -> {
            tvLowStockCount.setText(String.valueOf(count != null ? count : 0));
        });

        db.inventoryDao().getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            tvCategoriesCount.setText(String.valueOf(categories != null ? categories.size() : 0));
        });

        return view;
    }
}