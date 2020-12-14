package managers;

import datatypes.Vector2d;
import datatypes.observer.IAnimalStateObserver;
import entities.Animal;

import java.util.HashSet;
import java.util.Set;

public class StatisticsManager implements IAnimalStateObserver {
    private Animal followedAnimal;

    // Followed animal statistics
    private int followedAnimalDeathDate;
    private Set<Animal> followedAnimalChildren;
    private Set<Animal> followedAnimalDescendants;

    private int currentDay;

    public StatisticsManager() {
        currentDay = 0;
    }

    public void setFollowedAnimal(Animal followedAnimal) {
        this.followedAnimal = followedAnimal;
        followedAnimalChildren = new HashSet<>();
        followedAnimalDescendants = new HashSet<>();
    }

    public void incrementDay() {
        currentDay += 1;
    }

    public Animal getFollowedAnimal() {
        return followedAnimal;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    @Override
    public void animalDied(Animal deadAnimal) {
        if(deadAnimal.equals(followedAnimal)) {
            followedAnimalDeathDate = currentDay;
        }
    }

    @Override
    public void animalBorn(Animal parent, Animal child) {
        if(parent.equals(followedAnimal)) {
            followedAnimalChildren.add(child);
            followedAnimalDescendants.add(child);
        } else if(followedAnimalDescendants.contains(child)) {
            followedAnimalDescendants.add(child);
        }
    }
}
