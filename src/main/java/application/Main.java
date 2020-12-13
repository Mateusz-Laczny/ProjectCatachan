package application;

import application.controllers.MainApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        System.out.println(getClass().getResource("/fxml/main.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainApplicationController mainApplicationController = loader.getController();
        mainApplicationController.setMainApp(this);

        // We call the close method on the controller to kill the running thread
        stage.setOnCloseRequest(event -> {
            mainApplicationController.close();
            stage.close();
        });

        stage.setTitle("CatachanSimulator 1.0");
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        stage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
