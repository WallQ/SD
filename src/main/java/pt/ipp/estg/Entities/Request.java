package pt.ipp.estg.Entities;

import pt.ipp.estg.Enums.Role;

import java.util.Objects;

/**
 * The {@code Request} class represents a request made by a user, including details
 * such as the user, location, reason, and approval status.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Request {
    /**
     * The user making the request.
     */
    private User user;
    /**
     * The location associated with the request.
     */
    private String location;
    /**
     * The reason for the request.
     */
    private String reason;
    /**
     * The approval status of the request.
     */
    private Role approval;

    /**
     * Constructs an empty {@code Request} object.
     */
    public Request() {
    }

    /**
     * Constructs a {@code Request} object with the specified details.
     *
     * @param user     The user making the request.
     * @param location The location associated with the request.
     * @param reason   The reason for the request.
     * @param approval The approval status of the request.
     */
    public Request(User user, String location, String reason, Role approval) {
        this.user = user;
        this.location = location;
        this.reason = reason;
        this.approval = approval;
    }

    /**
     * Gets the user making the request.
     *
     * @return The user making the request.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user making the request.
     *
     * @param user The user making the request.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the location associated with the request.
     *
     * @return The location associated with the request.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location associated with the request.
     *
     * @param location The location associated with the request.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the reason for the request.
     *
     * @return The reason for the request.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the request.
     *
     * @param reason The reason for the request.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the approval status of the request.
     *
     * @return The approval status of the request.
     */
    public Role getApproval() {
        return approval;
    }

    /**
     * Sets the approval status of the request.
     *
     * @param approval The approval status of the request.
     */
    public void setApproval(Role approval) {
        this.approval = approval;
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
        Request request = (Request) o;
        return Objects.equals(user, request.user) && Objects.equals(location, request.location) && Objects.equals(reason, request.reason) && approval == request.approval;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, location, reason, approval);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "Request{" + "user=" + user + ", location='" + location + '\'' + ", reason='" + reason + '\'' + ", approval=" + approval + '}';
    }
}
