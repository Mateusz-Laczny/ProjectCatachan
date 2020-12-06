package entities;

import datatypes.AbstractMapElement;
import datatypes.Direction;
import datatypes.Genotype;
import datatypes.Vector2d;
import datatypes.observer.IAnimalStateObserver;
import datatypes.observer.IAnimalStatePublisher;
import util.IRandomGenerator;

import java.util.*;

//TODO
public class Animal extends AbstractMapElement implements IAnimalStatePublisher {
    private Direction orientation;
    private final WorldMap map;
    private final Genotype genotype;
    private int energy;
    private final Set<IAnimalStateObserver> stateObservers;

    /**
     * Constructor for an animal with defined attributes and a random orientation
     *
     * @param map
     *      Map to place the animal on
     * @param initialPosition
     *      Initial position of the animal
     * @param startingEnergy
     *      Starting energy of the animal
     *
     * @throws IllegalArgumentException
     *      If any of the given parameters is incorrect
     */
    public Animal(WorldMap map, Vector2d initialPosition, int startingEnergy, Genotype genotype)
            throws IllegalArgumentException{
        super(initialPosition, true, 0);

        Random random = new Random();

        if(startingEnergy < 0) {
            throw new IllegalArgumentException("Starting energy must be greater than 0");
        }
        energy = startingEnergy;

        stateObservers = new HashSet<>();

        this.map = map;
        map.place(this);

        this.genotype = genotype;

        orientation = Direction.values()[random.nextInt(Direction.values().length)];
    }

    /**
     * Constructor for an animal with random position and genotype
     * @param map
     *       Map to place the animal on
     * @param startingEnergy
     *      Starting energy of the animal
     * @param lengthOfGenotype
     *      Length of the animal's genome
     * @param numOfGeneTypes
     *      Number of gene types in the animal's genome
     */
    public Animal(WorldMap map, int startingEnergy, int lengthOfGenotype, int numOfGeneTypes) {
        this(map, map.getRandomPositionFromMap(), startingEnergy, new Genotype(lengthOfGenotype, numOfGeneTypes));
    }

    /**
     * Constructor for an animal with a given position
     * and a genotype generated from genotypes of it's parents
     * @param map
     *        Map to place the animal on
     * @param initialPosition
     *      Initial position of the animal
     * @param startingEnergy
     *      Starting energy of the animal
     * @param firstParent
     *      Animal object representing the parent of this animal
     * @param secondParent
     *      Animal object representing the parent of this animal
     */
    public Animal(WorldMap map, Vector2d initialPosition, int startingEnergy, Animal firstParent, Animal secondParent) {
        this(map, initialPosition, startingEnergy, new Genotype(firstParent.genotype, secondParent.genotype));
    }

    /**
     * Constructor for testing, which accepts a mockup random class
     * @param map
     *        Map to place the animal on
     * @param initialPosition
     *      Initial position of the animal
     * @param startingEnergy
     *      Starting energy of the animal
     * @param firstParent
     *      Animal object representing the parent of this animal
     * @param secondParent
     *      Animal object representing the parent of this animal
     * @param mockup
     *      Mockup class inheriting form IRandomGenerator
     */
    public Animal(WorldMap map, Vector2d initialPosition, int startingEnergy, Animal firstParent, Animal secondParent,
                  IRandomGenerator mockup) {
        this(map, initialPosition, startingEnergy, new Genotype(firstParent.genotype, secondParent.genotype));
    }

    // Accessors

    public Direction getOrientation() {
        return orientation;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public String toString() {
//        return "Animal{" +
//                "orientation=" + orientation +
//                ", genotype=" + genotype +
//                ", energy=" + energy +
//                '}';

        return position.toString();
    }

    // Methods

    /**
     * Moves the animal by one field in the given direction
     *
     * @param direction
     *      Direction to move the animal in
     */
    public void move(Direction direction) {
        setPosition(direction.toUnitVector());
        energy -= 1;
    }


    public void randomMove() {
        orientation = genotype.getRandomDirection();
        move(orientation);
    }

    /**
     * Changes position of the animal by the given vector, and notifies all observers about the change.
     *
     * @param changeVector
     *      Vector to add to the given position
     */
    private void setPosition(Vector2d changeVector) {
        Vector2d newPosition = position.add(changeVector);
        Vector2d oldPosition = position;

        int newPositionX = newPosition.x_coordinate;
        int newPositionY = newPosition.y_coordinate;

        if(newPositionY > map.getHeight() - 1) {
            newPositionY = 0;
        } else if(newPositionY < 0) {
            newPositionY = map.getHeight() - 1;
        }
        if(newPositionX > map.getWidth() - 1) {
            newPositionX = 0;
        } else if(newPositionX < 0) {
            newPositionX = map.getWidth() - 1;
        }

        newPosition = new Vector2d(newPositionX, newPositionY);
        for(IAnimalStateObserver observer : stateObservers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
        position = newPosition;
    }

    /**
     * Takes a list of animals at a given position
     * and divides energy from the plant between the strongest animals
     * (animals with the highest energy)
     *
     * @param animalsAtPosition
     *      List of animals at a position with a plant
     * @param energyFromPlant
     *      Energy surplus from eating a plant
     */
    public static void eat(List<Animal> animalsAtPosition, int energyFromPlant) {
        int maxEnergy = animalsAtPosition.get(0).energy;

        // Searching for an animal with maximal energy
        for(Animal animal : animalsAtPosition) {
            if(animal.energy > maxEnergy) {
                maxEnergy = animal.energy;
            }
        }

        // Removing animals with less than maximal energy
        List<Animal> animalsWithMaxEnergy = new LinkedList<>();

        for(Animal animal : animalsAtPosition) {
            if(animal.energy == maxEnergy) {
                animalsWithMaxEnergy.add(animal);
            }
        }

        // Increasing energy of the strongest animals
        for (Animal strongestAnimal : animalsWithMaxEnergy) {
            strongestAnimal.energy += energyFromPlant / animalsWithMaxEnergy.size();
            System.out.println("Animal at position " + strongestAnimal.getPosition().toString() + " ate a plant " +
                    "and gained " + energyFromPlant / animalsWithMaxEnergy.size() + " energy");
        }
    }

    /**
     * Checks whether there are eligible parents in the given list of animals.
     * If there are, creates a new animal and returns it
     *
     * @param animalsAtPosition
     *      List of animals at a one position
     * @param map
     *      The map containing the animals
     * @param startingEnergy
     *      Starting energy of an animal. It is equal to the energy at the start of the simulation
     *
     * @throws IllegalArgumentException
     *      If the list contains less than 2 animals
     */
    public static void haveSexyTime(List<Animal> animalsAtPosition, WorldMap map,  int startingEnergy)
            throws IllegalArgumentException {
        if(animalsAtPosition.size() < 2) {
            throw new IllegalArgumentException("There are not enough animals to reproduce");
        }

        // Finding animals that are ready for reproduction
        List<Animal> animalsReadyToReproduce = new LinkedList<>(animalsAtPosition);

        animalsReadyToReproduce.removeIf(animal -> 2 * animal.getEnergy() < startingEnergy);

        // Finding parents
        if(animalsReadyToReproduce.size() > 1) {
            Random random = new Random();

            // Sorting animals by their energy value
            animalsReadyToReproduce.sort(Comparator.comparing(Animal::getEnergy));
            int numOfAnimalsWithMaximalEnergy = 0;
            int maximalEnergy = animalsReadyToReproduce.get(0).getEnergy();

            // Counting the number of animals with the maximal energy
            for(Animal animal : animalsReadyToReproduce) {
                if(animal.getEnergy() == maximalEnergy) {
                    numOfAnimalsWithMaximalEnergy += 1;
                }
            }

            // Case one - There are less than 3 animals with the maximal energy
            Animal firstParent = animalsReadyToReproduce.get(0);
            Animal secondParent = animalsReadyToReproduce.get(1);

            // Case two - there are multiple animals with the same maximal energy
            if(numOfAnimalsWithMaximalEnergy > 2) {
                animalsReadyToReproduce.removeIf(animal -> animal.getEnergy() != maximalEnergy);

                firstParent = animalsReadyToReproduce.get(random.nextInt(animalsReadyToReproduce.size()));
                animalsReadyToReproduce.remove(firstParent);

                secondParent = animalsReadyToReproduce.get(random.nextInt(animalsReadyToReproduce.size()));
            }

            // Finding a position on the map for the child
            Vector2d parentsPosition = firstParent.getPosition();
            List<Vector2d> adjacentPositions = parentsPosition.getAdjacentPoints();

            // Filtering incorrect positions
            adjacentPositions.removeIf(position -> !map.isInsideMap(position));

            // Finding a correct position for the child
            Vector2d childPosition = adjacentPositions.get(random.nextInt(adjacentPositions.size()));

            // If there's a position that isn't occupied - we take it
            for(Vector2d position : adjacentPositions) {
                if(map.animalAt(position).isEmpty()) {
                    childPosition = position;
                    break;
                }
            }

            // Creating a child
            new Animal(map, childPosition,(firstParent.getEnergy() + secondParent.getEnergy())/ 4,
                    firstParent, secondParent);

            // Parent loose energy during reproduction
            firstParent.energy -= firstParent.getEnergy() / 4;
            secondParent.energy -= secondParent.getEnergy() / 4;
        }
    }

    /**
     * Notifies all observers about the animals death.
     * Animal dies when it's energy drops bellow 0
     */
    public void die() {
        for(IAnimalStateObserver observer : stateObservers) {
            // Press F to pay respects
            observer.animalDied(this);
        }
    }

    @Override
    public void addStateObserver(IAnimalStateObserver observer) {
        stateObservers.add(observer);
    }

    @Override
    public void removeStateObserver(IAnimalStateObserver observer) {
        stateObservers.remove(observer);
    }
}
