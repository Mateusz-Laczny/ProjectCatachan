package managers;

import datatypes.Direction;
import datatypes.containers.FollowedAnimalStatisticsContainer;
import datatypes.containers.StatisticsContainer;
import datatypes.observers.IAnimalEnergyObserver;
import datatypes.observers.IAnimalStateObserver;
import datatypes.observers.IPlantStateObserver;
import entities.Animal;
import entities.Plant;

import java.util.*;

public class StatisticsManager implements IAnimalStateObserver, IAnimalEnergyObserver, IPlantStateObserver {
    private Animal followedAnimal;
    private final Map<Animal, Integer> animalsBornDateMap;

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
    private final Map<Animal, Integer> numberOfChildren;
    private int aliveAnimalsChildrenCountSum;
    // Statistics of plants
    private int numberOfPlants;

    // Overall statistics
    private int overallAnimalPopulation;
    private int overallNumberOfPlants;
    private final Map<Direction, Integer> overallGenesCount;
    private float sumOfMeanEnergyLevels;
    private float sumOfMeanLifespans;
    private float sumOfMeanNumberOfChildren;

    private int currentDay;
    private Animal lastAddedAnimal;


    public StatisticsManager() {
        currentDay = 1;
        energySum = 0;
        lifespanSum = 0;
        numberOfAnimals = 0;
        numberOfDeadAnimals = 0;
        numberOfPlants = 0;
        aliveAnimalsChildrenCountSum = 0;
        // If set to -1 then the followed animal is still alive
        followedAnimalDeathDate = -1;

        overallAnimalPopulation = 0;
        overallNumberOfPlants = 0;
        sumOfMeanEnergyLevels = 0;
        sumOfMeanLifespans = 0;
        sumOfMeanNumberOfChildren = 0;

        genesCount = new LinkedHashMap<>();
        animalsBornDateMap = new HashMap<>();
        numberOfChildren = new HashMap<>();
        overallGenesCount = new HashMap<>();

        for(Direction direction : Direction.values()) {
            genesCount.put(direction, 0);
            overallGenesCount.put(direction, 0);
        }
    }

    public void setFollowedAnimal(Animal followedAnimal) {
        this.followedAnimal = followedAnimal;
        followedAnimalChildren = new HashSet<>();
        followedAnimalDescendants = new HashSet<>();
    }

    public void incrementDay() {
        currentDay += 1;
        sumOfMeanEnergyLevels += getMeanEnergyLevel();
        sumOfMeanLifespans += getMeanLifespan();
        sumOfMeanNumberOfChildren += getMeanNumberOfChildren();
    }

    public Animal getFollowedAnimal() {
        return followedAnimal;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getNumberOfAnimals() {
        return numberOfAnimals;
    }

    public int getNumberOfDeadAnimals() {
        return numberOfDeadAnimals;
    }

    public int getNumberOfPlants() {
        return numberOfPlants;
    }

    public float getMeanEnergyLevel() {
        return (float) energySum / numberOfAnimals;
    }

    public float getMeanNumberOfChildren() {
        return (float) aliveAnimalsChildrenCountSum / numberOfAnimals;
    }

    public Map<Direction, Integer> getGenesCount() {
        return genesCount;
    }

    public float getMeanLifespan() {
        if(numberOfDeadAnimals == 0) {
            return (float) currentDay / numberOfAnimals;
        } else {
            return (float) lifespanSum / numberOfDeadAnimals;
        }
    }

    public StatisticsContainer getCurrentDayStatistics() {
        return new StatisticsContainer(numberOfAnimals,
                numberOfPlants, getMeanEnergyLevel(),
                getMeanLifespan(), getMeanNumberOfChildren(),
                currentDay, genesCount);
    }

    public FollowedAnimalStatisticsContainer getFollowedAnimalStatistics() {
        return new FollowedAnimalStatisticsContainer(followedAnimalChildren.size(), followedAnimalDescendants.size(),
                followedAnimalDeathDate);
    }

    public StatisticsContainer getOverallStatistics() {
        return new StatisticsContainer(overallAnimalPopulation, overallNumberOfPlants,
                sumOfMeanEnergyLevels / currentDay, sumOfMeanLifespans / currentDay,
                sumOfMeanNumberOfChildren / currentDay, currentDay, overallGenesCount);
    }

    public void addAnimal(Animal animal) {
        animal.addStateObserver(this);
        animal.addEnergyObserver(this);
        animalsBornDateMap.put(animal, currentDay);
        numberOfChildren.put(animal, 0);

        // Updating statistics
        energySum += animal.getEnergy();
        numberOfAnimals += 1;
        overallAnimalPopulation += 1;

        Map<Direction, Integer> genesCount = animal.getGenesCount();

        for(Direction direction : genesCount.keySet()) {
            this.genesCount.put(direction, this.genesCount.get(direction) + genesCount.get(direction));
            overallGenesCount.put(direction, overallGenesCount.get(direction) + genesCount.get(direction));
        }
    }

    @Override
    public void animalDied(Animal deadAnimal) {
        numberOfAnimals -= 1;
        numberOfDeadAnimals += 1;
        lifespanSum += currentDay - animalsBornDateMap.get(deadAnimal);
        animalsBornDateMap.remove(deadAnimal);
        aliveAnimalsChildrenCountSum -= numberOfChildren.get(deadAnimal);
        numberOfChildren.remove(deadAnimal);

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
        if(!child.equals(lastAddedAnimal)) {
            addAnimal(child);
            lastAddedAnimal = child;
        }

        numberOfChildren.put(parent, numberOfChildren.get(parent) + 1);
        aliveAnimalsChildrenCountSum += 1;

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
    public void energyChanged(int energyChange) {
        energySum += energyChange;
    }

    @Override
    public void plantEaten(Plant eatenPlant) {
        numberOfPlants -= 1;
    }

    @Override
    public void newPlant(Plant newPlant) {
        numberOfPlants += 1;
        overallNumberOfPlants += 1;
    }
}
