package com.berrys.crochetinventory.ui.components;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.berrys.crochetinventory.R;
import com.berrys.crochetinventory.data.IconPack;

public class IconPickerDialog extends DialogFragment {

    public interface IconPickerListener {
        void onIconSelected(String iconName);
    }

    private String[] iconSet = IconPack.CATEGORY_ICONS;
    private String currentSelection;
    private IconPickerListener listener;

    public void setIcons(String[] icons) {
        this.iconSet = icons;
    }

    public void setCurrentIcon(String icon) {
        this.currentSelection = icon;
    }

    public void setListener(IconPickerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_icon_picker, null);
        GridLayout grid = view.findViewById(R.id.icon_grid);

        for (String iconName : iconSet) {
            ImageView iconView = new ImageView(requireContext());
            int resId = IconPack.getIconResourceId(requireContext(), iconName);

            iconView.setImageResource(resId != 0 ? resId : R.drawable.ic_inventory);
            iconView.setPadding(24, 24, 24, 24);

            if (iconName.equals(currentSelection)) {
                iconView.setBackgroundResource(R.drawable.bg_icon_selected);
            } else {
                iconView.setBackgroundResource(R.drawable.bg_icon_normal);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(12, 12, 12, 12);
            iconView.setLayoutParams(params);

            iconView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIconSelected(iconName);
                }
                // Delay dismiss to ensure callback fires first
                v.post(() -> dismiss());
            });

            grid.addView(iconView);
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Icon")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .create();
    }
}