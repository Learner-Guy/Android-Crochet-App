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

    private IconPickerListener listener;
    private String[] iconSet;
    private String currentSelection;

    public static IconPickerDialog newCategoryPicker(@Nullable String currentIcon, IconPickerListener listener) {
        IconPickerDialog dialog = new IconPickerDialog();
        dialog.iconSet = IconPack.CATEGORY_ICONS;
        dialog.currentSelection = currentIcon;
        dialog.listener = listener;
        return dialog;
    }

    public static IconPickerDialog newItemPicker(@Nullable String currentIcon, IconPickerListener listener) {
        IconPickerDialog dialog = new IconPickerDialog();
        dialog.iconSet = IconPack.ITEM_ICONS;
        dialog.currentSelection = currentIcon;
        dialog.listener = listener;
        return dialog;
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
            iconView.setPadding(16, 16, 16, 16);

            // Highlight current selection
            if (iconName.equals(currentSelection)) {
                iconView.setBackgroundResource(R.drawable.bg_icon_selected);
            } else {
                iconView.setBackgroundResource(R.drawable.bg_icon_normal);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            iconView.setLayoutParams(params);

            iconView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIconSelected(iconName);
                }
                dismiss();
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