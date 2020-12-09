package util.tasks;

import entities.Simulation;
import javafx.concurrent.Task;

public abstract class AbstractSimulationTask extends Task<Void> {
    protected Simulation simulation;

    public AbstractSimulationTask(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public abstract Void call() throws Exception;
}
