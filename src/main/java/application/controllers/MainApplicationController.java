package application.controllers;

import application.controllers.alertBox.NumberInputAlertBoxController;
import application.controllers.alertBox.StatisticsPopupController;
import application.controllers.charts.BarChartController;
import application.controllers.charts.LineChartController;
import datatypes.Direction;
import datatypes.Vector2d;
import datatypes.containers.FollowedAnimalStatisticsContainer;
import datatypes.containers.StatisticsContainer;
import datatypes.ui.Cell;
import datatypes.ui.Grid;
import entities.Animal;
import entities.Simulation;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.FileParser;
import util.Parameters;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainApplicationController extends AbstractController implements Initializable {
    // Buttons on the main window
    public Button startButton;
    public Button pauseButton;
    public Button resumeButton;
    public Button stopButton;
    public Button addMapButton;
    public Button loadButton;
    public Button followButton;
    public Button saveStatisticsButton;

    public Pane mapPane;
    // Statistics
    public ListView<String> statisticsList;
    public LineChart<String, Number> populationsAndEnergyChart;
    public LineChart<String, Number> lifespanChart;
    public LineChart<String, Number> childrenChart;
    public BarChart<String, Number> genesChart;

    private LineChartController populationsAndEnergyChartController;
    private LineChartController lifespanChartController;
    private LineChartController childrenChartController;
    private BarChartController genesChartController;
    // Map Grid
    private Grid grid;

    // Main simulation manager
    private Simulation simulationManager;
    // Simulation thread
    Thread simulationThread;
    // For parameters parsing
    private FileParser jsonParser;

    // Parameters of the simulation
    Parameters parameters;

    // Followed animal
    private Animal followedAnimal;
    // If set to -1, then we don't have to show any popups
    private int daysToShowStatisticsPopup;
    // Same as above
    private int daysToSaveStatisticsToAFile;
    // Currently selected Animal
    private Animal selectedAnimal;

    private boolean running;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonParser = new FileParser();
        daysToShowStatisticsPopup = -1;
        daysToSaveStatisticsToAFile = -1;

        startButton.setOnAction(event -> {


            if(simulationThread != null && simulationThread.isAlive()) {
                simulationThread.interrupt();
            }

            // Clearing the charts
            lifespanChart.getData().clear();
            childrenChart.getData().clear();
            populationsAndEnergyChart.getData().clear();
            genesChart.getData().clear();

            // Resetting the countdown until statistics box
            daysToShowStatisticsPopup = -1;

            // Unhighlighting all cells
            grid.unhighlightAll();

            try {
                runSimulation();
                pauseButton.setDisable(false);
                stopButton.setDisable(false);
                startButton.setDisable(true);

                Cell.canBeClicked = false;
            } catch (IllegalArgumentException e) {
                showAlertBox(e.getMessage());
            }
        });

        pauseButton.setOnAction(event -> {
            Cell.canBeClicked = true;
            running = false;
            followButton.setDisable(false);
            resumeButton.setDisable(false);
            saveStatisticsButton.setDisable(false);
        });

        resumeButton.setOnAction(event -> {
            followButton.setDisable(true);
            resumeButton.setDisable(true);
            saveStatisticsButton.setDisable(true);
            running = true;
        });

        stopButton.setOnAction(event -> {
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            stopButton.setDisable(true);
            saveStatisticsButton.setDisable(true);
            followButton.setDisable(true);
            startButton.setDisable(false);

            if(simulationThread != null) {
                simulationThread.interrupt();
            }
        });

        addMapButton.setOnAction(event -> {
            try {
                main.start(new Stage());
            } catch (Exception e) {
                showAlertBox("Something went wrong");
            }
        });

        loadButton.setOnAction(event -> {
            startButton.setDisable(false);
            load();
        });

        followButton.setOnAction(event -> {
            if(selectedAnimal != null) {
                followedAnimal = selectedAnimal;
                simulationManager.setFollowedAnimal(followedAnimal);

                Optional<Integer> dayToShowPopup = loadNumber("Give number of days");
                if(dayToShowPopup.isPresent() && dayToShowPopup.get() > 0) {
                    daysToShowStatisticsPopup = dayToShowPopup.get();
                } else {
                    showAlertBox("Ups! Wrong value!");
                }
            }
        });

        saveStatisticsButton.setOnAction(event -> {
            Optional<Integer> dayToShowPopup = loadNumber("Give number of days");

            if(dayToShowPopup.isPresent() && dayToShowPopup.get() > 0) {
                daysToSaveStatisticsToAFile = dayToShowPopup.get();
            } else {
                showAlertBox("Ups! Wrong value!");
            }
        });
    }

    private void runSimulation() {
        if(parameters != null) {
            Optional<Integer> startingNumberOfAnimalsOptional = loadNumber("Choose the starting number of animals");

            running = true;
            simulationManager = new Simulation(parameters.width, parameters.height, parameters.startEnergy,
                    parameters.plantEnergy, parameters.moveEnergy, parameters.jungleRatio, 32, 8);

            populationsAndEnergyChartController = new LineChartController(populationsAndEnergyChart, "Day",
                    List.of("Animals", "Plants", "Mean Energy"));
            lifespanChartController = new LineChartController(lifespanChart, "Day",
                    List.of("Mean Lifespan"));
            childrenChartController = new LineChartController(childrenChart, "Day",
                    List.of("Avg. number of children"));
            genesChartController = new BarChartController(genesChart);


            simulationThread = new Thread(() -> {
                while(!Thread.currentThread().isInterrupted()){
                    Platform.runLater(() -> {
                        if (running) {
                            try {
                                simulationManager.simulateDay();

                                refreshMap();

                                // Getting the statistics at the ned of the day
                                StatisticsContainer dayStatistics = simulationManager.getCurrentDayStatistics();
                                //System.out.println(dayStatistics.meanLifespan);

                                populationsAndEnergyChartController.addSeriesEntry("Animals",
                                        dayStatistics.currentDay, dayStatistics.numberOfAnimals);
                                populationsAndEnergyChartController.addSeriesEntry("Plants",
                                        dayStatistics.currentDay, dayStatistics.numberOfPlants);
                                populationsAndEnergyChartController.addSeriesEntry("Mean Energy",
                                        dayStatistics.currentDay, dayStatistics.meanEnergyLevel);
                                childrenChartController.addSeriesEntry("Avg. number of children",
                                        dayStatistics.currentDay, dayStatistics.meanNumberOfChildren);
                                lifespanChartController.addSeriesEntry("Mean Lifespan",
                                        dayStatistics.currentDay, dayStatistics.meanLifespan);

                                Map<Direction, Integer> genesCount = dayStatistics.genesCount;
                                Map<String, Number> genesCountWithStringLabels = new LinkedHashMap<>();

                                for(Direction direction : genesCount.keySet()) {
                                    genesCountWithStringLabels.put(direction.toString(), genesCount.get(direction));
                                }

                                genesChartController.updateSeries(genesCountWithStringLabels);

                                if(daysToShowStatisticsPopup == 0) {
                                    running = false;
                                    followButton.setDisable(false);

                                    daysToShowStatisticsPopup = -1;
                                    showStatisticsWindow(simulationManager.getFollowedAnimalStatistics());
                                } else if(daysToShowStatisticsPopup != -1){
                                    daysToShowStatisticsPopup -= 1;
                                }

                                if(daysToSaveStatisticsToAFile == 0) {
                                    running = false;
                                    followButton.setDisable(false);

                                    daysToSaveStatisticsToAFile -= 1;
                                    jsonParser.exportStatistics(simulationManager.getOverallStatistics());
                                    showAlertBox("Statistics saved");
                                } else if(daysToSaveStatisticsToAFile != -1) {
                                    daysToSaveStatisticsToAFile -= 1;
                                }

                                if(simulationManager.getNumberOfAnimals() == 0) {
                                    running = false;

                                    if(daysToShowStatisticsPopup != -1) {
                                        showStatisticsWindow(simulationManager.getFollowedAnimalStatistics());
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                simulationThread.interrupt();
                                showAlertBox("Ups! Something went wrong " + exception.getMessage());
                            }
                        }
                    });

                    try{
                        Thread.sleep(90);
                    } catch(InterruptedException e){
                        break;
                    }
                }
            });

            if(startingNumberOfAnimalsOptional.isPresent()) {
                int startingNumberOfAnimals = startingNumberOfAnimalsOptional.get();
                if (simulationManager != null) {
                    simulationManager.generateAnimalsAtRandomPositions(startingNumberOfAnimals);
                    refreshMap();
                    simulationThread.start();
                }
            }
        }

    }

    private void showStatisticsWindow(FollowedAnimalStatisticsContainer statistics) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatisticsPopup.fxml"));
        Parent layout;

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();

            StatisticsPopupController statisticsPopupController = loader.getController();
            statisticsPopupController.setStage(popupStage);

            StringBuilder builder = new StringBuilder();

            builder.append("Death Date: ");

            if(statistics.deathDay == -1) {
                builder.append("Still alive \n");
            } else {
                builder.append(statistics.deathDay).append("\n");
            }

            builder.append("Number of children: ").append(statistics.numberOfChildren).append("\n");
            builder.append("Number of descendants: ").append(statistics.numberOfDescendants).append("\n");

            statisticsPopupController.setStatisticsText(builder.toString());

            if(this.main!=null) {
                popupStage.initOwner(main.getPrimaryStage());
            }

            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Integer> loadNumber(String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NumberInputAlertBox.fxml"));
        Parent layout;

        Optional<Integer> result = Optional.empty();

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);


            Stage popupStage = new Stage();

            NumberInputAlertBoxController numberInputAlertBoxController = loader.getController();
            numberInputAlertBoxController.setTitleLabel(title);
            numberInputAlertBoxController.setStage(popupStage);

            if(this.main!=null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();

            String numOfStartingAnimalsString = numberInputAlertBoxController.getResults();

            try {
                result = Optional.of(Integer.parseInt(numOfStartingAnimalsString));
                return result;
            } catch (NumberFormatException e) {
                showAlertBox("Given string is not a correct number");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void load() {
        String currentDirectory = System.getProperty("user.dir");

        try {
            parameters = jsonParser.readParameters(currentDirectory + "/parameters.json");
            simulationManager = new Simulation(parameters.width, parameters.height, parameters.startEnergy,
                    parameters.plantEnergy, parameters.moveEnergy, parameters.jungleRatio, 32, 8);

            setMapPane();
            showAlertBox("Parameters loaded");
        } catch (IOException e) {
            showAlertBox("Error while loading parameters. " +
                    "Make sure that the parameters.json file exists in the working directory");
        }
    }

    private void refreshMap() {
        if(grid != null) {
            for(int i = 0; i < parameters.height; i++) {
                for(int j = 0; j < parameters.width; j++) {
                    Vector2d currentPosition = new Vector2d(j, i);

                    Optional<Animal> animalAtPosition = simulationManager.animalAt(currentPosition);

                    if (animalAtPosition.isPresent()) {
                        if(animalAtPosition.get().equals(simulationManager.getFollowedAnimal())) {
                            grid.getCell(j, i).setColour(Color.rgb(255, 201, 54, 1));
                        } else {
                            // We choose the red value based on the animal energy
                            // 0 energy - red
                            // starting energy - brown (153)
                            float energyMultiplayer = (float) animalAtPosition.get().getEnergy() / parameters.startEnergy;
                            int redValue = (int) (255 * energyMultiplayer);
                            int greenValue = (int) (132 * energyMultiplayer);

                            if(redValue > 255) {
                                redValue = 255;
                            }

                            if(greenValue > 132) {
                                greenValue = 132;
                            }

                            grid.getCell(j, i).setColour(Color.rgb(redValue, greenValue, 34, 1));
                        }
                    } else if(simulationManager.plantAt(currentPosition).isPresent()) {
                        grid.getCell(j, i).setColour(Color.rgb(33, 84, 30, 1));
                    } else {
                        grid.getCell(j, i).setColour(Color.rgb(126, 201, 119, 1));
                    }
                }
            }
        }
    }

    private void setMapPane() {
        mapPane.getChildren().clear();

        grid = new Grid(parameters.height, parameters.width, mapPane.getWidth(),
                mapPane.getHeight(), this);

        for(int i = 0; i < parameters.height; i++) {
            for(int j = 0; j < parameters.width; j++) {
                grid.add(j, i);
            }
        }

        mapPane.getChildren().add(grid);
    }

    public void cellHighlighted(Vector2d position) {
        statisticsList.getItems().clear();

        Optional<Animal> animalAtPositionOptional = simulationManager.animalAt(position);

        if(animalAtPositionOptional.isPresent()) {
            Animal animalAtPosition = animalAtPositionOptional.get();
            selectedAnimal = animalAtPosition;

            Map<Direction, Integer> geneCount = animalAtPosition.getGenesCount();

            for(Direction direction : Direction.values()) {
                if(geneCount.containsKey(direction)) {
                    statisticsList.getItems().add("Gene of type " + direction + ": " + geneCount.get(direction));
                }
            }
        } else {
            selectedAnimal = null;
        }
    }

    public void cellUnHighlighted(Vector2d position) {
        statisticsList.getItems().clear();
    }

    public void close() {
        if(simulationThread != null) {
            if(simulationThread.isAlive()) {
                simulationThread.interrupt();
            }
        }
    }
}
