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

    public Pane mapPane;
    // Map cells
    ImageView[][] mapCells;

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
        });

        resumeButton.setOnAction(event -> {
            running = true;
        });

        stopButton.setOnAction(event -> {
            stopSimulation();
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
    }

    private void runSimulation() {
        Optional<Integer> startingNumberOfAnimalsOptional = loadNumberOfAnimals();

        running = true;

        if(startingNumberOfAnimalsOptional.isPresent()) {
            int startingNumberOfAnimals = startingNumberOfAnimalsOptional.get();
            if (simulationManager != null) {
                simulationManager.generateAnimalsAtRandomPositions(startingNumberOfAnimals);
                refreshMap();
                taskThread.start();
            }
        }
    }

    private void pauseSimulation() {
        running = false;
    }

    private void stopSimulation() {
        mapPane.setStyle("-fx-background-color: blue");
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

            removeDeadTask.setOnRunning(event -> {
                setStageLabelText("Removing dead animals");
            });

            moveTask.setOnRunning(event -> {
                setStageLabelText("Moving animals");
            });

            eatTask.setOnRunning(event -> {
                setStageLabelText("Eating plants");
            });

            reproduceTask.setOnRunning(event -> {
                setStageLabelText("Reproducing");
            });

            generatePlantsTask.setOnRunning(event -> {
                setStageLabelText("Generating plants");
            });

            taskThread = new Thread(new Runnable() {
                public void pause() {
                    try {
                        taskThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                public void resume() {
                    taskThread.notify();
                }

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
                            //running = false;
                            break;
                        }
                    }
                }
            });

            setMapPane();
            showAlertBox("Parameters loaded");
        } catch (IOException e) {
            showAlertBox("Error while loading parameters. " +
                    "Make sure that the parameters.json file exists in the working directory");
        }
    }

    private void refreshMap() {
        if(mapCells == null) {
            showAlertBox("Map isn't set, you must have done something wrong");
        } else {
            for(int i = 0; i < parameters.height; i++) {
                for (int j = 0; j < parameters.width; j++) {
                    Vector2d currentPosition = new Vector2d(j, i);

                    if (simulationManager.animalAt(currentPosition).isPresent()) {
                        mapCells[j][i].setImage(animal);
                    } else if(simulationManager.plantAt(currentPosition).isPresent()) {
                        mapCells[j][i].setImage(plant);
                    } else {
                        mapCells[j][i].setImage(background);
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
//        mapGridPane.getChildren().clear();
//        mapGridPane.setGridLinesVisible(true);
//
//        for(int i = 0; i < parameters.width; i++) {
//            ColumnConstraints column = new ColumnConstraints();
//            column.setPercentWidth((double) 100 / parameters.width);
//            column.fillWidthProperty().setValue(true);
//            mapGridPane.getColumnConstraints().add(column);
//        }
//
//        for(int i = 0; i < parameters.height; i++) {
//            RowConstraints row = new RowConstraints();
//            row.setPercentHeight((double) 100 / parameters.height);
//            row.fillHeightProperty().setValue(true);
//            mapGridPane.getRowConstraints().add(row);
//        }
//
//        Image background = new Image(getClass().getResource("/images/dirt.jpg").toExternalForm());
//
//        for(int i = 0; i < parameters.height; i++) {
//            for(int j = 0; j < parameters.width; j++) {
//                ImageView cell = new ImageView();
//                cell.setImage(background);
//
//                System.out.println(mapPane.getWidth() / parameters.width);
//                System.out.println(mapPane.getHeight() / parameters.height);
//
//                cell.setFitWidth(mapGridPane.getWidth() / parameters.width);
//                cell.setFitHeight(mapGridPane.getHeight() / parameters.height);
//
//                mapGridPane.add(cell, i, j);
//            }
//        }

        mapPane.getChildren().clear();

        GridPane mapGrid = new GridPane();
        mapPane.getChildren().add(mapGrid);
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);
        mapGrid.setGridLinesVisible(true);

        mapCells = new ImageView[parameters.width][parameters.height];

        for(int i = 0; i < parameters.height; i++) {
            for(int j = 0; j < parameters.width; j++) {
                //mapGrid.add(new ImageView(background), i, j);
                ImageView cell = new ImageView();
                cell.setCache(true);
                cell.setImage(background);

                Pane pane = new Pane();
                pane.getChildren().add(cell);
                pane.setPrefSize(mapPane.getWidth() / parameters.width,
                        mapPane.getHeight() / parameters.height);


                cell.fitWidthProperty().bind(pane.widthProperty());
                cell.fitHeightProperty().bind(pane.heightProperty());

                mapCells[j][i] = cell;
//
//                cell.setFitWidth(mapGrid.getColumnConstraints().get(i).getPercentWidth() * mapPane.getWidth() / 100);
//                cell.setFitHeight(mapGrid.getRowConstraints().get(j).getPercentHeight() * mapPane.getHeight() / 100);

//                pane.maxWidthProperty().bind(mapGrid.);
                mapGrid.add(pane, i, j);
            }
        }

        //mapGrid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        for(int i = 0; i < parameters.width; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth((double) 100 / parameters.width);
            //column.fillWidthProperty().setValue(true);
            mapGrid.getColumnConstraints().add(column);
        }

        for(int i = 0; i < parameters.height; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight((double) 100 / parameters.height);
            //row.fillHeightProperty().setValue(true);
            mapGrid.getRowConstraints().add(row);
        }
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {

    }

    @Override
    public void animalDied(Animal deadAnimal) {

    }
}
