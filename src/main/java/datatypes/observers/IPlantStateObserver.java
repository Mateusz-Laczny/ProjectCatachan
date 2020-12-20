package datatypes.observers;

import entities.Plant;

public interface IPlantStateObserver {
    void plantEaten(Plant eatenPlant);

    void newPlant(Plant newPlant);
}
