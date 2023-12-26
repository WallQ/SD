package pt.ipp.estg;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.JSON;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Auth {
    public static void AuthMenu() {
        System.out.println("[Auth Menu]");
        System.out.println("1 - Sign Up");
        System.out.println("2 - Sign In");
        System.out.println("0 - Exit");
    }

    public static User signUp() {
        ArrayList<User> users;

        users = JSON.loadUsers();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Name: ");
        String name = scanner.nextLine();

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

        User user = new User(UUID.randomUUID(), name, email, password, Role.valueOf(role));

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

    public static User signIn() {
        ArrayList<User> users;

        users = JSON.loadUsers();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        if (users.isEmpty()) {
            System.out.println("Account not found!");
            return null;
        }

        for (User currentUser : users) {
            if (!currentUser.getEmail().equals(email)) {
                System.out.println("Email not found!");
                return null;
            }

            if (!currentUser.getPassword().equals(password)) {
                System.out.println("Password incorrect!");
                return null;
            }

            System.out.println(currentUser.getName() + " successfully signed in!");

            return currentUser;
        }

        return null;
    }
}
