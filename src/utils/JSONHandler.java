package utils;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONHandler {
    public static JSONArray readJSONArrayFromFile(String fileName) throws IOException, ParseException {
        return (JSONArray) new JSONParser().parse(new FileReader("data/" + fileName + ".json"));
    }

    public static void writeToJSONFile(String jsonString, String fileName) throws IOException {
        FileWriter file = new FileWriter("data/" + fileName + ".json");
        file.write(jsonString);
        file.close();
    }
}
