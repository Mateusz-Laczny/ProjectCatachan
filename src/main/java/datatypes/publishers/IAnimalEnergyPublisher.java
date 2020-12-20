package datatypes.publishers;

import datatypes.observers.IAnimalEnergyObserver;

public interface IAnimalEnergyPublisher {
    void addEnergyObserver(IAnimalEnergyObserver observer);

    void removeEnergyObserver(IAnimalEnergyObserver observer);
}
