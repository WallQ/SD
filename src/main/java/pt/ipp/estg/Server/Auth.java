package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.JSON;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The {@code Auth} class provides authentication functionality including user sign-up
 * and sign-in methods for managing user access to the application.
 * It interacts with user entities and role enumeration for user management.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Auth {
    /**
     * Signs up a new user with the specified username, email, password, and role.
     *
     * @param username The username of the new user.
     * @param email    The email address of the new user.
     * @param password The password of the new user.
     * @param role     The role of the new user (e.g., Private, Sergeant, Lieutenant, General).
     * @return The newly signed-up user if successful; {@code null} if the email is already in use.
     */
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

    /**
     * Signs in a user with the specified email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return The signed-in user if successful; {@code null} if the credentials are invalid.
     */
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
