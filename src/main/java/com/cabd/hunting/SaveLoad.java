package com.cabd.hunting;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SaveLoad {
    private NeuralNetwork nn;
    
    private final String file_name = "synapses.txt";
    private final File file = new File(file_name);
    private boolean first_line = true;

    public void setRnn(NeuralNetwork nn) {
        this.nn = nn;
    }
    
    protected boolean fileExists() {        
        return file.exists() && ! file.isDirectory();
    }
    
    protected void saveToFile() throws FileNotFoundException, UnsupportedEncodingException {
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file, true))) {
            if(! first_line) {
                writer.print("\n");
            }
            else {
                first_line = false;
            }
            
            // Append the current generation at the end of the file
            for(int l = 0; l < nn.genomes_per_generation; l++) {
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
        }
    }
    
    protected void loadFromFile() throws FileNotFoundException, IOException {
        String current_line, last_generation = null;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        
        // Get the last line and store it into last_generation
        while((current_line = reader.readLine()) != null) {
            last_generation = current_line;
        }
        
        // Split the string and store the numbers (as text)
        List<String> values = Arrays.asList(last_generation.trim().split(" "));
        
        // Init the synapsis
        int n = 0;
        for(int l = 0; l < nn.genomes_per_generation; l++) {
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
                        nn.synapses[l][i][j][k] = Double.parseDouble(values.get(n));
                        n++;
                    }
                }
            }
        }
    }
}