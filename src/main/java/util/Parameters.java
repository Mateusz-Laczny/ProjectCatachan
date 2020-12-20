package util;

/**
 * Container for parameters imported from a JSON file
 */
public class Parameters {
    public int width;
    public int height;
    public int startEnergy;
    public int plantEnergy;
    public int moveEnergy;
    public double jungleRatio;

    @Override
    public String toString() {
        return "Parameters{" +
                "width=" + width +
                ", height=" + height +
                ", startEnergy=" + startEnergy +
                ", plantEnergy=" + plantEnergy +
                ", moveEnergy=" + moveEnergy +
                ", jungleRatio=" + jungleRatio +
                '}';
    }
}
