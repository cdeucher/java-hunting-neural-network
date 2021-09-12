package com.cabd.hunting;

public class NeuralNetworkBuilder implements RnnBuilder{

    private int[] number_neurons = {2, 2, 1};
    private double min_weight;
    private double max_weight;
    private int genomes_per_generation;
    private double randomMutationProbability = 0.5;
    private double crossOverRate = 0.2;
    private SaveLoad saveLoad;

    @Override
    public NeuralNetworkBuilder setMinWeigh(double min_weight) {
        this.min_weight = min_weight;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setMaxWeight(double max_weight) {
        this.max_weight = max_weight;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setNumberNeurons(int number_neurons[]) {
        this.number_neurons = number_neurons;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setGenomesPerGeneration(int genomes_per_generation) {
        this.genomes_per_generation = genomes_per_generation;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setRandomMutationProbability(double random_mutation_probability) {
        this.randomMutationProbability = random_mutation_probability;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setCrossOverRate(double crossOverRate) {
        this.crossOverRate = crossOverRate;
        return this;
    }

    @Override
    public NeuralNetworkBuilder setSaveLoad(SaveLoad saveLoad) {
        this.saveLoad = saveLoad;
        return this;
    }

    @Override
    public NeuralNetwork getRNN() {
        return new NeuralNetwork(number_neurons, min_weight, max_weight, genomes_per_generation, randomMutationProbability, crossOverRate, saveLoad);
    }


}
