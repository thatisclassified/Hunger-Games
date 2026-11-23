package com.winterscheidt.hungergames.Classes.Game;

import java.util.List;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Interfaces.GameIO;

public class ConsoleIO implements GameIO {

    public static GameIO io = new ConsoleIO();

    private ConsoleIO() {}

    @Override
    public void broadcast(String message) {
        System.out.println(message);
    }

    @Override
    public void broadcast(String message, Location location) {
        // Will implement this more later; broadcast only to entities wihin the specified location
        broadcast(message);
    }

    @Override
    public void displayMenu(List<GameAction> items) {
        for (GameAction item : items) {
            System.out.println(item.getName());
        }
    }

    @Override
    public void broadcast(String message, Entity entity) {
        // Implement later
        broadcast(message);
    }

    @Override
    public void displayMenu(List<GameAction> items, Entity entity) {
        // Implement later
        displayMenu(items);
    }

    @Override
    public void broadcast(String message, Location location, Entity exclude) {
        // Implement later
        broadcast(message);
    }

    @Override
    public void broadcast(String message, List<Entity> entities) {
        // Implement later
        broadcast(message);
    }

    @Override
    public void broadcast(String message, Location location, List<Entity> exclude) {
        // Implement later
        broadcast(message);
    }
    
}
