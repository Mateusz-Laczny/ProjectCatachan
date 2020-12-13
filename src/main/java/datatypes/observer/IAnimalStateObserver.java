package datatypes.observer;


import datatypes.Vector2d;
import entities.Animal;

public interface IAnimalStateObserver {
    /**
     * Called when the energy of the animal drops bellow zero (animal dies)
     */
    void animalDied(Animal deadAnimal);
}
