package com.winterscheidt.hungergames.Classes.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.winterscheidt.hungergames.Interfaces.Inspectable;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;

public abstract class Item implements Inspectable, Serializable {
    // Data fields
    protected String name;
    protected String description;
    // protected boolean bePickedUp = true;

    // Constructors
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Methods
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
}