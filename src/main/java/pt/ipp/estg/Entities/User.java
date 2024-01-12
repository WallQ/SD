package pt.ipp.estg.Entities;

import pt.ipp.estg.Enums.Role;

import java.util.Objects;
import java.util.UUID;

/**
 * The {@code User} class represents a user entity with unique identification, username,
 * email, password, and role.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class User {
    /**
     * The unique identifier for the user.
     */
    private UUID id;
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The email address of the user.
     */
    private String email;
    /** The password associated with the user. */
    private String password;
    /** The role of the user (e.g., Public, Private, etc.). */
    private Role role;

    /**
     * Constructs an empty {@code User} object.
     */
    public User() {
    }

    /**
     * Constructs a {@code User} object with the specified username, email, and password.
     * The unique identifier is generated automatically, and the role is set to default (Private).
     *
     * @param username The username of the user.
     * @param email    The email address of the user.
     * @param password The password associated with the user.
     */
    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.Private;
    }

    /**
     * Constructs a {@code User} object with the specified details.
     *
     * @param id       The unique identifier for the user.
     * @param username The username of the user.
     * @param email    The email address of the user.
     * @param password The password associated with the user.
     * @param role     The role of the user.
     */
    public User(UUID id, String username, String email, String password, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier for the user.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The unique identifier for the user.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password associated with the user.
     *
     * @return The password associated with the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password associated with the user.
     *
     * @param password The password associated with the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the role of the user.
     *
     * @return The role of the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The role of the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, role);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}