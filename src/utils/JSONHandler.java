package utils;

import Entities.User;
import Enums.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JSONHandler {
    public static void saveUserToJson(User user, String fileName) {
        List<User> existingUsers = loadUsersFromJson(fileName);
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            existingUsers.add(user);
            JSONArray jsonArray = new JSONArray();
            for (User existingUser : existingUsers) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", user.getId().toString());
                jsonObject.put("name", user.getName());
                jsonObject.put("email", user.getEmail());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("role", user.getRole().toString());
                jsonArray.add(jsonObject);
            }
            fileWriter.write(jsonArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsersFromJson(String fileName) {
        List<User> userList = new ArrayList<>();
        try (FileReader fileReader = new FileReader(fileName)) {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(fileReader);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                UUID id = UUID.fromString((String) jsonObject.get("id"));
                String name = (String) jsonObject.get("name");
                String email = (String) jsonObject.get("email");
                String password = (String) jsonObject.get("password");
                Role role = Role.valueOf((String) jsonObject.get("role"));
                userList.add(new User(id, name, email, password, role));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
