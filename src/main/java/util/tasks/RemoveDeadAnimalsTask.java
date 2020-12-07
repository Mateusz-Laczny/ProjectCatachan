package util.tasks;

import entities.Simulation;

public class RemoveDeadAnimalsTask extends AbstractSimulationTask{
    public RemoveDeadAnimalsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    protected Void call() throws Exception {
        simulation.removeDeadAnimals();
        return null;
    }
}
