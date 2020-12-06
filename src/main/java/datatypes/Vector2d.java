package datatypes;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Vector2d {
    public final int x_coordinate;
    public final int y_coordinate;

    public Vector2d(int x_coordinate, int y_coordinate) {
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }

    public boolean precedes(Vector2d other) {
        return other.x_coordinate <= x_coordinate && other.y_coordinate <= y_coordinate;
    }

    public boolean follows(Vector2d other) {
        return other.x_coordinate >= x_coordinate && other.y_coordinate >= y_coordinate;
    }

    public Vector2d upperRight(Vector2d other) {
        int max_x_cr = x_coordinate;
        int max_y_cr = y_coordinate;

        if(max_x_cr < other.x_coordinate) {
            max_x_cr = other.x_coordinate;
        }

        if(max_y_cr < other.y_coordinate) {
            max_y_cr = other.y_coordinate;
        }

        return new Vector2d(max_x_cr, max_y_cr);
    }

    public Vector2d lowerLeft(Vector2d other) {
        int min_x_cr = x_coordinate;
        int min_y_cr = y_coordinate;

        if(min_x_cr > other.x_coordinate) {
            min_x_cr = other.x_coordinate;
        }

        if(min_y_cr > other.y_coordinate) {
            min_y_cr = other.y_coordinate;
        }

        return new Vector2d(min_x_cr, min_y_cr);
    }

    /**
     * Returns a new vector being the sum of given vectors
     * @param other vector to add
     * @return New vector equal to this vector + other
     */
    public Vector2d add(Vector2d other) {
        return new Vector2d(x_coordinate + other.x_coordinate, y_coordinate + other.y_coordinate);
    }

    /**
     * Returns a new vector being the result of subtraction of given vectors
     * @param other vector to subtract
     * @return New vector equal to this vector - other
     */
    public Vector2d subtract(Vector2d other) {
        return new Vector2d(x_coordinate - other.x_coordinate, y_coordinate - other.y_coordinate);
    }

    /**
     * Returns the opposite vector
     * @return Vector with coordinates of this vector multiplied by -1
     */
    public Vector2d opposite() {
        return new Vector2d(-x_coordinate, -y_coordinate);
    }

    /**
     * Returns list of vectors representing points adjacent
     * to the point represented by a Vector2d object.
     * Two points are considered adjacent if their difference (vector difference)
     * is equal to one of the unit vectors from the Direction enum
     *
     * @return List of Vector2d object corresponding to adjacent points
     */
    public List<Vector2d> getAdjacentPoints() {
        List<Vector2d> adjacentPositions = new LinkedList<>();

        for(Direction direction : Direction.values()) {
            adjacentPositions.add(this.add(direction.toUnitVector()));
        }

        return adjacentPositions;
    }

    // Methods for obtaining often used values

    /**
     *   Returns the zero vector (0,0)
     * @return Vector2D object with coordinates (0, 0)
     **/
    public static Vector2d zero() {
        return new Vector2d(0, 0);
    }

    /**
     * Returns the one vector (1, 1)
     * @return Vector2D object with coordinates (1, 1)
     */
    public static Vector2d one() {
        return new Vector2d(1, 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2D = (Vector2d) o;
        return x_coordinate == vector2D.x_coordinate &&
                y_coordinate == vector2D.y_coordinate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x_coordinate, y_coordinate);
    }

    @Override
    public String toString() {
        return "(" + x_coordinate + ", " + y_coordinate + ")";
    }
}
