package datatypes.observer;


import datatypes.Vector2d;
import entities.Animal;

public interface IAnimalStateObserver {
    /**
     * Called whenever the animal changes it's position
     */
    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);

    /**
     * Called when the energy of the animal drops bellow zero (animal dies)
     */
    void animalDied(Animal deadAnimal);
}
