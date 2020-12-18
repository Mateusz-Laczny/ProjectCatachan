package datatypes.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Cell extends StackPane {
    public static boolean canBeClicked = false;

    private final int column;
    private final int row;

    private final Rectangle background;
    private Paint oldColour;

    public Cell(int column, int row, double width, double height, Grid grid) {
        super();
        this.column = column;
        this.row = row;

        background = new Rectangle(width, height);
        this.getChildren().add(background);

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
    }

    public void highlight() {
        oldColour = background.getFill();
        background.setFill(Color.rgb(28, 53, 128, 1));
    }

    public void unhighlight() {
        if (oldColour != null) {
            background.setFill(oldColour);
            oldColour = null;
        }
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

    public void setColour(Color colour) {
        background.setFill(colour);
    }
}
