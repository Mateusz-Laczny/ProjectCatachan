package entities;

import datatypes.Genotype;
import datatypes.Vector2d;
import org.junit.jupiter.api.Test;
import util.randomMock.MockRandom;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {
    @Test
    public void mapHasCorrectDimensions() {
        WorldMap map = new WorldMap(10, 6, 0.5);
        assertEquals(10, map.getWidth());
        assertEquals(6, map.getHeight());
    }

    @Test
    public void jungleHasCorrectDimensions() {
        WorldMap map = new WorldMap(10, 6, 0.5);
        assertEquals(5, map.getJungleWidth());
        assertEquals(3, map.getJungleHeight());
    }

    @Test
    void correctlyPlacesAnimals() {
        WorldMap map = new WorldMap(12, 12, 0.5);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 10,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(3, 3), 10,
                new Genotype(32, 8));

        assertTrue(map.animalAt(puszek.getPosition()).isPresent());
        assertTrue(map.animalAt(pysia.getPosition()).isPresent());

        assertEquals(1, map.getAnimalsListAt(puszek.getPosition()).get().size());
        assertEquals(1, map.getAnimalsListAt(pysia.getPosition()).get().size());
    }

    @Test
    void correctlyRemovesAnimals() {
        WorldMap map = new WorldMap(12, 12, 0.5);
        Simulation simulation = new Simulation(map, 1, 1, 1, 32, 8);

        Animal tyranid1 = new Animal(map, new Vector2d(0, 0), 10,
                new Genotype(32, 8));

        Animal tyranid2 = new Animal(map, new Vector2d(3, 3), 10,
                new Genotype(32, 8));

        tyranid1.addStateObserver(simulation);
        tyranid2.addStateObserver(simulation);

        tyranid1.die();
        tyranid2.die();

        simulation.removeDeadAnimals();

        assertTrue(map.animalAt(tyranid1.getPosition()).isEmpty());
        assertTrue(map.animalAt(tyranid2.getPosition()).isEmpty());

        assertTrue(map.getAnimalsListAt(tyranid1.getPosition()).isEmpty());
        assertTrue(map.getAnimalsListAt(tyranid2.getPosition()).isEmpty());
    }

    @Test
    public void correctlyRemovesPlants() {
        WorldMap map = new WorldMap(12, 12, 0.5);
        Simulation simulation = new Simulation(map, 1, 1, 1, 32, 8);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 10,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(3, 3), 10,
                new Genotype(32, 8));

        Plant plant1 = new Plant(puszek.getPosition());
        Plant plant2 = new Plant(pysia.getPosition());

        plant1.addPlantObserver(map);
        plant2.addPlantObserver(map);

        plant1.notifyAboutANewPlant();
        plant2.notifyAboutANewPlant();

        simulation.eatPlants();

        assertTrue(map.plantAt(puszek.getPosition()).isEmpty());
        assertTrue(map.plantAt(pysia.getPosition()).isEmpty());

        Iterator<Plant> plantIterator = map.getPlantsIterator();

        while (plantIterator.hasNext()) {
            Plant currentPlant = plantIterator.next();
            assertNotEquals(plant1, currentPlant);
            assertNotEquals(plant2, currentPlant);
        }
    }

    @Test
    public void animalAtReturnsAnimalWithHighestEnergy() {
        WorldMap map = new WorldMap(20, 20, 0.2);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 20,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(0, 0), 20,
                new Genotype(32, 8));

        Animal skaven = new Animal(map, new Vector2d(0, 0), 30,
                new Genotype(32, 8));

        assertEquals(skaven, map.animalAt(new Vector2d(0, 0)).get());
    }

    @Test
    public void showsCorrectNumberOfAnimals() {
        WorldMap map = new WorldMap(12, 12, 0.5);
        Simulation simulation = new Simulation(map, 1, 1, 1, 32, 8);

        for (int i = 0; i < 4; i++) {
            Animal animal = new Animal(map, 1, 32, 8);
            animal.addStateObserver(simulation);
        }

        Animal animal = new Animal(map, 2, 32, 8);
        animal.addStateObserver(simulation);

        // Full simulation cycle
        assertEquals(5, map.getNumberOfAnimals());

        simulation.removeDeadAnimals();
        assertEquals(5, map.getNumberOfAnimals());

        simulation.moveAnimals();
        assertEquals(5, map.getNumberOfAnimals());

        simulation.eatPlants();
        assertEquals(5, map.getNumberOfAnimals());

        simulation.reproduceAnimals();
        assertEquals(5, map.getNumberOfAnimals());

        simulation.generatePlants();
        assertEquals(5, map.getNumberOfAnimals());

        // 4 animals should die
        simulation.removeDeadAnimals();
        assertEquals(1, map.getNumberOfAnimals());
    }

    @Test
    public void correctlyRemovesPositionFromFreePositionsForPlants() {
        WorldMap map = new WorldMap(12, 12, 0.5);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 10,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(3, 3), 10,
                new Genotype(32, 8));

        assertFalse(map.isAFreePositionForPlants(puszek.getPosition()));
        assertFalse(map.isAFreePositionForPlants(pysia.getPosition()));

        Plant plant1 = new Plant(new Vector2d(1, 1));
        Plant plant2 = new Plant(new Vector2d(2, 2));

        plant1.addPlantObserver(map);
        plant2.addPlantObserver(map);

        plant1.notifyAboutANewPlant();
        plant2.notifyAboutANewPlant();

        assertFalse(map.isAFreePositionForPlants(plant1.getPosition()));
        assertFalse(map.isAFreePositionForPlants(plant2.getPosition()));
    }

    @Test
    public void correctlyAddsPositionToFreePositionsForPlants() {
        WorldMap map = new WorldMap(12, 12, 0.5);
        Simulation simulation = new Simulation(map, 1, 1, 1, 32, 8);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 10,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(3, 3), 10,
                new Genotype(32, 8));

        puszek.addStateObserver(simulation);
        pysia.addStateObserver(simulation);

        puszek.die();
        pysia.die();

        simulation.removeDeadAnimals();

        assertTrue(map.isAFreePositionForPlants(puszek.getPosition()));
        assertTrue(map.isAFreePositionForPlants(pysia.getPosition()));

        Plant plant1 = new Plant(new Vector2d(1, 1));
        Plant plant2 = new Plant(new Vector2d(2, 2));

        plant1.addPlantObserver(map);
        plant2.addPlantObserver(map);

        plant1.notifyAboutANewPlant();
        plant2.notifyAboutANewPlant();

        plant1.removePlant();
        plant2.removePlant();

        assertTrue(map.isAFreePositionForPlants(plant1.getPosition()));
        assertTrue(map.isAFreePositionForPlants(plant2.getPosition()));
    }

    @Test
    void listOfAnimalsAtPositionIsCorrectlySorted() {
        WorldMap map = new WorldMap(20, 20, 0.2);

        Animal puszek = new Animal(map, new Vector2d(0, 0), 20,
                new Genotype(32, 8));

        Animal pysia = new Animal(map, new Vector2d(0, 0), 20,
                new Genotype(32, 8));

        Animal skaven = new Animal(map, new Vector2d(0, 0), 30,
                new Genotype(32, 8));

        List<Animal> listOfAnimalsAtPosition = map.getAnimalsListAt(Vector2d.zero()).get();

        int previousEnergy = 30;

        for(Animal animal : listOfAnimalsAtPosition) {
            assertTrue(animal.getEnergy() <= previousEnergy);
            previousEnergy = animal.getEnergy();
        }
    }
}