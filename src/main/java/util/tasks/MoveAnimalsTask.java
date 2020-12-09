package util.tasks;

import entities.Simulation;

public class MoveAnimalsTask extends AbstractSimulationTask{
    public MoveAnimalsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    public Void call() throws Exception {
        simulation.moveAnimals();
        return null;
    }
}
