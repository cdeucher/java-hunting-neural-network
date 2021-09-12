package com.cabd.hunting;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NeuralNetworkTest {

    private final int neurons_amount[] = {2, 2, 1};
    private final int min_weight = -1;
    private final int max_weight = 1;
    private final int genomes_per_generation = 3;
    private final double random_mutation_probability = 0.5;
    private final double crossOverRate = 0.2;
    private double inputs[];

    private NeuralNetwork rnn;
    private NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
    private SaveLoad saveLoad = Mockito.mock(SaveLoad.class);

    @Before
    public void setUp() {
        rnn = builder.setMaxWeight(max_weight)
               .setMinWeigh(min_weight)
               .setNumberNeurons(neurons_amount)
               .setGenomesPerGeneration(genomes_per_generation)
               .setRandomMutationProbability(random_mutation_probability)
               .setCrossOverRate(crossOverRate)
               .setSaveLoad(saveLoad)
               .getRNN();
        inputs = new double[]{130, 120};
    }

    @Test
    public void whenCreateRNN_ShouldPopulateLayersOfRNN() {
         assertEquals(3,rnn.getSynapses().length);
    }

    @Test
    public void whenSetNeuronsValues_ShouldPopulateNeuronsWithRealValues() {
        rnn.learn(inputs);

        assertEquals(1, rnn.getOutputs().length);
        assertFalse(rnn.getOutputs()[0] == 0);
    }

    @Test
    public void whenCreateNewGeneration_ShouldPopulateTheGeneration() {
        rnn.learn(inputs);
        rnn.newGenome(0);

        assertEquals(1, rnn.getCurrentGenome());
    }

    @Test
    public void whenCreateMultiplesGenerations_ShouldPopulateAllGenerations() {
        rnn.learn(inputs);
        rnn.newGenome(0);
        rnn.newGenome(0);
        assertEquals(2, rnn.getCurrentGenome());
    }

    @Test
    public void whenCreateNewGenome_ShouldUpdateSynapses() {
        double[][] output = new double[2][];
        rnn.learn(inputs);
        output[0] = rnn.getOutputs().clone();

        rnn.newGenome(0.1);
        rnn.learn(inputs);
        output[1] = rnn.getOutputs();

        assertFalse ( Arrays.equals(output[0],output[1]) ); ;
    }

    @Test
    public void whenCreateGenomesAndApplyCrossover_ShouldUpdateSynapsesAndGenomesOrder() {
        double[][] firstSynapse = new double[2][];
        rnn.learn(inputs);
        firstSynapse[0] = rnn.getSynapses(0,0, 0).clone();

        rnn.newGenome(0.1);
        rnn.learn(inputs);
        rnn.newGenome(0.1);
        rnn.learn(inputs);
        rnn.newGenome(0.5);
        rnn.learn(inputs);

        firstSynapse[1] = rnn.getSynapses(0,0, 0).clone();

        assertFalse ( Arrays.equals(firstSynapse[0],firstSynapse[1]) ); ;
    }
}
