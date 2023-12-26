package pt.ipp.estg;

import pt.ipp.estg.Entities.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        User user = null;

        Auth.AuthMenu();

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        while (option != 0) {
            switch (option) {
                case 1:
                    user = Auth.signUp();
                    break;
                case 2:
                    user = Auth.signIn();
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }

            if (user != null) break;

            Auth.AuthMenu();

            option = scanner.nextInt();
        }
    }
}