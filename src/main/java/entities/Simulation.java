package entities;

import datatypes.Genotype;
import datatypes.Vector2d;
import datatypes.observer.IAnimalStateObserver;
import managers.StatisticsManager;

import java.util.*;

public class Simulation implements IAnimalStateObserver {
    // Simulation parameters
    private final int startEnergy;
    private final WorldMap map;
    private final int plantEnergy;
    private final int moveEnergy;

    private final List<Animal> deadAnimalsBuffer;

    private final StatisticsManager statisticsManager;

    // TODO OBSŁUGA BŁĘDNYCH PARAMETRÓW
    public Simulation(int width, int height, int startEnergy, int plantEnergy, int moveEnergy, double jungleRatio) {
        this.startEnergy = startEnergy;
        this.map = new WorldMap(width, height, jungleRatio);
        this.plantEnergy = plantEnergy;
        this.moveEnergy = moveEnergy;

        statisticsManager = new StatisticsManager();
        deadAnimalsBuffer = new LinkedList<>();
    }

    public Simulation(WorldMap map, int startEnergy, int plantEnergy, int moveEnergy) {
        this.startEnergy = startEnergy;
        this.map = map;
        this.plantEnergy = plantEnergy;
        this.moveEnergy = moveEnergy;

        statisticsManager = new StatisticsManager();
        deadAnimalsBuffer = new LinkedList<>();
    }

    // Accessors

    public Optional<Animal> animalAt(Vector2d position) {
        return map.animalAt(position);
    }

    public Optional<Plant> plantAt(Vector2d position) {
        return map.plantAt(position);
    }

    public int getNumberOfAnimals() {
        return map.getNumberOfAnimals();
    }

    public Animal getFollowedAnimal() {
        return statisticsManager.getFollowedAnimal();
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "startEnergy=" + startEnergy +
                ", map=" + map +
                ", plantEnergy=" + plantEnergy +
                ", moveEnergy=" + moveEnergy +
                '}';
    }

    // Mutators

    public void setFollowedAnimal(Animal animal) {
        statisticsManager.setFollowedAnimal(animal);
        System.out.println("Animal is being followed");
    }


    public void generateAnimalsAtRandomPositions(int numberOfAnimals, int lengthOfGenome, int numberOfGenes) {
        if(numberOfAnimals > map.getWidth() * map.getHeight()) {
            throw new IllegalArgumentException("Number of animals is greater than the number of possible positions");
        }

        Set<Vector2d> freePositions = new HashSet<>();

        for(int i = 0; i < map.getWidth(); i++) {
            for(int j = 0; j < map.getHeight(); j++) {
                freePositions.add(new Vector2d(i, j));
            }
        }

        List<Vector2d> freePositionsList = new ArrayList<>(freePositions);

        for(int i = 0; i < numberOfAnimals; i++) {
            Collections.shuffle(freePositionsList);

            Animal animal = new Animal(map, freePositionsList.get(0), startEnergy,
                    new Genotype(lengthOfGenome, numberOfGenes));
            animal.addStateObserver(this);

            freePositionsList.remove(0);
        }
    }

    public void removeDeadAnimals() {
        for(Animal animal : deadAnimalsBuffer) {
            map.removeAnimalFromMap(animal);
        }

        deadAnimalsBuffer.clear();
    }

    /**
     * Moves all animals one tile in the random direction, according to the animal's genome
     */
    public void moveAnimals() {
        Iterator<Animal> iterator = map.getAnimalListIterator();

        while (iterator.hasNext()) {
            //System.out.println("Animal at position " + animal.getPosition() + " and with energy " + animal.getEnergy()
            //        + " is being moved");
            Animal currentAnimal = iterator.next();
            currentAnimal.randomMove(moveEnergy);
        }
        //System.out.println("Moved Animals");
    }

    public void eatPlants() {
        map.eatPlants(plantEnergy);
        //System.out.println("Animals ate plants");
    }

    /**
     * Reproduces all capable pairs of animals on tha map
    */
    public void reproduceAnimals() {
        Iterator<Vector2d> iterator = map.getAnimalPositionsIterator();

        while (iterator.hasNext()) {
            //System.out.println("Animal at position " + animal.getPosition() + " and with energy " + animal.getEnergy()
            //        + " is being moved");

            // If there are more than 2 animals on a given position
            // then they may be able to reproduce
            List<Animal> currentAnimalList = map.getAnimalsListAt(iterator.next()).get();
            if(currentAnimalList.size() >= 2) {
                // If a child was born, we add it to the list of nev animals
                Optional<Animal> child = Animal.haveSexyTime(currentAnimalList, map, startEnergy);
                // We add the simulation object as an observer
                child.ifPresent(animal -> animal.addStateObserver(this));
            }
        }

        //System.out.println("Animals reproduced");
    }

    public void generatePlants() {
        map.generatePlants();
        //System.out.println("Plants generated");

        // It is the end of a day, so we increment the current day value
        statisticsManager.incrementDay();
    }

    @Override
    public void animalDied(Animal deadAnimal) {
        deadAnimalsBuffer.add(deadAnimal);
        System.out.println("Animal " + deadAnimal + "died at day " + statisticsManager.getCurrentDay());
    }

    @Override
    public void animalBorn(Animal parent, Animal child) {

    }
}
