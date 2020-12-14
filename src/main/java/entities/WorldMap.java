package entities;

import datatypes.Vector2d;
import datatypes.observer.IAnimalPositionObserver;
import datatypes.observer.IPlantStateObserver;
import util.randomMock.IRandomGenerator;
import util.randomMock.RealRandom;

import java.util.*;


public class WorldMap implements IAnimalPositionObserver, IPlantStateObserver {
    // Map dimensions
    private final int width;
    private final int height;

    private final int jungleWidth;
    private final int jungleHeight;

    private final Vector2d jungleLowerLeftCorner;
    private final Vector2d jungleUpperRightCorner;

    private final Vector2d mapLowerLeftCorner;
    private final Vector2d mapUpperRightCorner;


    // Different collections
    private final List<Animal> animalsList;
    private final Map<Vector2d, List<Animal>> animals;
    private final Map<Vector2d, Plant> plants;

    // TODO WYRZUCIĆ I ZMIENIĆ NA ZBIORY
    private final List<Vector2d> freePositionsSteppe;
    private final List<Vector2d> freePositionsJungle;

    // Used for testing
    private IRandomGenerator random;

    /**
     * Creates a map with given dimensions
     *
     * @param width
     *      Width of the map
     * @param height
     *      Height of the map
     * @param jungleRatio
     *      Ration of jungle dimensions to overall map dimensions
     *      eg. for jungleRatio = 0.5 the jungle width and height
     *      will equal the half of corresponding map dimensions
     *
     * @throws IllegalArgumentException
     *      If given map dimensions are incorrect
     */
    public WorldMap(int width, int height, double jungleRatio) throws IllegalArgumentException {
        // Detecting incorrect arguments
        if(jungleRatio < 0) {
            throw new IllegalArgumentException("Jungle ratio must be non negative");
        }

        if(width < 1 || height < 1) {
            throw new IllegalArgumentException("Map dimensions can't be zero or negative");
        }

        if(width * jungleRatio % 1 != 0 || height * jungleRatio % 1 != 0) {
            throw new IllegalArgumentException("Jungle must properly fit the given rectangle");
        }

        // Initializing map dimensions
        this.width = width;
        this.height = height;

        this.jungleWidth = (int) (width * jungleRatio);
        this.jungleHeight = (int) (height * jungleRatio);

        jungleLowerLeftCorner = new Vector2d((width - jungleWidth) / 2,
                (height - jungleHeight) / 2);

        jungleUpperRightCorner = new Vector2d(((width + jungleWidth) / 2) - 1,
                ((height + jungleHeight) / 2) -1);

        mapLowerLeftCorner = Vector2d.zero();
        mapUpperRightCorner = new Vector2d(width - 1, height - 1);

        // Initializing collections
        animalsList = new LinkedList<>();
        animals = new HashMap<>();
        plants = new HashMap<>();
        freePositionsSteppe = new LinkedList<>();
        freePositionsJungle = new LinkedList<>();

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Vector2d currentPosition = new Vector2d(i, j);

                if(isInsideJungle(currentPosition)) {
                    freePositionsJungle.add(currentPosition);
                } else {
                    freePositionsSteppe.add(currentPosition);
                }
            }
        }

        random = new RealRandom();
    }

    /**
     * Constructor used for testing
     * @param width
     *      Width of the map
     * @param height
     *      Height of the map
     * @param jungleRatio
     *      Ration of jungle dimensions to overall map dimensions
     *      eg. for jungleRatio = 0.5 the jungle width and height
     *      will equal the half of corresponding map dimensions
     * @param mockup
     *      IRandomGenerator object mocking random behaviour (for test purposes)
     *
     * @throws IllegalArgumentException
     *      If given map dimensions are incorrect
     */
    public WorldMap(int width, int height, double jungleRatio, IRandomGenerator mockup)
            throws IllegalArgumentException {
        this(width, height, jungleRatio);
        random = mockup;
    }

    //Accessors

    public Vector2d[] getMapCorners() {
        return new Vector2d[]{mapLowerLeftCorner, mapUpperRightCorner};
    }

    public Vector2d[] getJungleCorners() {
        return new Vector2d[]{jungleLowerLeftCorner, jungleUpperRightCorner};
    }

    public int[] getMapDimensions() {
        return new int[]{width, height};
    }

    public int getJungleWidth() {
        return jungleWidth;
    }

    public int getJungleHeight() {
        return jungleHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumberOfAnimals() {
        return animalsList.size();
    }

    /**
     * Returns a random position from the inside of the map
     *
     * @return
     *      Vector2d object representing the position
     */
    public Vector2d getRandomPositionFromMap() {
        Random random = new Random();

        int randomX = random.nextInt(width);
        int randomY = random.nextInt(height);

        return new Vector2d(randomX, randomY);
    }

    public Optional<Vector2d> getRandomPositionFromJungle() {
        if(!freePositionsJungle.isEmpty()) {
            Collections.shuffle(freePositionsJungle);
            return Optional.of(freePositionsJungle.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Vector2d> getRandomPositionFromSteppe() {
        if(!freePositionsSteppe.isEmpty()) {
            Collections.shuffle(freePositionsSteppe);
            return Optional.of(freePositionsSteppe.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns an iterator over the animals list.
     * To avoid concurrent modification errors, the iterator is,
     * in reality, an iterator over the copy of the list
     *
     * @return
     *      An iterator over the animal list
     */
    public Iterator<Animal> getAnimalListIterator() {
        return List.copyOf(animalsList).iterator();
    }

    /**
     * Returns an iterator over the keys of the animal to position map.
     * To avoid concurrent modification errors, the iterator is in reality
     * an iterator over the copy of the key set
     *
     * @return
     *      An iterator over the positions occupied by animals
     */
    public Iterator<Vector2d> getAnimalPositionsIterator() {
        Set<Vector2d> keySetCopy = Set.copyOf(animals.keySet());
        return keySetCopy.iterator();
    }

    public Optional<List<Animal>> getAnimalsListAt(Vector2d position) {
        if(animals.containsKey(position)) {
            return Optional.of(List.copyOf(animals.get(position)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns plant at a given position, or an empty Optional object if the position has no plants
     * @param position
     *      Position to check
     *
     * @return Optional object with the plant from the given position, or an empty Optional
     */
    public Optional<Plant> plantAt(Vector2d position) {
        if(plants.containsKey(position)) {
            return Optional.of(plants.get(position));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns an animal with the highest energy from the given position,
     * or an empty Optional object if the position has no animals
     *
     * @param position
     *      Position to check
     *
     * @return Optional containing an animal from the given position or an empty Optional
     */
    public Optional<Animal> animalAt(Vector2d position) {
        if(animals.containsKey(position)) {
            if(!animals.get(position).isEmpty()) {
                Animal highestEnergyAnimal = animals.get(position).get(0);

                for (Animal animal : animals.get(position)) {
                    if(animal.getEnergy() > highestEnergyAnimal.getEnergy()) {
                        highestEnergyAnimal = animal;
                    }
                }

                return Optional.of(highestEnergyAnimal);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "WorldMap{" +
                "width=" + width +
                ", height=" + height +
                ", jungleWidth=" + jungleWidth +
                ", jungleHeight=" + jungleHeight +
                ", jungleLowerLeftCorner=" + jungleLowerLeftCorner +
                ", jungleUpperRightCorner=" + jungleUpperRightCorner +
                ", mapLowerLeftCorner=" + mapLowerLeftCorner +
                ", mapUpperRightCorner=" + mapUpperRightCorner +
                '}';
    }

    /**
     * Helper function for visual testing
     */
    public void printMap() {
        for(int i = height - 1; i >= 0; i--) {
            for(int j = width - 1; j >= 0; j--) {
                Vector2d currentPosition = new Vector2d(j, i);

                if(isInsideJungle(currentPosition)) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" 0 ");
                }
            }

            System.out.print('\n');
        }
    }

    // Methods

    // TODO WYRZUCIĆ
    public void eatPlants(int energyFromPlant) {
        List<Plant> plantsToRemove = new LinkedList<>();

        // Finding plants that will be eaten
        for(Vector2d plantPosition : plants.keySet()) {
            if(animals.containsKey(plantPosition)) {
                Animal.eat(animals.get(plantPosition), energyFromPlant);
                plantsToRemove.add(plants.get(plantPosition));
            }
        }

        // Removing eaten plants
        for(Plant plant : plantsToRemove) {
            plant.removePlant();
        }
    }

    /**
     * Removes an animal from the map
     *
     * @param animal
     *      Animal to be removed
     */
    public void removeAnimalFromMap(Animal animal) {
        animalsList.remove(animal);
        animals.get(animal.getPosition()).remove(animal);
    }


    /**
     * Places animal on the map at the given position. If the position is incorrect, an error is thrown
     *
     * @param animal
     *      Animal object to be placed on the map
     * @param position
     *      Position to put the animal at
     * @throws IllegalArgumentException
     *          If given position is incorrect
     */
    private void placeAt(Animal animal, Vector2d position) {
        if(isInsideMap(position)) {
            //System.out.println("Animal is being placed at position " + position.toString());

            if (!animals.containsKey(position)) {
                animals.put(position, new LinkedList<>());
            }
            animals.get(position).add(animal);
        } else {
            throw new IllegalArgumentException("Animal is outside the map");
        }
    }

    /**
     * Places animal on the map. If it's position is incorrect, an error is thrown
     *
     * @param animal Animal object to be placed on the map
     *
     * @throws IllegalArgumentException
     *          If given animal is at an incorrect position
     */
    public void place(Animal animal) throws IllegalArgumentException{
        animalsList.add(animal);
        animal.addPositionObserver(this);
        placeAt(animal, animal.getPosition());
    }

    /**
     * Checks whether the vector is inside the map
     *
     * @param position
     *         Vector representing the position to check
     * @return True if the position is inside the map
     */
    public boolean isInsideMap(Vector2d position){
        return position.follows(mapUpperRightCorner) && position.precedes(mapLowerLeftCorner);
    }

    /**
     * Checks whether the vector is inside the jungle section of the map
     *
     * @param position
     *         Vector representing the position to check
     * @return True if the position is inside the jungle
     */
    //TODO - WRZUCIC DO KLASY VECTOR2D
    private boolean isInsideJungle(Vector2d position) {
        return position.follows(jungleUpperRightCorner) && position.precedes(jungleLowerLeftCorner);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        // Updating the animal map
        //System.out.println("Position of animal previously at position " + oldPosition.toString() + "is being adjusted" +
        //        "to " + newPosition.toString());
        List<Animal> animalList = animals.get(oldPosition);
        animalList.remove(animal);

        if(animalList.isEmpty()) {
            animals.remove(oldPosition);
        }

        placeAt(animal, newPosition);

        // Updating the free positions collections
        updatePositionStatusForPlants(oldPosition);
        removeFromPossiblePositionsForPlants(newPosition);
    }

    @Override
    public void plantEaten(Plant eatenPlant) {
        plants.remove(eatenPlant.getPosition());
        updatePositionStatusForPlants(eatenPlant.getPosition());
    }

    // TODO WYRZUCIĆ
    @Override
    public void newPlant(Plant newPlant) {
        plants.put(newPlant.getPosition(), newPlant);
        freePositionsJungle.remove(newPlant.getPosition());
    }

    // TODO WYRZUCIĆ
    /**
     * Checks whether the given position is not occupied by any plant or animal. If it's not,
     * adds the position to the corresponding free positions list.
     *
     * @param position
     *      Position to update
     */
    private void updatePositionStatusForPlants(Vector2d position) {
        if(!animals.containsKey(position) && plantAt(position).isEmpty()) {
            if (isInsideJungle(position)) {
                if (!freePositionsJungle.contains(position)) {
                    freePositionsJungle.add(position);
                }
            } else {
                if (!freePositionsSteppe.contains(position)) {
                    freePositionsSteppe.add(position);
                }
            }
        }
    }

    /**
     * Removes position from the corresponding collection of free
     * positions for plants, if present (optional operation)
     *
     * @param position
     *      Position to remove
     */
    private void removeFromPossiblePositionsForPlants(Vector2d position) {
        if (isInsideJungle(position)) {
            freePositionsJungle.remove(position);
        } else {
            freePositionsSteppe.remove(position);
        }
    }
}
