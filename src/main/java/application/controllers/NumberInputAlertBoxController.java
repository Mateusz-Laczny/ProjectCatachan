package application.controllers;

import javafx.scene.control.Label;

public class NumberInputAlertBoxController extends AbstractAlertBox {
    public Label titleLabel;

    public void setTitleLabel(String title) {
        titleLabel.setText(title);
    }

    public String getResults() {
        return inputTextField.getCharacters().toString();
    }
}
