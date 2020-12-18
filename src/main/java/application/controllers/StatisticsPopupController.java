package application.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.*;

import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsPopupController extends AbstractController implements Initializable {
    public TextFlow statisticsText;
    public Button closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnAction(event -> closeStage());
    }

    public void setStatisticsText(String text) {
        Text textToSet = new Text(text);
        textToSet.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        statisticsText.getChildren().add(textToSet);
    }
}
