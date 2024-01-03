package pt.ipp.estg;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.JSON;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Auth {
    private User user;
    private static final Scanner scanner = new Scanner(System.in);

    public Auth() {
    }

    private void displayMenu() {
        System.out.println("[Auth Menu]");
        System.out.println("1 - Sign Up");
        System.out.println("2 - Sign In");
        System.out.println("0 - Exit");
    }

    public void handleAuth() {
        int option;

        do {
            displayMenu();

            System.out.println("Choose an option: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    this.user = signUp();
                    break;
                case 2:
                    this.user = signIn();
                    break;
                case 0:
                    System.out.println("Exiting...");
                default:
                    System.out.println("Invalid option. Please try again!");
                    break;
            }
        } while (option != 0);

    }

    private User signUp() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        System.out.println("Available Roles: [Private], [Sergeant], [Lieutenant], [General]");
        System.out.println("Role: ");
        String role = scanner.nextLine();

        if (!role.equals("Private") && !role.equals("Sergeant") && !role.equals("Lieutenant") && !role.equals("General")) {
            System.out.println("Role not found!");
            return null;
        }

        User user = new User(UUID.randomUUID(), username, email, password, Role.valueOf(role));

        ArrayList<User> users = JSON.loadUsers();

        if (!users.isEmpty()) {
            for (User currentUser : users) {
                if (currentUser.getEmail().equals(user.getEmail())) {
                    System.out.println("Email provided already exists!");
                    return null;
                }
            }
        }

        JSON.saveUser(user);

        System.out.println("User successfully signed up!");

        return user;
    }

    private User signIn() {
        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        ArrayList<User> users = JSON.loadUsers();

        if (users.isEmpty()) {
            System.out.println("Account not found!");
            return null;
        }

        for (User currentUser : users) {
            if (currentUser.getEmail().equals(email)) {
                if (!currentUser.getPassword().equals(password)) {
                    System.out.println("Password incorrect!");
                    return null;
                }
                System.out.println(currentUser.getUsername() + " successfully signed in!");

                return currentUser;
            }
        }

        System.out.println("Email not found!");

        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
