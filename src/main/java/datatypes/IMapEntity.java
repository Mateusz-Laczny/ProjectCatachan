package datatypes;

public interface IMapEntity {
    /**
     * Returns the position of the object in the 2D space
     *
     * @return Vector2d object representing the position of the object
     */
    Vector2d getPosition();

    /**
     * Returns priority for visualization. Lower number means higher priority. Priorities are always non-negative
     *
     * @return Int number representing priority for visualization
     */
    int getPriority();

    /**
     * Returns true if other map elements can't share the same position on the map with this object
     *
     * @return True if object is blocking it's position for other objects
     */
    boolean isBlockingMovement();

    // Methods used for creating comparators and sorting collections
    static int compareByXCoordinate(IMapEntity element1, IMapEntity element2) {
        if (element1.equals(element2)) {
            return 0;
        }

        if (element1.getPosition().x_coordinate == element2.getPosition().x_coordinate) {
            if (element1.getPriority() == element2.getPriority()) {
                return compareByYCoordinate(element1, element2);
            } else {
                return Integer.compare(element1.getPriority(), element2.getPriority());
            }
        } else {
            return Integer.compare(element1.getPosition().x_coordinate, element2.getPosition().x_coordinate);
        }
    }

    static int compareByYCoordinate(IMapEntity element1, IMapEntity element2) {
        if (element1.equals(element2)) {
            return 0;
        }

        if (element1.getPosition().y_coordinate == element2.getPosition().y_coordinate) {
            if (element1.getPriority() == element2.getPriority()) {
                return compareByXCoordinate(element1, element2);
            } else {
                return Integer.compare(element1.getPriority(), element2.getPriority());
            }
        } else {
            return Integer.compare(element1.getPosition().y_coordinate, element2.getPosition().y_coordinate);
        }
    }
}
