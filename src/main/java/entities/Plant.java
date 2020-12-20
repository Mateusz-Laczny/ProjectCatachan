package entities;

import datatypes.AbstractMapElement;
import datatypes.Vector2d;
import datatypes.observers.IPlantStateObserver;
import datatypes.publishers.IPlantStatePublisher;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Plant extends AbstractMapElement implements IPlantStatePublisher {
    private final Set<IPlantStateObserver> observers;

    public Plant(Vector2d position) {
        super(position, false, 1);
        observers = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        // There can't be more than one plant at a given position,
        // so the plant's position determines the plant
        return this.position.equals(plant.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    /**
     * Notifies all observers about a new plant
     */
    public void notifyAboutANewPlant() {
        for(IPlantStateObserver observer : observers) {
            observer.newPlant(this);
        }
    }

    /**
     * Notifies all observers to remove plant from their collections
     */
    public void removePlant() {
        for(IPlantStateObserver observer : observers) {
            observer.plantEaten(this);
        }
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
}
