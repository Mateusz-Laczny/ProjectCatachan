package datatypes;

import java.util.Map;

public class StatisticsContainer {
    public final int currentDay;

    public final int numberOfAnimals;
    public final int numberOfPlants;
    public final float meanEnergyLevel;
    public final float meanLifespan;
    public final float meanNumberOfChildren;
    public final Map<Direction, Integer> genesCount;

    public StatisticsContainer(int numberOfAnimals, int numberOfPlants, float meanEnergyLevel,
                               float meanLifespan, float meanNumberOfChildren, int currentDay,
                               Map<Direction, Integer> genesCount) {
        this.numberOfAnimals = numberOfAnimals;
        this.numberOfPlants = numberOfPlants;
        this.meanEnergyLevel = meanEnergyLevel;
        this.meanLifespan = meanLifespan;
        this.meanNumberOfChildren = meanNumberOfChildren;
        this.currentDay = currentDay;
        this.genesCount = genesCount;
    }
}
