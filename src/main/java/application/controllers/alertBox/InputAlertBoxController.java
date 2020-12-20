package application.controllers.alertBox;

import javafx.scene.control.TextField;

import java.io.File;
import java.util.Optional;


public class InputAlertBoxController extends AbstractAlertBox {
    public TextField inputTextField;

    public Optional<String> getResults() {
        String filePath = inputTextField.getCharacters().toString();

        File readFile = new File(filePath);

        if (!readFile.exists()) {
            return Optional.empty();
        } else {
            return Optional.of(filePath);
        }
    }
}
