package com.winterscheidt.hungergames.Classes.Game;

public class GameAction {
    private final String name;
    private final boolean endsTurn;
    private final Runnable execute;

    public GameAction(String name, boolean endsTurn, Runnable execute) {
        this.name = name;
        this.endsTurn = endsTurn;
        this.execute = execute;
    }

    public String getName() {
        return this.name;
    }
    public boolean endsTurn() {
        return this.endsTurn;
    }
    public void execute() {
        this.execute.run();
    }
}
