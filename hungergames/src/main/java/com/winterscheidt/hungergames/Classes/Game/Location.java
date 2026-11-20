package com.winterscheidt.hungergames.Classes.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winterscheidt.hungergames.Classes.Items.Item;

public class Location implements Serializable {
    private String name;
    private String description;
    private boolean dangerous = false;
    private List<Item> items = new ArrayList<>();
    private Map<String, Location> connections = new HashMap<>();

    // Constructors
    public Location (String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Location (String name, String description, List<Item> items) {
        this.name = name;
        this.description = description;
        this.items = items;
    }
    public Location (String name, String description, List<Item> items, Map<String, Location> connections) {
        this.name = name;
        this.description = description;
        this.items = items;
        this.connections = connections;
    }

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
    public void addDoubleConnection(String name1, Location location, String name2) {
        this.connections.put(name1, location);
        location.addConnection(name2, this);
    }

    // Items
    public Item getItem(int index) {
        return this.items.get(index);
    }
    public void addItem(Item item) {
        this.items.add(item);
    }
    public boolean removeItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            return true;
        }
        return false;
    }
    public boolean removeItem(Item item) {
        return this.items.remove(item);
    }
    public boolean hasItem(Item item) {
        return this.items.contains(item);
    }

    // Other getters
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public boolean isDangerous() {
        return this.dangerous;
    }

    // And setters
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }
}
