package util;

import java.util.Random;

public class RealRandom implements IRandomGenerator{
    @Override
    public int nextInt(int maxValue) {
        Random random = new Random();
        return random.nextInt(maxValue);
    }
}
