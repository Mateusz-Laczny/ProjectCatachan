package application;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BasicAlertBoxController implements Initializable {
    public Button okButton;
    public Label errorLabel;

    private Stage stage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        okButton.setOnAction((event) -> {
            closeStage();
        });
    }

    public void setErrorLabel(String label) {
        errorLabel.setText(label);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void closeStage() {
        if(stage != null) {
            stage.close();
        }
    }
}
