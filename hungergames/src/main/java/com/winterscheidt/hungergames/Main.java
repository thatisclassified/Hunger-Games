package com.winterscheidt.hungergames;

import java.util.Scanner;

import com.winterscheidt.hungergames.Classes.Game.ArenaBuilder;
import com.winterscheidt.hungergames.Classes.Game.ConsoleIO;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.GameIO;

public class Main {
    public static void main(String[] args) {
        GameIO io = ConsoleIO.io;
        Game game = new Game(io);
        ArenaBuilder.build(game);
        io.broadcast("Ladies and Gentlemen, Welcome to the 0th Annual Hunger Games!");
        
        Scanner input = new Scanner(System.in);
        game.startTurn(0);
        // Game loop, by AI
        while (!game.isGameOver()) {
            System.out.print("\nChoose an action number: ");
            if (input.hasNextInt()) {
                int choice = input.nextInt();
                game.runAction(choice - 1); // Executes choice; if turn-ending, advances to next human!
            } else {
                input.next(); // Clear invalid text input
                System.out.println("Please enter a valid number.");
            }
        }
        input.close();
    }        
}