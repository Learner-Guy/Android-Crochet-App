package com.berrys.crochetinventory.ui.orders;

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
import com.berrys.crochetinventory.data.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        db = AppDatabase.getInstance(requireContext());
        recyclerView = view.findViewById(R.id.recycler_orders);

        setupRecyclerView();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_order);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditOrderActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshOrders();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrdersAdapter(order -> {
            // Open order detail/edit
            Intent intent = new Intent(requireContext(), AddEditOrderActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshOrders() {
        if (!isAdded() || adapter == null) return;
        db.orderDao().getAllOrders().observe(getViewLifecycleOwner(), orders -> {
            if (isAdded() && adapter != null) {
                adapter.submitList(orders != null ? orders : new ArrayList<>());
            }
        });
    }
}