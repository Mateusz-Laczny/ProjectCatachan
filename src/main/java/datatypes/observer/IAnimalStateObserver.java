package datatypes.observer;

import entities.Animal;

public interface IAnimalStateObserver {
    /**
     * Called when the energy of the animal drops bellow zero (animal dies)
     */
    void animalDied(Animal deadAnimal);

    void animalBorn(Animal parent, Animal child);
}
