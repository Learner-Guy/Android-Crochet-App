package com.berrys.crochetinventory.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String iconName;  // NEW: stores icon identifier

    @Ignore
    public Category(String name) {
        this.name = name;
        this.iconName = IconPack.getDefaultCategoryIcon();
    }


    public Category(String name, String iconName) {
        this.name = name;
        this.iconName = iconName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}