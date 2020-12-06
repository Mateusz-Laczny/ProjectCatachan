package datatypes.observer;

public interface IAnimalStatePublisher {
    /**
     * Adds the given observer to the observer list of this object
     *
     * @param observer
     *          Object implementing IPositionChangeObserver
     */
    void addStateObserver(IAnimalStateObserver observer);

    /**
     * Removes the given observer from the observer list of this object.
     * If given observer is not on the list, the method does nothing
     *
     * @param observer
     *          Object implementing IPositionChangeObserver
     */
    void removeStateObserver(IAnimalStateObserver observer);
}
