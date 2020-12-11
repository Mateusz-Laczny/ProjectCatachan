package application;

import datatypes.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Grid extends Pane {
    // Grid dimensions
    int rows;
    int columns;
    double width;
    double height;

    Cell[][] cells;

    private final MainApplicationController mainApplicationController;

    public Grid(int columns, int rows, double width, double height, MainApplicationController mainApplicationController) {
        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;
        cells = new Cell[rows][columns];

        this.mainApplicationController = mainApplicationController;
    }

    /**
     * Add cell to array and to the UI.
     */
    public void add(int column, int row, Image image) {
        Cell cell = new Cell(column, row, this);
        cells[row][column] = cell;


        double w = width / columns;
        double h = height / rows;
        double x = w * column;
        double y = h * row;

        cell.setLayoutX(x);
        cell.setLayoutY(y);
        cell.setPrefWidth(w);
        cell.setPrefHeight(h);

        cell.initializeImage(image);

        getChildren().add(cell);
    }

    public Cell getCell(int column, int row) {
        return cells[row][column];
    }

    public void cellHighlighted(Cell cell) {
        mainApplicationController.cellHighlighted(new Vector2d(cell.getColumn(), cell.getRow()));
    }

    public void cellUnHighlighted(Cell cell) {
        mainApplicationController.cellUnHighlighted(new Vector2d(cell.getColumn(), cell.getRow()));
    }
}





