package main;
import java.util.Scanner;

import game.GameManager;
import game.SaveData;
import game.SaveManager;
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ConsoleUI ui = new ConsoleUI();

        ui.showTitle("RPG Game");

        System.out.println("1. New Game\n2. Load Game\n3. Exit\n");

        int choice = 0;

        try {
            System.out.println("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            ui.showMessage("Invalid input. Please enter a number.");
            scanner.close();
            return;
        }

        GameManager gameManager = new GameManager(scanner);
        SaveManager saveManager = new SaveManager();

        switch (choice) {
            case 1:
                gameManager.startGame();
                break;
            case 2:
                SaveData saveData = saveManager.loadGame();

                if(saveData != null){
                    gameManager.loadSaveGame(saveData);
                    gameManager.startLoadedGame();
                }
                break;
            case 3:
                System.out.println("Goodbye!");
                break;
            default:
                ui.showMessage("Invalid choice.");
                break;
        }
        scanner.close();
    }
}
