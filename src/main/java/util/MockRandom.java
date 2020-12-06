package util;

import java.util.LinkedList;
import java.util.List;

public class MockRandom implements IRandomGenerator{
    private final List<Integer> values;
    private int currentIndex;

    /**
     * Creates a fake random class, which returns values from the given list
     * @param values
     *      Values to return when method nextInt is called
     */
    public MockRandom(List<Integer> values) {
        this.values = List.copyOf(values);
        currentIndex = 0;
    }

    /**
     * Method mocking the real random function
     * @param maxValue
     *      Not used
     * @return
     *      Next value from the value set
     */
    @Override
    public int nextInt(int maxValue) {
        int valueToReturn = values.get(currentIndex);
        currentIndex = (currentIndex + 1) % values.size();

        return valueToReturn;
    }
}
