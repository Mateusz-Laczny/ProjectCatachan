package application;

import java.io.File;
import java.util.Optional;


public class InputAlertBoxController extends AbstractAlertBox {
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
