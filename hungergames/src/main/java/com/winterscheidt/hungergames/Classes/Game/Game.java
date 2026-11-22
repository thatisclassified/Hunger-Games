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
import com.winterscheidt.hungergames.Interfaces.GameIO;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.Usable;
import com.winterscheidt.hungergames.Utils.DetectionLevel;

public class Game implements Serializable {
    private List<Location> locations = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private List<GameAction> currentMenu;
    private Entity currentEntity;
    private GameIO io = ConsoleIO.io;

    public void openMenu(List<GameAction> menu) {
        this.currentMenu = menu;
        io.displayMenu(currentMenu);
    }

    public List<Entity> getEntities() {
        return this.entities;
    }
    public Entity getCurrentEntity() {
        return this.currentEntity;
    }

    public DetectionLevel getDetection(Entity observer, Entity target) {
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


    // TURN MANAGERS
    public void startTurn(Entity entity) {
        this.currentEntity = entity;
        entity.turnStart(this);
        io.broadcast("It's your turn, " + entity.getName(), entity);
        openMenu(getMainActions(entity));
    }

    public void runAction(int index) {
        runAction(currentMenu.get(index));
    }
    public void runAction(GameAction action) {
        action.execute();
        if (currentEntity.getHealth() <= 0 || action.endsTurn()) {
            endTurn();
        } else {
            openMenu(getMainActions(currentEntity));
        }
    }

    public void endTurn() {
        currentEntity.turnEnd(this);
    }


    // MENU DEFINITIONS

    public List<GameAction> getMoveActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        for (String locName : entity.getLocation().getConnections().keySet()) {
            actions.add(new GameAction("Move: " + locName, false,
            () -> {
                entity.move(locName);
                io.broadcast("Moved to " + locName + ".", entity);
            }));
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInspectActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (!entity.isAI()) {
            for (Item item : entity.getLocation().getItems()) {
                actions.add(new GameAction("Inspect " + item.getName(), false, 
                () -> io.broadcast(item.getDescription())));
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInteractActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        for (Item item : entity.getLocation().getItems()) {
            if (item instanceof Attainable) {
                actions.add(new GameAction("Pick up " + item.getName(), true, () -> {
                    entity.takeItem(item);
                    io.broadcast("Took " + item.getName() + ".", entity);
                }));
            }
            if (item instanceof Interactable) {
                actions.add(new GameAction("Interact with " + item.getName(), true, () -> 
                ((Interactable) item).interact(entity, this)));
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInventoryActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (entity.getEquipped() != null && entity.getInventory().getSize() < entity.getInventory().getMaxSize()) {
            actions.add(new GameAction("Unequip " + entity.getEquipped().getName(), false, 
            () -> { 
                entity.unequipItem(); 
                io.broadcast(entity.getEquipped().getName() + " unequipped.", entity); 
            }));
        }
        for (Item item : entity.getInventory().getItems()) {
            if (item instanceof Equippable) {
                actions.add(new GameAction("Equip " + item.getName(), false, 
                () -> { 
                    entity.equipItem(item); 
                    io.broadcast(item.getName() + " equipped.", entity); 
                }));
            }
            if (item instanceof Usable) {
                actions.add(new GameAction("Use " + item.getName(), false,
                () -> entity.useItem(item, this))); // Should this be passing in a game?
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getEntityActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        Location entLoc = entity.getLocation();
        for (Entity ent2 : entities) {
            if (ent2 != entity && ent2.getLocation() == entLoc) {
                DetectionLevel detection = getDetection(entity, ent2);
                if (detection == DetectionLevel.RECOGNIZED) {
                    actions.add(new GameAction("Attack " + ent2.getName(), true, null));
                } else if (detection == DetectionLevel.SPOTTED) {
                    actions.add(new GameAction("Attack Obscured Figure", true, null));
                }
                // Might add some more actions later, ie, talk()
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getMainActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        Player player = entity instanceof Player ? (Player) entity : null;

        // LOOK AROUND
        if (player != null && !entity.isAI()) {
            actions.add(new GameAction("Look Around", false, 
            () -> io.broadcast(player.getLocation().getFullDescription(this), entity)));
        }

        // MOVE
        if (entity.canMove()) {
            actions.add(new GameAction("Move...", false, // This should check canMove(),
            () -> openMenu(getMoveActions(entity)))); // and be available for all entities
        }

        // INSPECT
        if (player != null) { 
            if (!entity.isAI()) {
                actions.add(new GameAction("Inspect...", false, 
                () -> openMenu(getInspectActions(player))));
            }
            actions.add(new GameAction("Pick Up / Interact...", false, 
            () -> openMenu(getInteractActions(player))));
            actions.add(new GameAction("Use / Equip...", false, 
            () -> openMenu(getInventoryActions(player))));
        }
            
        // ATTACK
        actions.add(new GameAction("Attack...", false, // Same with attack and hide
        () -> openMenu(getEntityActions(entity))));
        
        // REST
        actions.add(new GameAction("Rest", true, () -> {
            entity.lerpObscurity(0.2);
            entity.addHealth(0.03);
            if (player != null) {
                player.addHunger(-0.02);
                player.addThirst(-0.02);
            }
        }));

        // HIDE
        actions.add(new GameAction("Hide", true, 
        () -> {
            entity.lerpObscurity(0.7);
            io.broadcast("Hidden.", entity);
        }));

        return actions;
    }
}
