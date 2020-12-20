package datatypes;

import java.util.*;

public class Genotype {
    private final int[] genes;
    private final int geneTypesNumber;
    // For testing purposes
    private final Random random;

    /**
     * Creates a correct, random genome. A correct genome must include at least one occurrence of every gene type
     *
     * @param lengthOfGenome
     *          Length of the genome
     *
     * @param geneTypesNumber
     *          Number of gene types
     *
     * @throws IllegalArgumentException
     *          If the given parameters are incorrect
     */
    public Genotype(int lengthOfGenome, int geneTypesNumber) throws IllegalArgumentException{
        if (lengthOfGenome < 0) {
            throw new IllegalArgumentException("Genome length can't be negative");
        }

        if (geneTypesNumber < 0 || geneTypesNumber > lengthOfGenome) {
            throw new IllegalArgumentException("Incorrect number of gene types");
        }
        random = new Random();

        this.geneTypesNumber = geneTypesNumber;
        genes = new int[lengthOfGenome];
        createRandomGenotype();
    }

    /**
     * Creates a correct genome based on given parent's genomes.
     *
     * @param firstParentGenotype
     *      Genome of the first parent
     * @param secondParentGenotype
     *      Genome of the second parent
     *
     * @throws IllegalArgumentException
     *          If the given parameters are incorrect
     */
    public Genotype(Genotype firstParentGenotype, Genotype secondParentGenotype) throws IllegalArgumentException {
        checkParametersCorrectness(firstParentGenotype, secondParentGenotype);

        geneTypesNumber = firstParentGenotype.getGeneTypesNumber();
        genes = new int[firstParentGenotype.getGenotypeLength()];

        random = new Random();

        if(random.nextInt(2) == 0) {
            createGenotypeFromParents(firstParentGenotype, secondParentGenotype);
        } else {
            createGenotypeFromParents(secondParentGenotype, firstParentGenotype);
        }
    }

    /**
     * Constructor for class testing
     *
     * @param firstParentGenotype
     *      Genome of the first parent
     * @param secondParentGenotype
     *      Genome of the second parent
     *
     * @throws IllegalArgumentException
     *          If the given parameters are incorrect
     */
    public Genotype(Genotype firstParentGenotype, Genotype secondParentGenotype, Random mockupRandom)
            throws IllegalArgumentException {

        checkParametersCorrectness(firstParentGenotype, secondParentGenotype);

        geneTypesNumber = firstParentGenotype.getGeneTypesNumber();
        genes = new int[firstParentGenotype.getGenotypeLength()];

        random = mockupRandom;

        if(random.nextInt(2) == 0) {
            createGenotypeFromParents(firstParentGenotype, secondParentGenotype);
        } else {
            createGenotypeFromParents(secondParentGenotype, firstParentGenotype);
        }
    }

    /**
     * Constructor for class testing
     *
     * @param lengthOfGenome
     *      Length of the genome
     * @param geneTypesNumber
     *      Number of gene types
     * @param mockupRandom
     *      Random function mockup for testing
     *
     * @throws IllegalArgumentException
     *          If the given parameters are incorrect
     */
    public Genotype(int lengthOfGenome, int geneTypesNumber, Random mockupRandom)
            throws IllegalArgumentException{
        if (lengthOfGenome < 0) {
            throw new IllegalArgumentException("Genome length can't be negative");
        }

        if (geneTypesNumber < 0 || geneTypesNumber > lengthOfGenome) {
            throw new IllegalArgumentException("Incorrect number of gene types");
        }
        random = mockupRandom;

        this.geneTypesNumber = geneTypesNumber;
        genes = new int[lengthOfGenome];
        createRandomGenotype();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genotype genotype = (Genotype) o;
        return geneTypesNumber == genotype.geneTypesNumber &&
                Arrays.equals(genes, genotype.genes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(geneTypesNumber);
        result = 31 * result + Arrays.hashCode(genes);
        return result;
    }

    @Override
    public String toString() {
        return "Genotype{" +
                "genes=" + Arrays.toString(genes) +
                '}';
    }

    private void checkParametersCorrectness(Genotype firstParentGenotype, Genotype secondParentGenotype)
            throws IllegalArgumentException {

        if(firstParentGenotype.getGenotypeLength() != secondParentGenotype.getGenotypeLength()) {
            throw new IllegalArgumentException("Genomes are not of the same length");
        }

        if(firstParentGenotype.getGeneTypesNumber() != secondParentGenotype.getGeneTypesNumber()) {
            throw new IllegalArgumentException("Genomes are not compatible");
        }
    }

    /**
     * Creates a genotype by slicing the genotypes of given parents.
     * Genome consists of two parts from the dominating parent and one part from the other.
     *
     * @param dominatingParent
     *      Genotype of the dominating parent
     * @param otherParent
     *      Genotype of the other parent
     */
    private void createGenotypeFromParents(Genotype dominatingParent, Genotype otherParent) {
        int[] cutIndexes = new int[2];

        cutIndexes[0] = random.nextInt(genes.length - 2);
        cutIndexes[1] = random.nextInt(genes.length - cutIndexes[0] - 2) + cutIndexes[0] + 1;

        List<Integer> parts = new LinkedList<>();
        parts.add(1);
        parts.add(2);
        parts.add(3);

        for(int i = 0; i < 2; i++) {
            int partIndex = random.nextInt(parts.size());

            int chosenPart = parts.get(partIndex);
            parts.remove(partIndex);

            switch (chosenPart) {
                case 1 -> copyGenes(dominatingParent, 0, cutIndexes[0]);
                case 2 -> copyGenes(dominatingParent, cutIndexes[0] + 1, cutIndexes[1]);
                case 3 -> copyGenes(dominatingParent, cutIndexes[1] + 1, genes.length - 1);
            }
        }

        // One part should remain
        int remainingPart = parts.get(0);

        switch (remainingPart) {
            case 1 -> copyGenes(otherParent, 0, cutIndexes[0]);
            case 2 -> copyGenes(otherParent, cutIndexes[0] + 1, cutIndexes[1]);
            case 3 -> copyGenes(otherParent, cutIndexes[1] + 1, genes.length - 1);
        }

        // Genome can be out of order after the slicing
        repairGenotype();
    }

    private void copyGenes(Genotype parentGenome, int startIndex, int endIndex) {
        if (endIndex + 1 - startIndex >= 0)
            System.arraycopy(parentGenome.genes, startIndex, genes, startIndex, endIndex + 1 - startIndex);
    }

    //Accessors
    public int getGeneTypesNumber() {
        return geneTypesNumber;
    }

    public int getGenotypeLength() {
        return genes.length;
    }

    /**
     * Returns a map where directions are keys, adn values
     * represent the number of the gene of the given type in the genome
     *
     * @return A Map object, where keys are of type Direction, and the values are integers
     */
    public Map<Direction, Integer> getGenesCount() {
        Map<Direction, Integer> geneCount = new HashMap<>();

        for (int i = 0; i < getGenotypeLength(); i++) {
            if(geneCount.containsKey(Direction.intToDirection(genes[i]))) {
                geneCount.replace(Direction.intToDirection(genes[i]), geneCount.get(Direction.intToDirection(genes[i])) + 1);
            } else {
                geneCount.put(Direction.intToDirection(genes[i]), 1);
            }
        }

        return geneCount;
    }

    // Methods
    /**
     * Returns a random direction based on the genes in the genotype
     * @return A Direction enum value
     */
    public Direction getRandomDirection() {
        int randomGene = genes[random.nextInt(genes.length)];
        return Direction.intToDirection(randomGene);
    }

    /**
     * Alters the genome so that every gene type appears at least once
     */
    private void repairGenotype() {
        int[] geneTypeCount = new int[geneTypesNumber];

        for(int gene : genes) {
            geneTypeCount[gene] += 1;
        }

        // We find genes that can be replaced
        List<Integer> genesAllowedToChange = new LinkedList<>();

        for(int i = 0; i < genes.length; i++) {
            if(geneTypeCount[genes[i]] > 1) {
                genesAllowedToChange.add(i);
            }
        }

        // Then we find all the non included gene types
        // and replace one of the allowed genes to be of that type
        for(int i = 0; i < geneTypesNumber; i++) {
            if(geneTypeCount[i] == 0) {
                int geneIndexToChange = genesAllowedToChange.get(random.nextInt(genesAllowedToChange.size()));
                genes[geneIndexToChange] = i;
                geneTypeCount[genes[geneIndexToChange]] -= 1;

                if(geneTypeCount[genes[geneIndexToChange]] == 0) {
                    genesAllowedToChange.remove(geneIndexToChange);
                }
            }
        }

        Arrays.sort(genes);
    }

    private void createRandomGenotype() {
        for(int i = 0; i < genes.length; i++) {
            genes[i] = random.nextInt(geneTypesNumber);
        }

        repairGenotype();
    }
}
