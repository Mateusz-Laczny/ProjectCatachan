package entities;

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
    }

    public void moveAnimals() {
        map.moveAnimals(moveEnergy);
    }

    public void eatPlants() {
        map.eatPlants(plantEnergy);
    }

    public void reproduceAnimals() {
        map.reproduceAllAnimals(startEnergy);
    }

    public void generatePlants() {
        map.generatePlants();
        currentDay += 1;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void runSimulationInConsole() {

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
