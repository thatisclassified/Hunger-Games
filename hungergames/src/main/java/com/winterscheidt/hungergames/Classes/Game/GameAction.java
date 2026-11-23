package com.winterscheidt.hungergames.Classes.Game;

public class GameAction {
    private final String name;
    private final boolean endsTurn;
    private final boolean subMenu;
    private final Runnable execute;

    public GameAction(String name, boolean endsTurn, boolean subMenu, Runnable execute) {
        this.name = name;
        this.endsTurn = endsTurn;
        this.subMenu = subMenu;
        this.execute = execute;
    }

    public String getName() {
        return this.name;
    }
    public boolean endsTurn() {
        return this.endsTurn;
    }
    public boolean opensSubMenu() {
        return this.subMenu;
    }
    public void execute() {
        this.execute.run();
    }
}
