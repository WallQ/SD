package pt.ipp.estg.Utils;

import com.google.gson.Gson;
import pt.ipp.estg.Entities.Logger;
import pt.ipp.estg.Entities.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

public class JSON {
    private static final String USERS_FILE_PATH = Resources.getPathFromResources("users.json");
    private static final String LOGS_FILE_PATH = Resources.getPathFromResources("logs.json");

    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader fileReader = new FileReader(USERS_FILE_PATH)) {
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

        try (FileWriter fileWriter = new FileWriter(USERS_FILE_PATH)) {
            users.add(user);
            gson.toJson(users, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Logger.LogEntry> loadLogs() {
        ArrayList<Logger.LogEntry> logs = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader fileReader = new FileReader(LOGS_FILE_PATH)) {
            Logger.LogEntry[] logsArray = gson.fromJson(fileReader, Logger.LogEntry[].class);
            if (logsArray != null) logs.addAll(Arrays.asList(logsArray));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public static void saveLog(Logger.LogEntry logEntry) {
        Gson gson = new Gson();
        ArrayList<Logger.LogEntry> logEntries = loadLogs();

        try (FileWriter fileWriter = new FileWriter(LOGS_FILE_PATH)) {
            logEntries.add(logEntry);
            gson.toJson(logEntries, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
