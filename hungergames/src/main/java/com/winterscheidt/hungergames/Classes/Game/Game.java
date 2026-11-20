package com.winterscheidt.hungergames.Classes.Game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.Usable;

public class Game implements Serializable {
    private List<Location> locations = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();

    public List<GameAction> getMoveActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        for (String locName : entity.getLocation().getConnections().keySet()) {
            actions.add(new GameAction("Move: " + locName, true,
            () -> entity.move(locName)));
        }
        return actions;
    }

    public List<GameAction> getInspectActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (!entity.isAI()) {
            for (Item item : entity.getLocation().getItems()) {
                actions.add(new GameAction("Inspect " + item.getName(), false, null));
            }
        }
        return actions;
    }

    public List<GameAction> getInteractActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (!entity.isAI()) {
            for (Item item : entity.getLocation().getItems()) {
                if (item instanceof Attainable) {
                    actions.add(new GameAction("Pick up " + item.getName(), true, null));
                }
                if (item instanceof Interactable) {
                    actions.add(new GameAction("Interact with " + item.getName(), true, null));
                }
            }
        }
        return actions;
    }

    public List<GameAction> getInventoryActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (entity.getInventory().getSize() < entity.getInventory().getMaxSize()) {
            actions.add(new GameAction("Unequip " + entity.getEquipped().getName(), false, 
            () -> entity.unequipItem()));
        }
        for (Item item : entity.getInventory().getItems()) {
            if (item instanceof Equippable) {
                actions.add(new GameAction("Equip " + item.getName(), false, 
                () -> entity.equipItem(item)));
            }
            if (item instanceof Usable) {
                actions.add(new GameAction("Use " + item.getName(), false,
                () -> entity.useItem(item, this))); // Should this be passing in a game?
            }
        }
        return actions;
    }

    public List<GameAction> getEntityActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        Location entLoc = entity.getLocation();
        for (Entity ent2 : entities) {
            if (ent2 != entity && ent2.getLocation() == entLoc) {
                actions.add(new GameAction("Attack " + ent2.getName(), true, null));
                // Might add some more actions later, ie, talk()
            }
        }
        return actions;
    }

    public List<GameAction> getMainActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!entity.isAI()) {
                actions.add(new GameAction("1: Look Around", false, null));
            }
            actions.add(new GameAction("2: Move...", false, 
            () -> {
                // And do something with it
                getMoveActions(player);
            }));
            actions.add(new GameAction("3: Inspect...", false, 
            () -> {
                // And do something with it
                getInspectActions(player);
            }));
            actions.add(new GameAction("4: Pick Up / Interact...", false, 
            () -> {
                // And do something with it
                getInteractActions(player);
            }));
            actions.add(new GameAction("5: Use / Equip...", false, 
            () -> {
                // And do something with it
                getInventoryActions(player);
            }));
            actions.add(new GameAction("6: Attack...", false, 
            () -> {
                // And do something with it
                getEntityActions(player);
            }));
            
        }

        return actions;
    }
}
