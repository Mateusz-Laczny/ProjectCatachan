package entities;

import datatypes.Vector2d;
import datatypes.observers.IAnimalPositionObserver;
import datatypes.observers.IPlantStateObserver;

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
    private final Set<Animal> animalsList;
    private final Map<Vector2d, List<Animal>> animals;
    private final Map<Vector2d, Plant> plants;

    private final Set<Vector2d> freePositionsSteppe;
    private final Set<Vector2d> freePositionsJungle;

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
        animalsList = new HashSet<>();
        animals = new HashMap<>();
        plants = new HashMap<>();
        freePositionsSteppe = new HashSet<>();
        freePositionsJungle = new HashSet<>();

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
    }

    //Accessors
    public Vector2d[] getMapCorners() {
        return new Vector2d[]{mapLowerLeftCorner, mapUpperRightCorner};
    }

    public Vector2d[] getJungleCorners() {
        return new Vector2d[]{jungleLowerLeftCorner, jungleUpperRightCorner};
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

    public Optional<Vector2d> getRandomFreePositionFromJungle() {
        if(!freePositionsJungle.isEmpty()) {
            List<Vector2d> freePositionsJungleArrayList = new ArrayList<>(freePositionsJungle);

            Collections.shuffle(freePositionsJungleArrayList);
            return Optional.of(freePositionsJungleArrayList.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Vector2d> getRandomFreePositionFromSteppe() {
        if(!freePositionsSteppe.isEmpty()) {
            List<Vector2d> freePositionsSteppeArrayList = new ArrayList<>(freePositionsSteppe);

            Collections.shuffle(freePositionsSteppeArrayList);
            return Optional.of(freePositionsSteppeArrayList.get(0));
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
    public Iterator<Animal> getAnimalsIterator() {
        return List.copyOf(animalsList).iterator();
    }

    public Iterator<Plant> getPlantsIterator() {
        Set<Plant> valuesSetCopy = Set.copyOf(plants.values());
        return valuesSetCopy.iterator();
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
                // Animals at a given position should always be sorted
                // decreasingly by their energy
                return Optional.of(animals.get(position).get(0));
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

    // Methods
    /**
     * Removes an animal from the map
     *
     * @param animal
     *      Animal to be removed
     */
    public void removeAnimalFromMap(Animal animal) {
        animalsList.remove(animal);

        List<Animal> animalsAtRemovedAnimalsPosition = animals.get(animal.getPosition());
        animalsAtRemovedAnimalsPosition.remove(animal);

        // We do not store empty lists in positions to animals map
        if(animalsAtRemovedAnimalsPosition.isEmpty()) {
            animals.remove(animal.getPosition());
        }

        updatePositionStatusForPlants(animal.getPosition());
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
    private void placeAt(Animal animal, Vector2d position) throws IllegalArgumentException{
        if(isInsideMap(position)) {
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
        animals.get(animal.getPosition()).sort(Comparator.comparing(Animal::getEnergy).reversed());
        removeFromPossiblePositionsForPlants(animal.getPosition());
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
    private boolean isInsideJungle(Vector2d position) {
        return position.follows(jungleUpperRightCorner) && position.precedes(jungleLowerLeftCorner);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        // Updating the animal map
        List<Animal> animalList = animals.get(oldPosition);
        animalList.remove(animal);

        if(animalList.isEmpty()) {
            animals.remove(oldPosition);
        } else {
            animalList.sort(Comparator.comparing(Animal::getEnergy).reversed());
        }

        placeAt(animal, newPosition);

        // We have to sort because always keep animals at a given position sorted by their energy
        animals.get(newPosition).sort(Comparator.comparing(Animal::getEnergy).reversed());

        // Updating the free positions collections
        updatePositionStatusForPlants(oldPosition);
        removeFromPossiblePositionsForPlants(newPosition);
    }

    @Override
    public void plantEaten(Plant eatenPlant) {
        plants.remove(eatenPlant.getPosition());
        updatePositionStatusForPlants(eatenPlant.getPosition());
    }

    @Override
    public void newPlant(Plant newPlant) {
        plants.put(newPlant.getPosition(), newPlant);
        removeFromPossiblePositionsForPlants(newPlant.getPosition());
    }

    /**
     * Checks whether the given position is not occupied by any plant or animal. If it's not,
     * adds the position to the corresponding free positions list.
     *
     * @param position
     *      Position to update
     */
    private void updatePositionStatusForPlants(Vector2d position) {
        if(!animals.containsKey(position)) {
            if(plantAt(position).isEmpty()) {
                if (isInsideJungle(position)) {
                    freePositionsJungle.add(position);
                } else {
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

    // Methods for testing
    public boolean isAFreePositionForPlants(Vector2d position) {
        return freePositionsSteppe.contains(position) || freePositionsJungle.contains(position);
    }
}
