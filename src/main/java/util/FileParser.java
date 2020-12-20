package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import datatypes.containers.StatisticsContainer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileParser {
    private final Gson gson;

    public FileParser() {
        gson = new Gson();
    }

    /**
     * Reads starting parameters of the simulation from a JSON file.
     * The file must be of format:
     * {
     *   "width": ..,
     *   "height": ..,
     *   "startEnergy": ..,
     *   "plantEnergy": ..,
     *   "moveEnergy": ..,
     *   "jungleRatio": ..
     * }
     * @param filePath
     *      Path to the JSON file
     * @return
     *      Parameters object containing the information
     * @throws IOException
     *      If the reading of the file failed
     */
    public Parameters readParameters(String filePath) throws IOException{
        Parameters parameters;

        try (JsonReader reader = gson.newJsonReader(new FileReader(filePath))){
            parameters = gson.fromJson(reader, Parameters.class);
        }

        return parameters;
    }

    public void exportStatistics(StatisticsContainer container) {
        String currentDirectory = System.getProperty("user.dir");

        try {
            Writer writer = new FileWriter(currentDirectory + "/statistics.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(container, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
