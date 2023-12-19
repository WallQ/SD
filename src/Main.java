import Entities.User;
import Enums.Role;
import utils.JSONHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        User user1 = new User(UUID.randomUUID(), "Sergio Felix", "8200615@estg.ipp.pt", "12345", Role.Captain);
        User user2 = new User(UUID.randomUUID(), "Carlos Leite", "8200377@estg.ipp.pt", "54321", Role.Captain);

        userList.add(user1);
        userList.add(user2);

        JSONHandler.saveUserToJson(userList, "data/users.json");

        List<User> users = JSONHandler.loadUsersFromJson("data/users.json");
        for (User user : users) {
            System.out.println(user);
        }
    }
}