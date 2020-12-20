package application.controllers.alertBox;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class BasicAlertBoxController extends AbstractAlertBox implements Initializable {
    public Button okButton;
    public Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        okButton.setOnAction((event) -> {
            closeStage();
        });
    }

    public void setErrorLabel(String label) {
        errorLabel.setText(label);
    }
}
