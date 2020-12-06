package application;

import datatypes.Direction;
import datatypes.Genotype;
import datatypes.Vector2d;
import entities.Animal;
import entities.Simulation;
import entities.WorldMap;

public class Main {
    public static void main(String[] args) {
        WorldMap map = new WorldMap(30, 30, 0.2);
        Simulation simulation = new Simulation(20, map, 100,
                32, 8, 10);

        simulation.runSimulation();
    }
}
