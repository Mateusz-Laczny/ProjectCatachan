package util.tasks;

import entities.Simulation;

public class ReproduceAnimalsTask extends AbstractSimulationTask{
    public ReproduceAnimalsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    protected Void call() throws Exception {
        simulation.reproduceAnimals();
        return null;
    }
}
