import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== RPG Game ===\n");

        System.out.println("1. New Game\n2. Load Game\n3. Exit\n");

        int choice = 0;

        try {
            System.out.println("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.close();
            return;
        }

        GameManager gameManager = new GameManager();
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
                System.out.println("Invalid choice. Please try again.");
                break;
        }
        scanner.close();
    }
}
