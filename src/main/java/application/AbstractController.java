package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController {
    protected Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    protected void showAlertBox(String errorLabel) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/BasicAlertBox.fxml"));
        Parent layout;

        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            scene.getStylesheets().add(getClass().getResource("InputBoxStyle.css").toExternalForm());
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
}
