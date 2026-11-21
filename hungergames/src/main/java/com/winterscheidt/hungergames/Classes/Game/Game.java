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
import com.winterscheidt.hungergames.Utils.DetectionLevel;

public class Game implements Serializable {
    private List<Location> locations = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private List<GameAction> currentMenu;

    public void openMenu(List<GameAction> menu) {
        this.currentMenu = menu;
        // Notify your UI/Discord listener to render 'currentMenu'
    }

    public DetectionLevel checkDetection(Entity observer, Entity target) {
        // Roll perception: base perception + random roll (e.g., 0.0 to 1.0)
        double roll = observer.getPerception() + Math.random(); 
        double effectiveObscurity = Math.min(0.95, target.getObscurity());
        
        double margin = roll - effectiveObscurity;

        if (margin >= 0.4) {
            return DetectionLevel.RECOGNIZED; // "You see Cato hiding near the rocks."
        } else if (margin >= 0.0) {
            return DetectionLevel.SPOTTED;    // "You spot a shadowy figure moving in the brush."
        } else {
            return DetectionLevel.HIDDEN;     // Completely unseen!
        }
    }


    // MENU DEFINITIONS

    public List<GameAction> getMoveActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        for (String locName : entity.getLocation().getConnections().keySet()) {
            actions.add(new GameAction("Move: " + locName, true,
            () -> entity.move(locName)));
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInspectActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (!entity.isAI()) {
            for (Item item : entity.getLocation().getItems()) {
                actions.add(new GameAction("Inspect " + item.getName(), false, null));
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInteractActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        for (Item item : entity.getLocation().getItems()) {
            if (item instanceof Attainable) {
                actions.add(new GameAction("Pick up " + item.getName(), true, null));
            }
            if (item instanceof Interactable) {
                actions.add(new GameAction("Interact with " + item.getName(), true, null));
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInventoryActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (entity.getEquipped() != null && entity.getInventory().getSize() < entity.getInventory().getMaxSize()) {
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
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
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
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getMainActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!entity.isAI()) {
                actions.add(new GameAction("Look Around", false, null));
            }
            actions.add(new GameAction("Move...", false, // This should check canMove(),
            () -> openMenu(getMoveActions(player)))); // and be available for all entities
            if (!entity.isAI()) {
                actions.add(new GameAction("Inspect...", false, 
                () -> openMenu(getInspectActions(player))));
            }
            actions.add(new GameAction("Pick Up / Interact...", false, 
            () -> openMenu(getInteractActions(player))));
            actions.add(new GameAction("Use / Equip...", false, 
            () -> openMenu(getInventoryActions(player))));
            actions.add(new GameAction("Attack...", false, // Same with attack and hide
            () -> openMenu(getEntityActions(player))));
            actions.add(new GameAction("Rest", true, () -> {
                player.lerpObscurity(0.2);
                player.addHunger(-0.02);
                player.addThirst(-0.02);
                player.addHealth(0.03);
            }));
            actions.add(new GameAction("Hide", true, 
            () -> player.lerpObscurity(0.7)));
            
        }
        return actions;
    }
}
