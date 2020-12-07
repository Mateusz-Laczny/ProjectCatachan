package application;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractAlertBox extends AbstractController implements Initializable {
    public Button doneButton;

    public TextField inputTextField;

    private Stage stage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doneButton.setOnAction((event) -> {
            closeStage();
        });
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
