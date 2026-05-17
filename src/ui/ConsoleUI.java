package ui;

public class ConsoleUI {
    public void showMessage(String message){
        System.out.println(message);
    }

    public void showDividerLine(){
        System.out.println("===================================");
    }

    public void showTitle(String title){
        showDividerLine();
        System.out.println(title);
        showDividerLine();
    }
}

