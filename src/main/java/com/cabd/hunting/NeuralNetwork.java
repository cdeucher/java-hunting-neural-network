package com.cabd.hunting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.exp;

public class NeuralNetwork {

    private boolean ONLY_EXEC = false;
    private static double CrossOverRate;
    public int number_neurons[];
    public double synapses[][][][];
    private double min_weight, max_weight;
    public double neurons[][];
    public int number_layers;

    public int genomes_per_generation;
    private double randomMutationProbability;
    private double fits[];
    private int current_genome = 0;
    private int maxHasProgressBeforeGiveUp = 40;

    private SaveLoad saveLoad;
    private int hasProgressCount = 0;

    public NeuralNetwork(int number_neurons[], double min_weight, double max_weight, int genomes_per_generation, double randomMutationProbability, double CrossOverRate, SaveLoad saveLoad) {
        this.number_neurons = number_neurons;
        this.min_weight = min_weight;
        this.max_weight = max_weight;

        this.genomes_per_generation = genomes_per_generation;
        this.randomMutationProbability = randomMutationProbability;
        this.CrossOverRate = CrossOverRate;

        this.number_layers = number_neurons.length;
        this.neurons = new double[number_layers][];

        this.saveLoad = saveLoad;
        this.saveLoad.setRnn(this);

        generateNeurons();
        generateFits();
        setBias();
        initSynapsesEmpty();
        initSynapsesRandomlyOrLoadTheBestGeneration();
    }

    public void setOnlyExec(boolean b){
        ONLY_EXEC = b;
    }

    private void generateNeurons() {
        for(int i = 0; i < number_layers; i++) {
            if(i != number_layers - 1) {
                number_neurons[i]++; // The last neuron is the bias.
            }
            neurons[i] = new double[number_neurons[i]];
        }
    }

    private void setBias() {
        for(int i = 0; i < number_layers - 1; i++) {
            neurons[i][number_neurons[i] - 1] = 1;
        }
    }

    protected double[][][][] getSynapses() {
        return synapses;
    }

    protected double[] getSynapses(int currentGenome, int currentLayer, int currentNeuron) {
        return synapses[currentGenome][currentLayer][currentNeuron];
    }

    public double[] learn(double inputs[]) {
        setNeuronsValues(inputs);
        populateNeuronsVsSynapses();
        return getOutputs();
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

    void populateNeuronsVsSynapses() {
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

                neurons[i][j] = activateSigmoid(neurons[i][j]);
            }
        }
    }

    public double[] getOutputs() {
        return neurons[number_layers - 1];
    }

    protected int getCurrentGenome() {
        return current_genome;
    }

    private double activateSigmoid(double x) {
        return 1 / (1 + exp(-x));
    }

    protected double randDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    private void initSynapsesEmpty() {
        synapses = new double[genomes_per_generation][][][];
        for(int currentGenome = 0; currentGenome < genomes_per_generation; currentGenome++) {
            synapses[currentGenome] = new double[number_layers - 1][][];
            for(int currentLayer = 0; currentLayer < number_layers - 1; currentLayer++) {
                synapses[currentGenome][currentLayer] = new double[number_neurons[currentLayer]][];
                for(int currentNeuron = 0; currentNeuron < number_neurons[currentLayer]; currentNeuron++) {
                    if(currentLayer + 1 != number_layers - 1) {
                        synapses[currentGenome][currentLayer][currentNeuron] = new double[number_neurons[currentLayer + 1] - 1];
                    }
                    else {
                        synapses[currentGenome][currentLayer][currentNeuron] = new double[number_neurons[currentLayer + 1]];
                    }
                }
            }
        }
    }

    protected void initSynapsesRandomlyOrLoadTheBestGeneration() {
        if(saveLoad.fileExists()) {
            try {
                saveLoad.loadFromFile();
            } catch(IOException ex) {
                Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            for(int currentGenome = 0; currentGenome < genomes_per_generation; currentGenome++) {
                for(int currentLayer = 0; currentLayer < number_layers - 1; currentLayer++) {
                    for(int currentNeuron = 0; currentNeuron < number_neurons[currentLayer]; currentNeuron++) {
                        int m;
                        if(currentLayer + 1 != number_layers - 1) {
                            m = number_neurons[currentLayer + 1] - 1;
                        }
                        else {
                            m = number_neurons[currentLayer + 1];
                        }
                        for(int currentSynapse = 0; currentSynapse < m; currentSynapse++) {
                            synapses[currentGenome][currentLayer][currentNeuron][currentSynapse] = randDouble(min_weight, max_weight);
                        }
                    }
                }
            }
        }
    }

    public void newGenome(double current_genome_fit) {
        fits[current_genome] = current_genome_fit;

        // If all genomes have been executed, create a new generation
        if(current_genome + 1 == genomes_per_generation) {
            current_genome = 0;
            newGeneration();
        } else {
            current_genome++;
        }
    }

    private void generateFits(){
        fits = new double[genomes_per_generation];
    }

    protected double getFits(int generation){
        return fits[generation];
    }

    protected void setFits(int generation, double value){
        fits[generation] = value;
    }

    private void newGeneration() {
        boolean hasProgress = false;

        if(saveLoad.fileExists() && ONLY_EXEC) {
            try {
                saveLoad.loadFromFile();
            } catch(IOException ex) {
                Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
            for (int i = 0; i < genomes_per_generation; i++) {
                if (fits[i] > 0) {
                    hasProgress = true;
                    hasProgressCount++;
                    break;
                }
            }

            if (hasProgress && hasProgressCount < maxHasProgressBeforeGiveUp) {
                initSynapsesCrossover();
            } else {
                hasProgressCount = 0;
                initSynapsesRandomlyOrLoadTheBestGeneration();
            }
        }
    }

    protected void initSynapsesCrossover() {
        // Sort
        sortTheBestGeneration();
        try {
            saveLoad.saveToFile();
        }
        catch(UnsupportedEncodingException ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        // The best genome is now the first. We mix it with all the other genomes
        double prob_rand;
        for(int genome = 1; genome < genomes_per_generation; genome++) {
            for(int layer = 0; layer < number_layers - 1; layer++) {
                for(int neuron = 0; neuron < number_neurons[layer]; neuron++) {
                    int m;
                    if(layer + 1 != number_layers - 1) {
                        m = number_neurons[layer + 1] - 1;
                    }
                    else {
                        m = number_neurons[layer + 1];
                    }
                    for(int synapse = 0; synapse < m; synapse++) {
                        // If this genome made any progress, mix it with the first genome or generate a new number randomly or keep the current value
                        if(fits[genome] > 0) {
                            prob_rand = randDouble(0, 1);
                            if(prob_rand < randomMutationProbability) {
                                synapses[genome][layer][neuron][synapse] = randDouble(min_weight, max_weight);
                            }
                            else {
                                prob_rand = randDouble(0, 1);
                                if(prob_rand < CrossOverRate) {
                                    synapses[genome][layer][neuron][synapse] = synapses[0][layer][neuron][synapse];
                                }
                                // Else keep the current value (implicit)
                            }
                        }
                        // Else mix it with the first genome or generate a new number randomly
                        else {
                            prob_rand = randDouble(0, 1);
                            if(prob_rand < randomMutationProbability) {
                                synapses[genome][layer][neuron][synapse] = randDouble(min_weight, max_weight);
                            }
                            else {
                                synapses[genome][layer][neuron][synapse] = synapses[0][layer][neuron][synapse];
                            }
                        }
                    }
                }
            }
        }
    }

    private void sortTheBestGeneration() {
        int betterGeneration;
        double fit_temp, synapses_temp[][][];
        for(int generation = 0; generation < genomes_per_generation - 1; generation++) {
            betterGeneration = generation;
            for(int j = generation + 1; j < genomes_per_generation; j++) {
                if(fits[j] > fits[betterGeneration]) {
                    betterGeneration = j;
                }
            }
            if(betterGeneration != generation) {
                fit_temp = fits[generation];
                synapses_temp = synapses[generation];
                fits[generation] = fits[betterGeneration];
                synapses[generation] = synapses[betterGeneration];
                fits[betterGeneration] = fit_temp;
                synapses[betterGeneration] = synapses_temp;
            }
        }
    }

}

