package datatypes.observers;

import datatypes.Vector2d;
import entities.Animal;

public interface IAnimalPositionObserver {
    /**
     * Called whenever the animal changes it's position
     */
    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);
}
