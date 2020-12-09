package util.tasks;

import entities.Simulation;

public class EatPlantsTask extends AbstractSimulationTask{
    public EatPlantsTask(Simulation simulation) {
        super(simulation);
    }

    @Override
    public Void call() throws Exception{
        simulation.eatPlants();
        return null;
    }
}
