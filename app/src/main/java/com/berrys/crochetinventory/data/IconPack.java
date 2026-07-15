package com.berrys.crochetinventory.data;

import android.content.Context;

public class IconPack {

    public static final String[] CATEGORY_ICONS = {
            "ic_yarn", "ic_hooks", "ic_scissors", "ic_button",
            "ic_bead", "ic_heart", "ic_flower", "ic_star", "ic_leaf"
    };

    public static final String[] ITEM_ICONS = {
            "ic_inventory", "ic_yarn", "ic_hooks", "ic_scissors",
            "ic_button", "ic_bead", "ic_heart", "ic_flower"
    };

    public static int getIconResourceId(Context context, String iconName) {
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

    public static String getDefaultCategoryIcon() {
        return "ic_yarn";
    }

    public static String getDefaultItemIcon() {
        return "ic_inventory";
    }
}