package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class MouseSelection {
    private final MainApplicationController controller;

    public MouseSelection(MainApplicationController controller) {
        this.controller = controller;
    }

    public void makeSelectable( Node node) {
        node.hoverProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println( observable + ": " + newValue);

                if( newValue) {
                    ((Cell) node).hoverHighlight();
                } else {
                    ((Cell) node).hoverUnhighlight();
                }
                for( String s: node.getStyleClass())
                    System.out.println( node + ": " + s);
            }
        });

        node.setOnMousePressed(onMousePressedEventHandler);
    }

    EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
        Cell cell = (Cell) event.getSource();

        if( event.isPrimaryButtonDown()) {
            cell.highlight();
        } else if( event.isSecondaryButtonDown()) {
            cell.unhighlight();
        }
    };
}
