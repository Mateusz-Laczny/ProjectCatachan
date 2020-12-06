package datatypes.observer;

import entities.Plant;

public interface IPlantStateObserver {
    void plantEaten(Plant eatenPlant);
}
