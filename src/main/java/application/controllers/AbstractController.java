package application.controllers;

import application.Main;
import application.controllers.alertBox.BasicAlertBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController {
    protected Main main;

    private Stage stage = null;

    public void setMainApp(Main main) {
        this.main = main;
    }

    protected void showAlertBox(String errorLabel) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BasicAlertBox.fxml"));
        Parent layout;

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            scene.getStylesheets().add(getClass().getResource("/css/AlertBoxStyle.css").toExternalForm());
            Stage popupStage = new Stage();

            BasicAlertBoxController basicAlertBoxController = loader.getController();
            basicAlertBoxController.setStage(popupStage);

            if(this.main!=null) {
                popupStage.initOwner(main.getPrimaryStage());
            }

            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);

            basicAlertBoxController.setErrorLabel(errorLabel);

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void closeStage() {
        if(stage != null) {
            stage.close();
        }
    }
}
