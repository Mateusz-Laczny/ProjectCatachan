package application;

import datatypes.Vector2d;
import datatypes.observer.IAnimalStateObserver;
import entities.Animal;
import entities.Simulation;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Parameters;
import util.ParametersParser;
import util.tasks.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainApplicationController extends AbstractController implements Initializable, IAnimalStateObserver {
    // Buttons on the main window
    public Button startButton;
    public Button pauseButton;
    public Button resumeButton;
    public Button stopButton;
    public Button addMapButton;
    public Button showStatisticsButton;
    public Button loadButton;
    public Button followButton;

    public Pane mapPane;
    public ListView<String> statisticsList;
    // Map cells
    ImageView[][] mapCells;
    private Grid grid;

    public Label currentStageLabel;

    // Main simulation manager
    private Simulation simulationManager;
    // Simulation thread
    Thread taskThread;
    // For parameters parsing
    private ParametersParser parametersParser;
    // Tasks for simulation manager
    AbstractSimulationTask removeDeadTask;
    AbstractSimulationTask moveTask;
    AbstractSimulationTask eatTask;
    AbstractSimulationTask reproduceTask;
    AbstractSimulationTask generatePlantsTask;

    // Parameters of the simulation
    Parameters parameters;

    // Map images
    Image background = new Image(getClass().getResource("/images/dirt.jpg").toExternalForm());
    Image animal = new Image(getClass().getResource("/images/sheepWithoutGrass.jpg").toExternalForm());
    Image plant = new Image(getClass().getResource("/images/grass.png").toExternalForm());

    private boolean running;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parametersParser = new ParametersParser();

        startButton.setOnAction(event -> {
            runSimulation();
        });

        pauseButton.setOnAction(event -> {
            running = false;
            followButton.setDisable(false);
        });

        resumeButton.setOnAction(event -> {
            running = true;
        });

        // TODO
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

        // TODO
        followButton.setOnAction(event -> {

        });
    }

    private void runSimulation() {
        Optional<Integer> startingNumberOfAnimalsOptional = loadNumberOfAnimals();

        running = true;

        taskThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (running) {
                                try {
                                    removeDeadTask.call();
                                    refreshMap();

                                    moveTask.call();
                                    refreshMap();

                                    eatTask.call();
                                    refreshMap();

                                    reproduceTask.call();
                                    refreshMap();

                                    generatePlantsTask.call();
                                    refreshMap();

                                    if(simulationManager.getNumberOfAnimals() == 0) {
                                        System.out.println("Ping");
                                        running = false;
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    showAlertBox("Ups! Something went wrong " + exception.getMessage());
                                }
                            }
                        }
                    });
                    try{
                        Thread.sleep(300);
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

    private Optional<Integer> loadNumberOfAnimals() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NumberInputAlertBox.fxml"));
        Parent layout;

        Optional<Integer> result = Optional.empty();

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            //scene.getStylesheets().add(getClass().getResource("InputBoxStyle.css").toExternalForm());
            Stage popupStage = new Stage();

            NumberInputAlertBoxController numberInputAlertBoxController = loader.getController();
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
                    parameters.plantEnergy, parameters.moveEnergy, parameters.jungleRatio);

            removeDeadTask = new RemoveDeadAnimalsTask(simulationManager);
            moveTask = new MoveAnimalsTask(simulationManager);
            eatTask = new EatPlantsTask(simulationManager);
            reproduceTask = new ReproduceAnimalsTask(simulationManager);
            generatePlantsTask = new GeneratePlantsTask(simulationManager);

            removeDeadTask.setOnScheduled(event -> {
                setStageLabelText("Removing dead animals");
            });

            moveTask.setOnScheduled(event -> {
                setStageLabelText("Moving animals");
            });

            eatTask.setOnScheduled(event -> {
                setStageLabelText("Eating plants");
            });

            reproduceTask.setOnScheduled(event -> {
                setStageLabelText("Reproducing");
            });

            generatePlantsTask.setOnScheduled(event -> {
                setStageLabelText("Generating plants");
            });


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

                    if (simulationManager.animalAt(currentPosition).isPresent()) {
                        grid.getCell(j, i).setImage(animal);
                    } else if(simulationManager.plantAt(currentPosition).isPresent()) {
                        grid.getCell(j, i).setImage(plant);
                    } else {
                        grid.getCell(j, i).setImage(background);
                    }
                }
            }
        }
    }

    private void setStageLabelText(String text) {
        currentStageLabel.setText(text);
    }

    // TODO
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

    }

    public void cellUnHighlighted(Vector2d position) {

    }

    public boolean isRunning() {
        return running;
    }

    public void close() {
        if(taskThread != null) {
            if(taskThread.isAlive()) {
                taskThread.interrupt();
            }
        }
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {

    }

    @Override
    public void animalDied(Animal deadAnimal) {

    }
}
