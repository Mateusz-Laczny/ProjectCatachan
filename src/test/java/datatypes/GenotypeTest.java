package datatypes;

import org.junit.jupiter.api.Test;
import util.IRandomGenerator;
import util.MockRandom;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenotypeTest {
    @Test
    public void correctlyInitializesObject() {
        assertDoesNotThrow(() -> new Genotype(32, 8));
        Genotype genotype = new Genotype(32, 8);

        assertEquals(32, genotype.getGenomeLength());
        assertEquals(8, genotype.getGeneTypesNumber());
    }

    @Test
    public void correctlyGeneratesGenes() {
        List<Integer> genotype1 = List.of(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3,
                3, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7);

        IRandomGenerator mockup1 = new MockRandom(genotype1);
        Genotype test = new Genotype(32, 8, mockup1);

        assertEquals("Genotype{genes=[0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, " +
                "4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7]}", test.toString());
    }

    @Test
    //TODO
    public void correctlyGeneratesGenotypeFromParents() {
        List<Integer> genotype1 = List.of(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3,
                3, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7);

        List<Integer> genotype2 = List.of(0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
                2, 3, 3, 3, 3, 3, 3, 4, 5, 5, 5, 5, 6, 7, 7, 7, 7, 7, 7);

        IRandomGenerator mockup1 = new MockRandom(genotype1);
        IRandomGenerator mockup2 = new MockRandom(genotype2);

        List<Integer> testValuesForGenotypeGeneration = List.of(1, 5);
    }
}