package datatypes.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Cell extends StackPane {
    public static boolean canBeClicked = false;

    private final int column;
    private final int row;

    private final ImageView image;

    public Cell(int column, int row, Grid grid) {
        this.column = column;
        this.row = row;

        // Setting up the image
        this.image = new ImageView();

        getStyleClass().add("map-cell");

        this.setOnMousePressed(event -> {
            Cell cell = (Cell) event.getSource();

            if(canBeClicked) {
                if(event.isPrimaryButtonDown()) {
                    cell.highlight();
                    grid.cellHighlighted(this);
                } else if(event.isSecondaryButtonDown()) {
                    cell.unhighlight();
                    grid.cellUnHighlighted(this);
                }
            }
        });

        //setOpacity(0.9);
    }

    public void highlight() {
        // ensure the style is only once in the style list
        getStyleClass().remove("map-cell-highlight");

        // add style
        getStyleClass().add("map-cell-highlight");
    }

    public void unhighlight() {
        getStyleClass().remove("map-cell-highlight");
    }

    public void hoverHighlight() {
        // ensure the style is only once in the style list
        getStyleClass().remove("map-cell-hover-highlight");

        // add style
        getStyleClass().add("map-cell-hover-highlight");
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

    public void initializeImage(Image image) {
        this.image.setFitWidth(this.getPrefWidth());
        this.image.setFitHeight(this.getPrefHeight());
        this.image.setImage(image);
        this.getChildren().add(this.image);
    }

    public void setImage(Image image) {
        this.image.setImage(image);
    }
}
