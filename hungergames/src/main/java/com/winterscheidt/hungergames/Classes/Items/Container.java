package com.winterscheidt.hungergames.Classes.Items;

import java.util.ArrayList;
import java.util.List;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Utils.Inventory;

public class Container extends Item implements Interactable {
    private Inventory inventory = new Inventory();
    private boolean extendsSize = false;
    private boolean locationDump = false;

    public Container(String name, String description, Inventory inventory, boolean extendsSize) {
        super(name, description);
        this.inventory = inventory;
        this.extendsSize = extendsSize;
    }

    @Override
    public void interact(Player player, Game game) {
        Inventory pInv = player.getInventory();
        if (extendsSize) {
            pInv.setMaxSize(pInv.getMaxSize() + inventory.getSize());
        }
        String message = "You open up the " + name + ". Inside, you find: ";
        for (Item item : inventory.getItems()) {
            message += "\n - " + item.getName();
            if (locationDump || pInv.getSize() >= pInv.getMaxSize()) {
                player.getLocation().addItem(item);
            } else {
                pInv.addItem(item);
            }
        }
        player.getLocation().removeItem(this);
        game.getIO().broadcast(message, player);
    }
}