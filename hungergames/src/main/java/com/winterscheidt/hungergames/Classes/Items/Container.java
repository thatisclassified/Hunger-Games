package com.winterscheidt.hungergames.Classes.Items;

import java.util.ArrayList;
import java.util.List;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Utils.Inventory;

public class Container extends Item implements Interactable {
    private Inventory inventory;
    private boolean extendsSize = false;
    private boolean locationDump = false;

    public Container(String name, String description, Inventory inventory, boolean extendsSize) {
        super(name, description);
    }

    @Override
    public void interact(Player player, Game game) {
        Inventory pInv = player.getInventory();
        if (extendsSize) {
            pInv.setMaxSize(pInv.getMaxSize() + inventory.getSize());
        }
        String message = "You open up the " + name + ". Inside, you find: ";
        for (Item item : inventory.getItems()) {
            message += " - " + item.getName();
            if (locationDump || pInv.getSize() >= pInv.getMaxSize()) {
                player.getLocation().addItem(item);
            } else {
                pInv.addItem(item);
            }
        }
        game.getIO().broadcast(message);
    }
}