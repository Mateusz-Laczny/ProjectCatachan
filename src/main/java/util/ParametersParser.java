package util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import entities.Simulation;

import java.io.FileReader;
import java.io.IOException;

public class ParametersParser {
    private final Gson gson;

    public ParametersParser() {
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
        Parameters parameters = null;

        try (JsonReader reader = gson.newJsonReader(new FileReader(filePath))){
            parameters = gson.fromJson(reader, Parameters.class);
        }

        return parameters;
    }
}
