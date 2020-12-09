package util.tasks;

import entities.Simulation;

public class ReproduceAnimalsTask extends AbstractSimulationTask{
    public ReproduceAnimalsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    public Void call() throws Exception {
        simulation.reproduceAnimals();
        return null;
    }
}
