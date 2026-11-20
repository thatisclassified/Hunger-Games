package com.winterscheidt.hungergames.Classes.Utils;

import java.util.ArrayList;
import java.util.List;

import com.winterscheidt.hungergames.Classes.Items.Item;

public class Inventory {
    private List<Item> inventory = new ArrayList<Item>();
    private int maxSize = 5;

    public Item getItem(int index) {
        return this.inventory.get(index);
    }
    public boolean hasItem(Item item) {
        return this.inventory.contains(item);
    }
    public boolean addItem(Item item) {
        if (this.inventory.size() < this.maxSize) {
            this.inventory.add(item);
            return true;
        }
        return false;
    }
    public boolean removeItem(int index) {
        if (index >= 0 && index < this.inventory.size()) {
            this.inventory.remove(index);
            return true;
        }
        return false;
    }
    public boolean removeItem(Item item) {
        return this.inventory.remove(item);
    }
}
