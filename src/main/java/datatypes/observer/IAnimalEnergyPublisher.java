package datatypes.observer;

public interface IAnimalEnergyPublisher {
    void addEnergyObserver(IAnimalEnergyObserver observer);

    void removeEnergyObserver(IAnimalEnergyObserver observer);
}
