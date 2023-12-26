package pt.ipp.estg.sd;

import Entities.User;
import Enums.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        User user1 = new User(UUID.randomUUID(), "Sergio Felix", "8200615@estg.ipp.pt", "12345", Role.Captain);
        User user2 = new User(UUID.randomUUID(), "Carlos Leite", "8200377@estg.ipp.pt", "54321", Role.Captain);

        saveUser(user1);
        saveUser(user2);

        ArrayList<User> users = readUsers();
        System.out.println(users);
    }

    public static ArrayList<User> readUsers() throws IOException, ParseException {
        ArrayList<User> users = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        Reader reader = new FileReader("users.json");

        JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

        for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
            UUID id = UUID.fromString((String) jsonObject.get("id"));
            String name = (String) jsonObject.get("name");
            String email = (String) jsonObject.get("email");
            String password = (String) jsonObject.get("password");
            Role role = Role.valueOf((String) jsonObject.get("role"));
            User user = new User(id, name, email, password, role);
            users.add(user);
        }

        reader.close();

        return users;
    }

    public static void saveUser(User user) throws IOException, ParseException {
        ArrayList<User> users = new ArrayList<>();

        users = readUsers();

        JSONArray jsonArray = new JSONArray();
        for (User currentUser : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", currentUser.getId());
            jsonObject.put("name", currentUser.getName());
            jsonObject.put("email", currentUser.getEmail());
            jsonObject.put("password", currentUser.getPassword());
            jsonObject.put("role", currentUser.getRole().toString());
            jsonArray.add(jsonObject);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("role", user.getRole().toString());

        jsonArray.add(jsonObject);

        Writer fileWriter = new FileWriter("users.json");
        fileWriter.write(jsonArray.toJSONString());

        fileWriter.flush();
        fileWriter.close();
    }
}
