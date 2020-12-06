package entities;

import datatypes.AbstractMapElement;
import datatypes.Vector2d;
import datatypes.observer.IPlantStateObserver;
import datatypes.observer.IPlantStatePublisher;

import java.util.HashSet;
import java.util.Set;

//TODO
public class Plant extends AbstractMapElement implements IPlantStatePublisher {
    private final Set<IPlantStateObserver> observers;

    public Plant(Vector2d position) {
        super(position, false, 1);
        observers = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Plant{" +
                "position=" + position +
                '}';
    }

    @Override
    public void addPlantObserver(IPlantStateObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removePlantObserver(IPlantStateObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers to remove plant from their collections
     */
    public void removePlant() {
        for(IPlantStateObserver observer : observers) {
            observer.plantEaten(this);
        }
    }
}
