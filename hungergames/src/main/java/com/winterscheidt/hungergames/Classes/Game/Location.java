package com.winterscheidt.hungergames.Classes.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Utils.DetectionLevel;

public class Location implements Serializable {
    private String name;
    private String description;
    private double baseObscurity = 0;
    private boolean dangerous = false;
    private List<Item> items = new ArrayList<>();
    private Map<String, Location> connections = new HashMap<>();

    // Constructors
    public Location (String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Location (String name, String description, double baseObscurity, List<Item> items) {
        this.name = name;
        this.description = description;
        this.baseObscurity = baseObscurity;
        this.items = items;
    }
    public Location (String name, String description, double baseObscurity) {
        this.name = name;
        this.description = description;
        this.baseObscurity = baseObscurity;
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
    public Map<String, Location> getConnections() {
        return this.connections;
    }
    public Location getConnection(String name) {
        return this.connections.get(name);
    }
    public void addDoubleConnection(String name1, Location location, String name2) {
        this.connections.put(name1, location);
        location.addConnection(name2, this);
    }

    // Items
    public List<Item> getItems() {
        return this.items;
    }
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
    public double getBaseObscurity() {
        return this.baseObscurity;
    }
    public String getFullDescription(Game game, Entity requester) {
        String result = " --- " + this.getName() + " --- ";
        if (game.getIO() instanceof DiscordIO) {
            result = "**" + result + "**";
        }
        result += "\n" + this.getDescription() + "\n";
        if (this.items.size() > 0) {
            result += "You see the following items: \n";
            for (Item item : this.items) {
                result += " - " + item.getName() + "\n";
            }
        }
        boolean entities = false;
        for (Entity entity : game.getEntities()) {
            if (entity.getLocation() == this && entity != requester) {
                DetectionLevel detection = game.getCurrentEntity().getDetection(entity);
                if (detection != DetectionLevel.HIDDEN) {
                    if (!entities) {
                        result += "You see the following creatures: \n";
                        entities = true;
                    }
                    if (detection == DetectionLevel.RECOGNIZED) {
                        result += " - " + entity.getName();
                        if (game.getIO() instanceof DiscordIO) {
                            result += "\n-#";
                        }
                        result += " (" + entity.getDescription() + ")\n";
                    } else { // SPOTTED
                        result += " - Obscured Figure" + "\n";
                    }
                }
            }
        }
        return result;
    }

    // And setters
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }
}
