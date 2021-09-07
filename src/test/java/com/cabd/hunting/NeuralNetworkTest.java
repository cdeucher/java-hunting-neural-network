package com.cabd.hunting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NeuralNetworkTest {

    private final int neurons_amount[] = {2, 2, 1};
    private final int min_weight = -1;
    private final int max_weight = 1;

    private final int genomes_per_generation = 3;
    private final double random_mutation_probability = 0.5;

    private NeuralNetwork rnn;

    @Before
    public void setUp() {
        rnn = new NeuralNetwork(neurons_amount, min_weight, max_weight, genomes_per_generation, random_mutation_probability);
    }

    @Test
    public void whenCreateRNN_ShouldCreateRNN() {
         assertEquals(3,rnn.getSynapses().length);
    }
}
