package com.winterscheidt.hungergames.Classes.Entities;

import com.winterscheidt.hungergames.Interfaces.Inspectable;
import com.winterscheidt.hungergames.classes.Game.Game;
import com.winterscheidt.hungergames.classes.Game.Location;

public abstract class Entity implements Inspectable {
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
