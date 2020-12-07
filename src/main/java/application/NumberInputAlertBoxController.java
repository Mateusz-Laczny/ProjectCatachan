package application;

public class NumberInputAlertBoxController extends AbstractAlertBox{
    public String getResults() {
        return inputTextField.getCharacters().toString();
    }
}
