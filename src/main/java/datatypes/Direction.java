package datatypes;

public enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    /**
     * Returns the unit vector associated with the current direction
     * @return Vector object representing the unit vector
     */
    public Vector2d toUnitVector() {
        return switch (this) {
            case N -> new Vector2d(0, 1);
            case NE -> new Vector2d(1, 1);
            case E -> new Vector2d(1, 0);
            case SE -> new Vector2d(1, -1);
            case S -> new Vector2d(0, -1);
            case SW -> new Vector2d(-1, -1);
            case W -> new Vector2d(-1, 0);
            case NW -> new Vector2d(-1, 1);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case N -> "N";
            case NE -> "NE";
            case E -> "E";
            case SE -> "SE";
            case S -> "S";
            case SW -> "SW";
            case W -> "W";
            case NW -> "NW";
        };
    }
}
