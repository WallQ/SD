package pt.ipp.estg.Entities;

import pt.ipp.estg.Enums.Role;

import java.util.Objects;

public class Request {
    private User user;
    private String location;
    private String reason;
    private Role approval;

    public Request() {
    }

    public Request(User user, String location, String reason, Role approval) {
        this.user = user;
        this.location = location;
        this.reason = reason;
        this.approval = approval;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Role getApproval() {
        return approval;
    }

    public void setApproval(Role approval) {
        this.approval = approval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(user, request.user) && Objects.equals(location, request.location) && Objects.equals(reason, request.reason) && approval == request.approval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, location, reason, approval);
    }

    @Override
    public String toString() {
        return "Request{" + "user=" + user + ", location='" + location + '\'' + ", reason='" + reason + '\'' + ", approval=" + approval + '}';
    }
}
