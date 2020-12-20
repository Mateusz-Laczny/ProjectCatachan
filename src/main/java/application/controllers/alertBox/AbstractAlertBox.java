package application.controllers.alertBox;

import application.controllers.AbstractController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractAlertBox extends AbstractController implements Initializable {
    public Button doneButton;
    public Label titleLabel;

    public void setTitleLabel(String title) {
        titleLabel.setText(title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doneButton.setOnAction((event) -> {
            closeStage();
        });
    }
}
