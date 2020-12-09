package entities;

import datatypes.Vector2d;

import java.util.Optional;

public class Simulation {
    // Simulation parameters
    private final int startEnergy;
    private final WorldMap map;
    private final int plantEnergy;
    private final int moveEnergy;

    private boolean isRunning;

    private int currentDay;

    // TODO OBSŁUGA BŁĘDNYCH PARAMETRÓW
    public Simulation(int width, int height, int startEnergy, int plantEnergy, int moveEnergy, double jungleRatio) {
        this.startEnergy = startEnergy;
        this.map = new WorldMap(width, height, jungleRatio);
        this.plantEnergy = plantEnergy;
        this.moveEnergy = moveEnergy;

        isRunning = false;

        currentDay = 0;
    }

    public void generateAnimalsAtRandomPositions(int numberOfAnimals) {
        map.generateAnimalsAtRandomPositions(numberOfAnimals, startEnergy, 32, 8);
    }

    public void removeDeadAnimals() {
        map.removeDeadAnimals();
        //System.out.println("Removed dead animals");
    }

    public void moveAnimals() {
        map.moveAnimals(moveEnergy);
        //System.out.println("Moved Animals");
    }

    public void eatPlants() {
        map.eatPlants(plantEnergy);
        //System.out.println("Animals ate plants");
    }

    public void reproduceAnimals() {
        map.reproduceAllAnimals(startEnergy);
        //System.out.println("Animals reproduced");
    }

    public void generatePlants() {
        map.generatePlants();
        //System.out.println("Plants generated");
        currentDay += 1;
    }

    public Optional<Animal> animalAt(Vector2d position) {
        return map.animalAt(position);
    }

    public Optional<Plant> plantAt(Vector2d position) {
        return map.plantAt(position);
    }

    public int getNumberOfAnimals() {
        return map.getNumberOfAnimals();
    }

    public int getCurrentDay() {
        return currentDay;
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
}
