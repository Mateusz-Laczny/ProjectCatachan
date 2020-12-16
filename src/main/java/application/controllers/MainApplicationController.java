package application.controllers;

import datatypes.OneDayStatistics;
import datatypes.ui.Cell;
import datatypes.ui.Grid;
import datatypes.Direction;
import datatypes.Vector2d;
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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Parameters;
import util.ParametersParser;

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

    public Pane mapPane;
    // Statistics
    public ListView<String> statisticsList;
    public LineChart<String, Number> populationsChart;
    public LineChart<String, Number> energyAndChildrenChart;
    public BarChart<String, Number> genesChart;

    private LineChartController populationsChartController;
    private LineChartController energyAndChildrenChartController;
    private BarChartController genesChartController;
    // Map Grid
    private Grid grid;

    // Main simulation manager
    private Simulation simulationManager;
    // Simulation thread
    Thread taskThread;
    // For parameters parsing
    private ParametersParser parametersParser;

    // Parameters of the simulation
    Parameters parameters;

    // Map images
    Image background = new Image(getClass().getResource("/images/dirt.jpg").toExternalForm());
    Image animal = new Image(getClass().getResource("/images/sheepWithoutGrass.jpg").toExternalForm());
    Image animalFollowed = new Image(getClass().getResource("/images/followedAnimal.png").toExternalForm());
    Image plant = new Image(getClass().getResource("/images/grass.png").toExternalForm());

    // Followed animal
    private Animal followedAnimal;
    // Currently selected Animal
    private Animal selectedAnimal;

    private boolean running;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parametersParser = new ParametersParser();

        startButton.setOnAction(event -> runSimulation());

        pauseButton.setOnAction(event -> {
            Cell.canBeClicked = true;
            running = false;
            followButton.setDisable(false);
        });

        resumeButton.setOnAction(event -> {
            followButton.setDisable(true);
            running = true;
        });

        stopButton.setOnAction(event -> {
            if(taskThread != null) {
                taskThread.interrupt();
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
            load();
        });

        followButton.setOnAction(event -> {
            if(selectedAnimal != null) {
                followedAnimal = selectedAnimal;
                simulationManager.setFollowedAnimal(followedAnimal);
            }
        });
    }

    private void runSimulation() {
        // Disabling the option to highlight cells
        Cell.canBeClicked = false;
        // Disabling the follow button
        followButton.setDisable(true);

        if(taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt();
        }

        // Clearing the charts
        energyAndChildrenChart.getData().clear();
        populationsChart.getData().clear();
        genesChart.getData().clear();

        if(parameters != null) {
            Optional<Integer> startingNumberOfAnimalsOptional = loadNumber("Choose the starting number of animals");

            running = true;
            simulationManager = new Simulation(parameters.width, parameters.height, parameters.startEnergy,
                    parameters.plantEnergy, parameters.moveEnergy, parameters.jungleRatio, 32, 8);

            populationsChartController = new LineChartController(populationsChart, "Day",
                    List.of("Animals", "Plants"));
            energyAndChildrenChartController = new LineChartController(energyAndChildrenChart, "Day",
                    List.of("Mean Energy", "Mean num. of children", "Mean Lifespan"));
            genesChartController = new BarChartController(genesChart);


            taskThread = new Thread(new Runnable() {
                public void run() {
                    while(!Thread.currentThread().isInterrupted()){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (running) {
                                    try {
                                        simulationManager.removeDeadAnimals();
                                        refreshMap();

                                        simulationManager.moveAnimals();
                                        refreshMap();

                                        simulationManager.eatPlants();
                                        refreshMap();

                                        simulationManager.reproduceAnimals();
                                        refreshMap();

                                        simulationManager.generatePlants();
                                        refreshMap();

                                        // Getting the statistics at the ned of the day
                                        OneDayStatistics dayStatistics = simulationManager.getCurrentDayStatistics();

                                        populationsChartController.addSeriesEntry("Animals",
                                                dayStatistics.currentDay, dayStatistics.numberOfAnimals);

                                        populationsChartController.addSeriesEntry("Plants",
                                                dayStatistics.currentDay, dayStatistics.numberOfPlants);

                                        energyAndChildrenChartController.addSeriesEntry("Mean Energy",
                                                dayStatistics.currentDay, dayStatistics.meanEnergyLevel);

                                        energyAndChildrenChartController.addSeriesEntry("Mean num. of children",
                                                dayStatistics.currentDay, dayStatistics.meanNumberOfChildren);

                                        energyAndChildrenChartController.addSeriesEntry("Mean Lifespan",
                                                dayStatistics.currentDay, dayStatistics.meanLifespan);

                                        Map<Direction, Integer> genesCount = dayStatistics.genesCount;
                                        Map<String, Number> genesCountWithStringLabels = new LinkedHashMap<>();

                                        for(Direction direction : genesCount.keySet()) {
                                            genesCountWithStringLabels.put(direction.toString(), genesCount.get(direction));
                                        }

                                        genesChartController.updateSeries(genesCountWithStringLabels);

                                        if(simulationManager.getNumberOfAnimals() == 0) {
                                            running = false;
                                        }
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                        taskThread.interrupt();
                                        showAlertBox("Ups! Something went wrong " + exception.getMessage());
                                    }
                                }
                            }
                        });

                        try{
                            Thread.sleep(90);
                        } catch(InterruptedException e){
                            break;
                        }
                    }
                }
            });

            if(startingNumberOfAnimalsOptional.isPresent()) {
                int startingNumberOfAnimals = startingNumberOfAnimalsOptional.get();
                if (simulationManager != null) {
                    simulationManager.generateAnimalsAtRandomPositions(startingNumberOfAnimals);
                    refreshMap();
                    taskThread.start();
                }
            }
        }

    }

    private Optional<Integer> loadNumber(String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NumberInputAlertBox.fxml"));
        Parent layout;

        Optional<Integer> result = Optional.empty();

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            //scene.getStylesheets().add(getClass().getResource("InputBoxStyle.css").toExternalForm());
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
            parameters = parametersParser.readParameters(currentDirectory + "/parameters.json");
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
                            grid.getCell(j, i).setImage(animalFollowed);
                        } else {
                            grid.getCell(j, i).setImage(animal);
                        }
                    } else if(simulationManager.plantAt(currentPosition).isPresent()) {
                        grid.getCell(j, i).setImage(plant);
                    } else {
                        grid.getCell(j, i).setImage(background);
                    }
                }
            }
        }
    }

    private void setMapPane() {
        mapPane.getChildren().clear();

        Image background = new Image(getClass().getResource("/images/dirt.jpg").toExternalForm());

        grid = new Grid(parameters.height, parameters.width, mapPane.getWidth(),
                mapPane.getHeight(), this);

        for(int i = 0; i < parameters.height; i++) {
            for(int j = 0; j < parameters.width; j++) {
                grid.add(j, i, background);
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
        }
    }

    public void cellUnHighlighted(Vector2d position) {
        statisticsList.getItems().clear();
    }

    public void close() {
        if(taskThread != null) {
            if(taskThread.isAlive()) {
                taskThread.interrupt();
            }
        }
    }
}
