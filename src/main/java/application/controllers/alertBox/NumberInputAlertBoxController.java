package application.controllers.alertBox;

import javafx.scene.control.TextField;

public class NumberInputAlertBoxController extends AbstractAlertBox {
    public TextField inputTextField;

    public String getResults() {
        return inputTextField.getCharacters().toString();
    }
}
