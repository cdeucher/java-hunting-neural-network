package com.cabd.hunting;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SaveLoad {
    private NeuralNetwork nn;
    
    private final String file_name = "synapses.txt";
    private final File file = new File(file_name);

    public void setRnn(NeuralNetwork nn) {
        this.nn = nn;
    }
    
    protected boolean fileExists() {        
        return file.exists() && ! file.isDirectory();
    }
    
    protected synchronized  void saveToFile() throws UnsupportedEncodingException {
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file, true))) {
            // Append the current generation at the end of the file
            for(int l = 0; l < nn.genomes_per_generation; l++) {
                writer.print(nn.getFits(l) + " ");
                for(int i = 0; i < nn.number_layers - 1; i++) {
                    for(int j = 0; j < nn.number_neurons[i]; j++) {
                        int m;
                        if(i + 1 != nn.number_layers - 1) {
                            m = nn.number_neurons[i + 1] - 1;
                        }
                        else {
                            m = nn.number_neurons[i + 1];
                        }
                        for(int k = 0; k < m; k++) {
                            writer.print(nn.synapses[l][i][j][k] + " ");
                        }
                    }
                }
            }
            writer.print("\n");
        }catch (Exception ex){
            new RuntimeException(" Cant save the file:"+ ex);
        }
    }
    
    protected synchronized void loadFromFile() throws FileNotFoundException, IOException {
        List<String> values = loadBetterGenerationFromFile();

        int n = 0;
        for(int generation = 0; generation < nn.genomes_per_generation; generation++) {
            nn.setFits(generation, Double.parseDouble(values.get(n)));
            n++;
            for(int layer = 0; layer < nn.number_layers - 1; layer++) {
                for(int j = 0; j < nn.number_neurons[layer]; j++) {
                    int m;
                    if(layer + 1 != nn.number_layers - 1) {
                        m = nn.number_neurons[layer + 1] - 1;
                    }
                    else {
                        m = nn.number_neurons[layer + 1];
                    }
                    for(int k = 0; k < m; k++) {
                        nn.synapses[generation][layer][j][k] = Double.parseDouble(values.get(n));
                        n++;
                    }
                }
            }
        }
    }

    private List<String> loadBetterGenerationFromFile() throws IOException {
        String current_line, betterGeneration = null;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        double maxFitness = 0.0;

        while((current_line = reader.readLine()) != null) {
            List<String> line = Arrays.asList(current_line.trim().split(" "));
            double fitness = Double.parseDouble( line.get(0) );
            if( fitness > maxFitness) {
                betterGeneration = current_line;
                maxFitness = fitness;
            }
        }

        return Arrays.asList(betterGeneration.trim().split(" "));
    }
}
