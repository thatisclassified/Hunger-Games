package com.winterscheidt.hungergames.Interfaces;

import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Game.Location;

public abstract interface GameIO {
    public void broadcast(String message);
    public void broadcast(String message, Location location);
    public void broadcast(String message, Location location, Entity exclude);
    public void broadcast(String message, Entity entity);
    public void broadcast(String message, List<Entity> entities);
    public void broadcast(String message, Location location, List<Entity> exclude);
    public void displayMenu(List<GameAction> items);
    public void displayMenu(List<GameAction> items, Entity entity);
}
