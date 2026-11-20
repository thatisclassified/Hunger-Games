package com.winterscheidt.hungergames.Classes.Entities;

import com.winterscheidt.hungergames.Interfaces.Inspectable;
import java.io.Serializable;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Classes.Game.Location;

public abstract class Entity implements Inspectable, Serializable {
    // Fields
    protected String name;
    protected String description;
    protected double health;
    protected Location location;
    protected boolean AI = true;

    // Methods
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public boolean isAI() {
        return isAI();
    }
    public Location getLocation() {
        return this.location;
    }
    public boolean move(String location) {
        if (this.location.getConnections().containsKey(location)) {
            this.location = this.location.getConnection(location);
            return true;
        }
        return false;
    }
    public boolean move(Location location) {
        if (this.location.getConnections().containsValue(location)) {
            this.location = location;
            return true;
        }
        return false;
    }

    public abstract void onDeath(Game game);
    public abstract void onTurn(Game game);
}
