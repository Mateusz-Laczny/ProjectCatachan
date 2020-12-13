package entities;

import datatypes.Vector2d;
import org.junit.jupiter.api.Test;
import util.randomMock.IRandomGenerator;
import util.randomMock.MockRandom;

import java.util.List;

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
    public void correctlyGeneratesGrass() {
        List<Integer> valuesForRandom = List.of(0, 0);
        IRandomGenerator mockup = new MockRandom(valuesForRandom);

        WorldMap map = new WorldMap(12, 12, 0.5, mockup);
        map.generatePlants();

        assertTrue(map.plantAt(Vector2d.zero()).isPresent());
        assertTrue(map.plantAt(new Vector2d(3,3)).isPresent());
    }

//    @Test
//    public void animalsEatPlants() {
//        List<Integer> valuesForRandom = List.of(0, 0);
//        IRandomGenerator mockup = new MockRandom(valuesForRandom);
//
//        WorldMap map = new WorldMap(12, 12, 0.5, mockup);
//        map.generatePlants();
//
//        Animal puszek = new Animal(map, new Vector2d(0, 0), 10,
//                new Genotype(32, 8));
//
//        Animal pysia = new Animal(map, new Vector2d(3, 3), 10,
//                new Genotype(32, 8));
//
//        map.eatPlants(5);
//
//        assertTrue(map.plantAt(puszek.getPosition()).isEmpty());
//        assertTrue(map.plantAt(pysia.getPosition()).isEmpty());
//    }

    @Test
    public void showsCorrectNumberOfAnimals() {
        WorldMap map = new WorldMap(12, 12, 0.5);

        for (int i = 0; i < 4; i++) {
            new Animal(map, 1, 32, 8);
        }

        new Animal(map, 2, 32, 8);

        // Full simulation cycle
        assertEquals(5, map.getNumberOfAnimals());

        map.removeDeadAnimals();
        assertEquals(5, map.getNumberOfAnimals());

        map.moveAnimals(1);
        assertEquals(5, map.getNumberOfAnimals());

        map.eatPlants(10);
        assertEquals(5, map.getNumberOfAnimals());

        map.reproduceAllAnimals(10);
        assertEquals(5, map.getNumberOfAnimals());

        map.generatePlants();
        assertEquals(5, map.getNumberOfAnimals());

        // 4 animals should die
        map.removeDeadAnimals();
        assertEquals(1, map.getNumberOfAnimals());
    }
}