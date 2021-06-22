import NEAT.*;

// Main class to test
public class App {
    public static void main(String[] args) throws Exception {
        ConnectionHistory ch = new ConnectionHistory(4, 2);
        // Temporary inputs for network
        float[] temp = new float[]{
            (float)Math.random(),
            (float)Math.random(),
            (float)Math.random(),
            (float)Math.random()
        };
        
        // Test genomes
        Genome g1 = new Genome(ch);
        Genome g2 = new Genome(ch);

        // Random mutations
        for(int i = 0; i < 5; i++){
            g1.mutate();
            g2.mutate();
        }
        
        // Printing Genomes
        g1.printGenome();
        g2.printGenome();
        
        // Crossing Over (Works flawlessly)
        Genome g3 = g1.crossover(g2);
        g3.printGenome();
    }
}