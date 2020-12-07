package application;

import datatypes.Vector2d;
import datatypes.observer.IAnimalStateObserver;
import entities.Animal;
import entities.Simulation;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Parameters;
import util.ParametersParser;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainApplicationController extends AbstractController implements Initializable, IAnimalStateObserver {
    public Pane mapVisualizationPane;

    // Buttons on the main window
    public Button startButton;
    public Button pauseButton;
    public Button stopButton;
    public Button addMapButton;
    public Button showStatisticsButton;
    public Button loadButton;
    public Pane mapPane;

    // Main simulation manager
    private Simulation simulationManager;

    // Parameters of the simulation
    Parameters parameters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startButton.setOnAction(event -> {
            runSimulation();
        });

        pauseButton.setOnAction(event -> {
            pauseSimulation();
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

        if(startingNumberOfAnimalsOptional.isPresent()) {
            int startingNumberOfAnimals = startingNumberOfAnimalsOptional.get();
            if (simulationManager != null) {
                simulationManager.generateAnimalsAtRandomPositions(startingNumberOfAnimals);
            }
        }
    }

    private void pauseSimulation() {

    }

    private void stopSimulation() {

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InputAlertBox.fxml"));
        Parent layout;
        Optional<String> filePathOptional;

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            //scene.getStylesheets().add(getClass().getResource("InputBoxStyle.css").toExternalForm());
            Stage popupStage = new Stage();

            InputAlertBoxController inputAlertBoxController = loader.getController();
            inputAlertBoxController.setStage(popupStage);

            if(this.main!=null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();

            filePathOptional = inputAlertBoxController.getResults();

            if(filePathOptional.isEmpty()) {
                showAlertBox("The file does not exist, or can't be opened");
            } else {
                ParametersParser parser = new ParametersParser();
                parameters = parser.readParameters(filePathOptional.get());
                simulationManager = new Simulation(parameters.width, parameters.height, parameters.startEnergy,
                        parameters.plantEnergy, parameters.moveEnergy, parameters.jungleRatio);
                setMapPane();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO
    private void setMapPane() {
        GridPane mapGrid = new GridPane();
        mapGrid.setGridLinesVisible(true);
        mapGrid.setStyle("-fx-background-color: brown");
        mapGrid.setMinHeight(mapPane.getMinHeight());
        mapGrid.setMinWidth(mapPane.getMinWidth());
        mapGrid.setBlendMode(BlendMode.SRC_ATOP);
        mapGrid.setVisible(true);


        for(int i = 0; i < parameters.height; i++) {
            for(int j = 0; j < parameters.width; j++) {
                Pane cell = new Pane();
                cell.setPrefWidth(-1);
                cell.setPrefHeight(-1);
                mapGrid.add(cell, i, j);
            }
            mapGrid.addRow(i, new Pane());
        }

        mapPane.getChildren().addAll(mapGrid);
        System.out.println("Correctly set map pane");
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {

    }

    @Override
    public void animalDied(Animal deadAnimal) {

    }
}
