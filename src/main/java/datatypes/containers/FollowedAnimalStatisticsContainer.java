package datatypes.containers;

public class FollowedAnimalStatisticsContainer {
    public final int numberOfChildren;
    public final int numberOfDescendants;
    public final int deathDay;

    public FollowedAnimalStatisticsContainer(int numberOfChildren, int numberOfDescendants, int deathDay) {
        this.numberOfChildren = numberOfChildren;
        this.numberOfDescendants = numberOfDescendants;
        this.deathDay = deathDay;
    }
}
