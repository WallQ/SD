package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.JSON;

import java.util.ArrayList;
import java.util.UUID;

public class Auth {
    public static User signUp(String username, String email, String password, String role) {
        ArrayList<User> users = JSON.loadUsers();

        User user = new User(UUID.randomUUID(), username, email, password, Role.valueOf(role));

        if (!users.isEmpty()) {
            for (User currentUser : users) {
                if (currentUser.getEmail().equals(user.getEmail())) {
                    return null;
                }
            }
        }

        JSON.saveUser(user);

        return user;
    }

    public static User signIn(String email, String password) {
        ArrayList<User> users = JSON.loadUsers();

        if (users.isEmpty()) return null;

        for (User currentUser : users) {
            if (currentUser.getEmail().equals(email) && currentUser.getPassword().equals(password)) {
                return currentUser;
            }
        }

        return null;
    }
}
