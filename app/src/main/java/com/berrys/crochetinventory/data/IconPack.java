package com.berrys.crochetinventory.data;

public class IconPack {

    // Category icons - craft themed
    public static final String[] CATEGORY_ICONS = {
            "ic_yarn", "ic_hooks", "ic_needles", "ic_scissors",
            "ic_fabric", "ic_ribbon", "ic_button", "ic_bead",
            "ic_gem", "ic_heart", "ic_star", "ic_flower",
            "ic_leaf", "ic_paw", "ic_baby", "ic_gift",
            "ic_home", "ic_bag", "ic_hat", "ic_socks",
            "ic_bow", "ic_crown", "ic_sun", "ic_moon",
            "ic_music", "ic_book", "ic_camera", "ic_paint"
    };

    // Inventory item icons - general craft supplies
    public static final String[] ITEM_ICONS = {
            "ic_box", "ic_package", "ic_bottle", "ic_jar",
            "ic_roll", "ic_spool", "ic_skein", "ic_bundle",
            "ic_stack", "ic_pile", "ic_tool", "ic_kit",
            "ic_set", "ic_pair", "ic_pack", "ic_bag_item",
            "ic_container", "ic_tube", "ic_sheet", "ic_strip",
            "ic_piece", "ic_length", "ic_weight", "ic_dozen"
    };

    // Get drawable resource ID from name
    public static int getIconResourceId(android.content.Context context, String iconName) {
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

    // Default icons
    public static String getDefaultCategoryIcon() {
        return "ic_yarn";
    }

    public static String getDefaultItemIcon() {
        return "ic_box";
    }
}