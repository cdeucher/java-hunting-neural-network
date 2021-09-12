package com.cabd.hunting;

public interface RnnBuilder {

    NeuralNetworkBuilder setMinWeigh(double min_weight);

    NeuralNetworkBuilder setMaxWeight(double max_weight);

    NeuralNetworkBuilder setNumberNeurons(int number_neurons[]);

    NeuralNetworkBuilder setGenomesPerGeneration(int genomes_per_generation);

    NeuralNetworkBuilder setRandomMutationProbability(double random_mutation_probability);

    NeuralNetworkBuilder setCrossOverRate(double crossOverRate);

    NeuralNetworkBuilder setSaveLoad(SaveLoad saveLoad);

    NeuralNetwork getRNN();
}
