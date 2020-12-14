package entities;

import datatypes.Direction;
import datatypes.Genotype;
import datatypes.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    @Test
    public void correctlyInitializesAnimal() {
        WorldMap map = new WorldMap(10, 6, 0.5);

        Animal puszek = new Animal(map, Vector2d.zero(), 10,
                new Genotype(32, 8));

        assertEquals(10, puszek.getEnergy());
        assertEquals(Vector2d.zero(), puszek.getPosition());
    }

    @Test
    public void animalMovesInAGivenDirection() {
        WorldMap map = new WorldMap(10, 6, 0.5);

        Animal puszek = new Animal(map, Vector2d.zero(), 10,
                new Genotype(32, 8));

        puszek.move(Direction.N);
        assertEquals(new Vector2d(0, 1), puszek.getPosition());
    }

    @Test
    public void animalDiesIfItsEnergyDropsBellow0() {
        WorldMap map = new WorldMap(60, 10, 0.6);

        Animal reksio = new Animal(map, Vector2d.one(), 1,
                new Genotype(32, 8));

        Simulation simulation = new Simulation(map, 1, 1, 1);

        reksio.addStateObserver(simulation);
        reksio.randomMove(1);

        simulation.removeDeadAnimals();

        assertTrue(map.animalAt(Vector2d.one()).isEmpty());
        assertTrue(map.animalAt(new Vector2d(1, 2)).isEmpty());
        assertTrue(map.animalAt(new Vector2d(1, 0)).isEmpty());
        assertTrue(map.animalAt(new Vector2d(0, 1)).isEmpty());
        assertTrue(map.animalAt(Vector2d.zero()).isEmpty());
        assertTrue(map.animalAt(new Vector2d(0, 2)).isEmpty());
        assertTrue(map.animalAt(new Vector2d(2, 2)).isEmpty());
        assertTrue(map.animalAt(new Vector2d(2, 1)).isEmpty());
        assertTrue(map.animalAt(new Vector2d(2, 0)).isEmpty());
    }
}