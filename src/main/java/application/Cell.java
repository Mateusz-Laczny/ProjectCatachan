package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Cell extends ImageView {
    public static boolean canBeClicked = false;

    private final int column;
    private final int row;

    private final Grid grid;

    public Cell(int column, int row, Image image, Grid grid) {
        this.column = column;
        this.row = row;
        this.grid = grid;
        super.setImage(image);

        getStyleClass().add("cell");

        this.setOnMouseClicked(event -> {
            Cell cell = (Cell) event.getSource();

            if(canBeClicked) {
                if(event.isPrimaryButtonDown()) {
                    cell.highlight();
                } else if(event.isSecondaryButtonDown()) {
                    cell.unhighlight();
                }
            }
        });

        //setOpacity(0.9);
    }

    public void highlight() {
        // ensure the style is only once in the style list
        getStyleClass().remove("cell-highlight");

        // add style
        getStyleClass().add("cell-highlight");
    }

    public void unhighlight() {
        getStyleClass().remove("cell-highlight");
    }

    public void hoverHighlight() {
        // ensure the style is only once in the style list
        getStyleClass().remove("cell-hover-highlight");

        // add style
        getStyleClass().add("cell-hover-highlight");
    }

    public void hoverUnhighlight() {
        getStyleClass().remove("cell-hover-highlight");
    }

    public String toString() {
        return this.column + "/" + this.row;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
