package util.tasks;

import entities.Simulation;

public class GeneratePlantsTask extends AbstractSimulationTask{
    public GeneratePlantsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    public Void call() throws Exception {
        simulation.generatePlants();
        return null;
    }
}
