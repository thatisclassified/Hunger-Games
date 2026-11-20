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

    // Methods
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }

    public abstract void onDeath(Game game);
    public abstract void onTurn(Game game);
}
