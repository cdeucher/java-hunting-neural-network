package com.cabd.hunting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NeuralNetworkTest {

    private final int neurons_amount[] = {2, 2, 1};
    private final int min_weight = -1;
    private final int max_weight = 1;

    private final int genomes_per_generation = 3;
    private final double random_mutation_probability = 0.5;

    private double inputs[];

    private NeuralNetwork rnn;

    @Before
    public void setUp() {
        rnn = new NeuralNetwork(neurons_amount, min_weight, max_weight, genomes_per_generation, random_mutation_probability);
        inputs = new double[]{130, 120};
    }

    @Test
    public void whenCreateRNN_ShouldPopulateLayersOfRNN() {
         assertEquals(3,rnn.getSynapses().length);
    }

    @Test
    public void whenSetNeuronsValues_ShouldPopulateNeuronsWithRealValues() {
        rnn.setNeuronsValues(inputs);
        rnn.setneuronsVsSynapses();

        assertEquals(1, rnn.getOutputs().length);
        assertFalse(rnn.getOutputs()[0] == 0);
    }


}
