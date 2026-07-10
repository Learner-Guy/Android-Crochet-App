package com.example.crochetinventory.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crochetinventory.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Arrays;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private final List<String> categories = Arrays.asList(
            "Yarn", "Hooks", "Stuffing", "Safety Eyes",
            "Buttons", "Beads", "Packaging", "Other"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(new CategoryAdapter());

        return view;
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(categories.get(position));
        }

        @Override
        public int getItemCount() { return categories.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvName;
            private final MaterialCardView card;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_category_name);
                card = itemView.findViewById(R.id.card_category);
            }

            void bind(String category) {
                tvName.setText(category);
                card.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), category + " selected", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}