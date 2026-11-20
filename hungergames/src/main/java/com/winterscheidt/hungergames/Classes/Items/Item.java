package com.winterscheidt.hungergames.Classes.Items;

import java.util.ArrayList;
import java.util.List;

import com.winterscheidt.hungergames.Interfaces.Inspectable;
import com.winterscheidt.hungergames.classes.Living.Player;

public abstract class Item implements Inspectable {
    // Data fields
    protected String name;
    protected String description;
    protected boolean bePickedUp = true;

    // Constructors

    // Methods
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }

    // public void onInteract(Player player) {
    //     broadcast("You picked up " + this.name + ".");
    //     player.giveItem(this);

    // }
    
}