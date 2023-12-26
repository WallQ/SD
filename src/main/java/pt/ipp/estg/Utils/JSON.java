package pt.ipp.estg.Utils;

import com.google.gson.Gson;
import pt.ipp.estg.Entities.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

public class JSON {
    private static final String FILE_PATH = Resources.getPathFromResources("users.json");

    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader fileReader = new FileReader(FILE_PATH)) {
            User[] userArray = gson.fromJson(fileReader, User[].class);
            if (userArray != null) users.addAll(Arrays.asList(userArray));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void saveUser(User user) {
        Gson gson = new Gson();
        ArrayList<User> users = loadUsers();

        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            users.add(user);
            gson.toJson(users, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
