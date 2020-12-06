package entities;

public class Simulation {
    private final int startingEnergy;
    private final WorldMap map;
    private final int energyFromPlant;
    private int currentDay;

    // TODO OBSŁUGA BŁĘDNYCH PARAMETRÓW
    public Simulation(int startingEnergy, WorldMap map, int startingNumOfAnimals, int numOfGenes,
                      int numOfGeneTypes, int energyFromPlant) {
        this.startingEnergy = startingEnergy;
        this.map = map;
        this.energyFromPlant = energyFromPlant;
        currentDay = 0;

        for(int i = 0; i < startingNumOfAnimals; i++) {
            new Animal(map, startingEnergy, numOfGenes, numOfGeneTypes);
        }
    }

    public void runSimulation() {
        while(map.getNumberOfAnimals() > 0) {
            System.out.println("Day " + currentDay);
            System.out.println("Number of animals " + map.getNumberOfAnimals());

            map.removeDeadAnimals();
            System.out.println("Removed dead animals");
            map.moveAnimals();
            System.out.println("Moved animals");
            map.eatPlants(energyFromPlant);
            System.out.println("Animals ate plants");
            map.reproduceAllAnimals(startingEnergy);
            System.out.println("Animals reproduced");
            map.generatePlants();
            System.out.println("Plants generated");

            System.out.println(".");
            System.out.println(".");
            System.out.println(".");

            currentDay += 1;

            System.out.println();
        }

        System.out.println("Animals survived " + currentDay + " days");
    }
}
