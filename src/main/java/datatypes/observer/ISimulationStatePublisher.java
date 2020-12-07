package datatypes.observer;

public interface ISimulationStatePublisher {
    void addObserver(ISimulationStateObserver observer);

    void removeObserver(ISimulationStateObserver observer);
}
