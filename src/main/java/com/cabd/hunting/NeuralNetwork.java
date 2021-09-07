package com.cabd.hunting;

import java.util.Random;

import static java.lang.Math.exp;

public class NeuralNetwork {

    private int number_neurons[];
    private double synapses[][][][];
    private double min_weight, max_weight;
    private double neurons[][];
    private int number_layers;

    private int genomes_per_generation;
    private double random_mutation_probability;
    private double fits[];
    private int current_genome = 0;

    public NeuralNetwork(int number_neurons[], double min_weight, double max_weight, int genomes_per_generation, double random_mutation_probability) {
        this.number_neurons = number_neurons;
        this.min_weight = min_weight;
        this.max_weight = max_weight;

        this.genomes_per_generation = genomes_per_generation;
        this.random_mutation_probability = random_mutation_probability;

        this.number_layers = number_neurons.length;
        this.neurons = new double[number_layers][];

        generateNeurons();
        setBias();
        generateSynapses();
        generateFits();
        initSynapsesRandomly();
    }

    private void generateNeurons() {
        for(int i = 0; i < number_layers; i++) {
            if(i != number_layers - 1) {
                number_neurons[i]++; // The last neuron is the bias.
            }
            neurons[i] = new double[number_neurons[i]];
        }

        System.out.println("");
    }

    private void setBias() {
        for(int i = 0; i < number_layers - 1; i++) {
            neurons[i][number_neurons[i] - 1] = 1;
        }

        System.out.println("");
    }

    private void generateSynapses() {
        synapses = new double[genomes_per_generation][][][];
        for(int k = 0; k < genomes_per_generation; k++) {
            synapses[k] = new double[number_layers - 1][][];
            for(int i = 0; i < number_layers - 1; i++) {
                synapses[k][i] = new double[number_neurons[i]][];
                for(int j = 0; j < number_neurons[i]; j++) {
                    if(i + 1 != number_layers - 1) {
                        synapses[k][i][j] = new double[number_neurons[i + 1] - 1];
                    }
                    else {
                        synapses[k][i][j] = new double[number_neurons[i + 1]];
                    }
                }
            }
        }
    }

    private void generateFits(){
        fits = new double[genomes_per_generation];
    }

    private void initSynapsesRandomly() {
        for(int l = 0; l < genomes_per_generation; l++) {
            for(int i = 0; i < number_layers - 1; i++) {
                for(int j = 0; j < number_neurons[i]; j++) {
                    int m;
                    if(i + 1 != number_layers - 1) {
                        m = number_neurons[i + 1] - 1;
                    }
                    else {
                        m = number_neurons[i + 1];
                    }
                    for(int k = 0; k < m; k++) {
                        synapses[l][i][j][k] = randDouble(min_weight, max_weight);
                    }
                }
            }
        }
    }

    protected double[][][][] getSynapses() {
        return synapses;
    }

    public void setNeuronsValues(double inputs[]) {
        // Copy inputs
        for (int i = 0; i < number_neurons[0] - 1; i++) {
            neurons[0][i] = inputs[i];
        }

        // Init the other neurons to 0
        for (int i = 1; i < number_layers; i++) {
            int m;
            if (i + 1 != number_layers) {
                m = number_neurons[i] - 1;
            } else {
                m = number_neurons[i];
            }
            for (int j = 0; j < m; j++) {
                neurons[i][j] = 0;
            }
        }
    }

    void setneuronsVsSynapses() {
        for(int i = 1; i < number_layers; i++) {
            int m;
            if(i != number_layers - 1) {
                m = number_neurons[i] - 1;
            }
            else {
                m = number_neurons[i];
            }
            for(int j = 0; j < m; j++) {
                for(int k = 0; k < number_neurons[i - 1]; k++) {
                    neurons[i][j] += neurons[i - 1][k] * synapses[current_genome][i - 1][k][j];
                }

                // Activation function
                neurons[i][j] = sigmoid(neurons[i][j]);
            }
        }
    }

    public double[] getOutputs() {
        return neurons[number_layers - 1];
    }

    private double sigmoid(double x) {
        return 1 / (1 + exp(-x));
    }

    private double randDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }
}

