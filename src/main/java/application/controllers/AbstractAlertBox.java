package application.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractAlertBox extends AbstractController implements Initializable {
    public Button doneButton;

    public TextField inputTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doneButton.setOnAction((event) -> {
            closeStage();
        });
    }
}
