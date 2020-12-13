package util.randomMock;

public interface IRandomGenerator {
    /**
     * Returns a pseudorandom int value between 0 (inclusive) and max (exclusive).
     * Is equal to nextint(maxValue) from java.util.Random
     * @param maxValue
     *      Maximal possible returned value (exclusive)
     * @return
     *      Pseudorandom int value between 0 and maxValue
     */
    int nextInt(int maxValue);
}
