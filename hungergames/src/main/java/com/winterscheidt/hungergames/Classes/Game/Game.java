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
    private int remainingTributes;
    private boolean gameOver = false;

    // Getters
    public void openMenu(List<GameAction> menu) {
        this.currentMenu = menu;
        io.displayMenu(currentMenu);
    }
    public List<Entity> getEntities() {
        return this.entities;
    }
    public List<Location> getLocations() {
        return this.locations;
    }
    public Entity getCurrentEntity() {
        return this.currentEntity;
    }
    public GameIO getIO() {
        return this.io;
    }
    public boolean isGameOver() {
        return this.gameOver;
    }

    // Constructors
    public Game(GameIO io) {
        this.io = io;
        ArenaBuilder.build(this);
    }

    // TURN MANAGERS
    // TODO: Need to fix the recursion depth problem here;
    // game will crash if AI fights each other for weeks
    // Additionally, I need to make a better system so that
    // not every action has to reopen main(); use endsTurn flag for this
    /*
    public void startTurn(int index) {
        while (!gameOver) {
            currentEntity = entities.get(index);
            currentEntity.turnStart(this);
            if (currentEntity.getHealth() > 0) {
                if (currentEntity.isAI()) {

                } else {
                    io.broadcast("It's your turn, " + currentEntity.getName(), currentEntity);
                    openMenu(getMainActions(currentEntity));
                    io.displayMenu(currentMenu, currentEntity);
                    return;
                }
            }
            if (remainingTributes <= 1) {
                gameOver = true;
            }
        }
    }
    */

    public void startTurn(int index) {
        startTurn(this.entities.get(index));
    }
    public void startTurn(Entity entity) {
        this.currentEntity = entity;
        entity.turnStart(this);
        if (entity.getHealth() <= 0) {
            endTurn();
            if (this.remainingTributes <= 1) {
                this.gameOver = true;
                return;
            }
        } else {
            io.broadcast("\n\nIt's your turn, " + entity.getName(), entity);
            openMenu(getMainActions(entity));
            turnBody(entity);
        }
    }

    public void turnBody(Entity entity) {
        if (entity.isAI()) {
            GameAction action = entity.chooseAction(currentMenu);
            runAction(action);
            if (!action.endsTurn()) {
                turnBody(entity);
            }
        }
    }

    public void runAction(int index) {
        runAction(currentMenu.get(index));
    }
    // public void runAction(GameAction action) {
    //     action.execute();
    //     if ()
    // }
    // public void runAction(GameAction action) {
    //     action.execute();
    // }

    public void runAction(GameAction action) {
        action.execute();
        if (currentEntity.getHealth() <= 0 || action.endsTurn()) {
            endTurn();
        } else {
//            openMenu(getMainActions(currentEntity));
        }
    }

    public void endTurn() {
        currentEntity.turnEnd(this);
        int previous = this.entities.indexOf(currentEntity);
        if (previous + 1 < this.entities.size()) {
            startTurn(previous + 1);
        } else {
            startTurn(0);
        }
    }


    // MENU DEFINITIONS

    public List<GameAction> getMoveActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        for (String locName : entity.getLocation().getConnections().keySet()) {
            actions.add(new GameAction("Move: " + locName, false,
            () -> {
                entity.move(locName);
                io.broadcast("Moved to " + locName + ".", entity);
                openMenu(getMainActions(entity));
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
                () -> { 
                    io.broadcast(item.getDescription(), entity);
                    openMenu(getMainActions(entity)); 
                }));
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
                    //io.broadcast(entity.takeItem(item) ? "Success" : "Fail");
                    entity.takeItem(item);
                    io.broadcast("Took " + item.getName() + ".", entity);
                }));
            }
            if (item instanceof Interactable) {
                actions.add(new GameAction("Interact with " + item.getName(), true, () -> {
                ((Interactable) item).interact(entity, this);
                io.broadcast("Interacted with " + item.getName(), entity);
            }));
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInventoryActions(Player entity) {
//        io.broadcast("ITEMS: " + entity.getInventory().getItems());
        List<GameAction> actions = new ArrayList<>();
        if (entity.getEquipped() != null && entity.getInventory().getSize() < entity.getInventory().getMaxSize()) {
            actions.add(new GameAction("Unequip " + entity.getEquipped().getName(), false, 
            () -> { 
                if (entity.unequipItem()) { 
                    io.broadcast(entity.getEquipped().getName() + " unequipped.", entity); 
                }
                openMenu(getMainActions(entity));
            }));
        }
        for (Item item : entity.getInventory().getItems()) {
            if (item instanceof Equippable) {
                actions.add(new GameAction("Equip " + item.getName(), false, 
                () -> { 
                    if (entity.equipItem(item)) {
                        io.broadcast(item.getName() + " equipped.", entity); 
                    }
                    openMenu(getMainActions(entity));
                }));
            }
            if (item instanceof Usable) {
                actions.add(new GameAction("Use " + item.getName(), false,
                () -> { 
                    if (entity.useItem(item, this)) {
                        io.broadcast("Used " + item.getName(), entity);
                    }
                    openMenu(getMainActions(entity));
                })); // Should this be passing in a game?
            }
        }
        actions.add(new GameAction("Back", false, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getEntityActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        Location entLoc = entity.getLocation();
        for (Entity ent2 : entities) {
            // Check for same location
            if (ent2 != entity && ent2.getLocation() == entLoc) {
                DetectionLevel detection = entity.getDetection(ent2);
            
                actions.add(new GameAction("Attack " + (detection == DetectionLevel.RECOGNIZED ?
                ent2.getName() : "Obscured Figure"), true, () -> {
                    io.broadcast("You attack " + ent2.getName(), entity);

                    // Is there a weapon?
                    String with = "";
                    if (entity instanceof Player) {
                        if (((Player) entity).getEquipped() != null) {
                            with = " with a " + ((Player) entity).getEquipped().getName();
                        }
                    }

                    // Broadcast to everyone in the area, besides the attacker and victim
                    io.broadcast(entity.getName() + " attacks you" + with + "!", ent2);
                    List<Entity> exclude = new ArrayList<Entity>();
                    exclude.add(entity);
                    exclude.add(ent2);
                    io.broadcast(entity.getName() + " attacks " + ent2.getName() + with + ".", exclude);
                    double damage = entity.attack(ent2, this);

                    if (damage == 0) {
                        io.broadcast("The attack misses!", entity.getLocation(), ent2);
                        io.broadcast("You were able to dodge!", ent2);
                    } else {
                        io.broadcast("You dealt " + damage + " damage.");
                        io.broadcast(entity.getGender().capObject() + " dealt " + damage +
                        " damage to you.");
                        io.broadcast("The attack lands.", entity.getLocation(), exclude);
                    }
                }));
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
            () -> { 
                io.broadcast(player.getLocation().getFullDescription(this, entity), entity); 
                openMenu(getMainActions(entity));
            }));
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
            entity.addHealth(0.05);
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
