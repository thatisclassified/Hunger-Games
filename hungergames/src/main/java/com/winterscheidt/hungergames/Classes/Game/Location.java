package com.winterscheidt.hungergames.Classes.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winterscheidt.hungergames.Classes.Items.Item;

public class Location {
    private String name;
    private String description;
    private boolean dangerous;
    private List<Item> items = new ArrayList<>();
    private Map<String, Location> connections = new HashMap<>();

    // Connection management
    public void addConnection(String name, Location location) {
        this.connections.put(name, location);
    }
    public void removeConnection(String name) {
        this.connections.remove(name);
    }
    public Location getConnection(String name) {
        return this.connections.get(name);
    }

    // Items
    public Item getItem(int index) {
        return this.items.get(index);
    }
    public void addItem(Item item) {
        this.items.add(item);
    }
    public void removeItem(Item item) {
        this.items.remove(item);
    }

    // Other getters
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
}
