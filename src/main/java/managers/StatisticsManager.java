package managers;

import datatypes.Direction;
import datatypes.Vector2d;
import datatypes.observer.IAnimalEnergyObserver;
import datatypes.observer.IAnimalStateObserver;
import datatypes.observer.IPlantStateObserver;
import entities.Animal;
import entities.Plant;

import java.util.*;

public class StatisticsManager implements IAnimalStateObserver, IAnimalEnergyObserver, IPlantStateObserver {
    private Animal followedAnimal;
    private Map<Animal, Integer> animalsBornDateMap;

    // Followed animal statistics
    private int followedAnimalDeathDate;
    private Set<Animal> followedAnimalChildren;
    private Set<Animal> followedAnimalDescendants;

    // Statistics of all animals
    private int energySum;
    private int lifespanSum;
    private int numberOfAnimals;
    private int numberOfDeadAnimals;
    private final Map<Direction, Integer> genesCount;

    // Statistics of plants
    private int numberOfPlants;

    private int currentDay;

    public StatisticsManager(int numberOfGeneTypes) {
        currentDay = 1;
        energySum = 0;
        lifespanSum = 0;
        numberOfAnimals = 0;
        numberOfDeadAnimals = 0;
        numberOfPlants = 0;

        genesCount = new HashMap<>();
        animalsBornDateMap = new HashMap<>();

        for(Direction direction : Direction.values()) {
            genesCount.put(direction, 0);
        }
    }

    public void setFollowedAnimal(Animal followedAnimal) {
        this.followedAnimal = followedAnimal;
        followedAnimalChildren = new HashSet<>();
        followedAnimalDescendants = new HashSet<>();
    }

    public void addAnimal(Animal animal) {
        animal.addStateObserver(this);
        animal.addEnergyObserver(this);
        animalsBornDateMap.put(animal, currentDay);
        numberOfAnimals += 1;
    }

    public void incrementDay() {
        currentDay += 1;
    }

    public Animal getFollowedAnimal() {
        return followedAnimal;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public float getMeanEnergy() {
        return (float) energySum / numberOfAnimals;
    }

    @Override
    public void animalDied(Animal deadAnimal) {
        numberOfAnimals -= 1;
        numberOfDeadAnimals += 1;
        lifespanSum = currentDay - animalsBornDateMap.get(deadAnimal);
        animalsBornDateMap.remove(deadAnimal);

        Map<Direction, Integer> genesCount = deadAnimal.getGenesCount();

        for(Direction direction : genesCount.keySet()) {
            this.genesCount.put(direction, this.genesCount.get(direction) - genesCount.get(direction));
        }

        if(deadAnimal.equals(followedAnimal)) {
            followedAnimalDeathDate = currentDay;
        }
    }

    @Override
    public void animalBorn(Animal parent, Animal child) {
        addAnimal(child);

        Map<Direction, Integer> genesCount = child.getGenesCount();

        for(Direction direction : genesCount.keySet()) {
            this.genesCount.put(direction, this.genesCount.get(direction) - genesCount.get(direction));
        }

        if(followedAnimal != null) {
            if(parent.equals(followedAnimal)) {
                followedAnimalChildren.add(child);
                followedAnimalDescendants.add(child);
            } else if(followedAnimalDescendants.contains(child)) {
                followedAnimalDescendants.add(child);
            }
        }
    }

    @Override
    public void energyChanged(int previousEnergy, int newEnergy) {
        energySum -= previousEnergy;
        energySum += newEnergy;
    }

    @Override
    public void plantEaten(Plant eatenPlant) {
        numberOfPlants -= 1;
    }

    @Override
    public void newPlant(Plant newPlant) {
        numberOfPlants += 1;
    }
}
