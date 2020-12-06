package datatypes.observer;

import entities.Plant;

public interface IPlantStatePublisher {
    /**
     * Adds the given observer to the observer list of this object
     *
     * @param observer
     *          Object implementing IPositionChangeObserver
     */
    void addPlantObserver(IPlantStateObserver observer);

    /**
     * Removes the given observer from the observer list of this object.
     * If given observer is not on the list, the method does nothing
     *
     * @param observer
     *          Object implementing IPositionChangeObserver
     */
    void removePlantObserver(IPlantStateObserver observer);
}
