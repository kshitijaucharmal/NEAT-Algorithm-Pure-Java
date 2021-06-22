package NEAT;

import java.util.ArrayList;
import java.util.Random;

// The main brain of NEAT
public class Genome {
    public int n_inputs, n_outputs;
    public int total_nodes = 0; // total number of nodes in genome
    public int highest_inno = 0; // highest innovation number in genome
    public int input_layer = 0; // fixed input and output layer (try to make this dynamic)
    public int output_layer = 10;
    public float fitness = 0; // fitness score
    public float adjusted_fitness = 0f; // fitness after adjusting in species (not yet used)

    // Nodes and genes
    public ArrayList<Node> nodes = new ArrayList<>();
    public ArrayList<Connection> genes = new ArrayList<>();

    // CH to refer
    public ConnectionHistory ch;
    public Random rand = new Random();

    // Constructor
    public Genome(ConnectionHistory ch){
        this.ch = ch;
        n_inputs = ch.n_inputs;
        n_outputs = ch.n_outputs;
        
        // Add Essential Nodes (no bias yet)
        for(int i = 0; i < n_inputs; i++){
            nodes.add(new Node(total_nodes++, input_layer));
        }

        for(int i = 0; i < n_outputs; i++){
            nodes.add(new Node(total_nodes++, output_layer));
        }
    }

    // Connect the genes to the native nodes
    public void connectNodes(){
        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).inConnections.clear();
        }

        for(int i = 0; i < genes.size(); i++){
            Node ino = getNode(genes.get(i).in_node.number);
            Node ono = getNode(genes.get(i).out_node.number);

            genes.get(i).in_node = ino;
            genes.get(i).out_node = ono;
        }

        for(int i = 0; i < genes.size(); i++){
            genes.get(i).out_node.inConnections.add(genes.get(i));
        }
    }

    // Get a node from its number
    private Node getNode(int n){
        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).number == n){
                return nodes.get(i);
            }
        }
        return null;
    }

    // Convert from List to Array
    public float[] cvt(ArrayList<Float> arr){
        float[] s = new float[arr.size()];
        for(int i = 0; i < arr.size(); i++){
            s[i] = arr.get(i);
        }
        return s;
    }

    // Give outputs by analysing inputs
    public float[] predict(float[] inputs){
        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).sum = 0f;
            nodes.get(i).outputValue = 0f;
        }

        for(int i = 0; i < n_inputs; i++){
            nodes.get(i).outputValue = inputs[i];
        }

        ArrayList<Float> outputs = new ArrayList<Float>();
        for(int l = 0; l < output_layer+1; l++){
            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).layer == l){
                    nodes.get(i).calculate();
                }
                if(nodes.get(i).layer == l && l == output_layer){
                    outputs.add(nodes.get(i).outputValue);
                }
            }
        }
        return cvt(outputs);
    }

    // Check gene existence based on innovation number
    public Connection exists(int nn){
        if(genes.size() == 0) return null;

        for(Connection c : genes){
            if(c.inno == nn){
                return c;
            }
        }

        return null;
    }

    // Helper function to connect 2 nodes
    private void addC(Node n1, Node n2){
        if(n1.layer > n2.layer){
            Node temp = n1;
            n1 = n2;
            n2 = temp;
        }

        Connection c = ch.exists(n1, n2);
        Connection x = new Connection(n1, n2);

        if(c != null){
            x.inno = c.inno;
            if(exists(x.inno) == null){
                genes.add(x);
                n2.inConnections.add(x);
                if(x.inno > highest_inno) highest_inno = x.inno;
            }
        }
        else{
            x.inno = ch.global_inno++;
            genes.add(x);
            ch.allConnections.add(x.clone());
            n2.inConnections.add(x);
            if(x.inno > highest_inno) highest_inno = x.inno;
        }
    }

    // Mutate Add Connection
    public void addConnection(){
        Node n1 = nodes.get(rand.nextInt(nodes.size()));
        Node n2 = nodes.get(rand.nextInt(nodes.size()));

        while(n1.layer == n2.layer){
            n1 = nodes.get(rand.nextInt(nodes.size()));
            n2 = nodes.get(rand.nextInt(nodes.size()));
        }

        addC(n1, n2);
    }

    // Mutate Add Node
    public void addNode(){
        if(genes.size() == 0) addConnection();

        nodes.add(new Node(total_nodes++, 1 + rand.nextInt(output_layer-1)));

        Connection r = genes.get(rand.nextInt(genes.size())); // Random connection to insert a node
        addC(nodes.get(nodes.size()-1), r.out_node); // hidden to out
        addC(r.in_node, nodes.get(nodes.size()-1)); // in to hidden
        r.enabled = false; // diable the connection

        genes.get(genes.size()-1).weight = 1;
        genes.get(genes.size()-2).weight = r.weight;

        // Overall, this node adding doesn't change the output (maybe)
    }

    // Elegant mutate function taken from Evan from "CODE BULLET" (adapted to work with my implementation)
    public void mutate(){
        if (genes.size() == 0) {
            addConnection();
        }

        float rand1 = (float)Math.random();
        if (rand1<0.8) { // 80% of the time mutate weights
            for (int i = 0; i< genes.size(); i++) {
                genes.get(i).mutateWeight();
            }
        }
        //5% of the time add a new connection
        float rand2 = (float)Math.random();
        if (rand2<0.08) {
            addConnection();
        }
        //1% of the time add a node
        float rand3 = (float)Math.random();
        if (rand3<0.02) {
            addNode();
        }
    }

    // Crossover between two Genomes (Works flawlessly now) (There are elements for specieation, but I haven't figured that out yet)
    public Genome crossover(Genome partner){
        Genome child = new Genome(ch); // New child with no nodes and genes
        child.nodes.clear();
        child.genes.clear();
        child.total_nodes = 0;

        // Just to be a little cleaner
        Genome parent1 = this;
        Genome parent2 = partner;

        // Parent2 should have the greatest fitness
        if(fitness > partner.fitness){
            Genome temp = parent1;
            parent1 = parent2;
            parent2 = temp;
        }
        child.highest_inno = parent2.highest_inno;

        int total; // total number of innovation

        if(parent1.highest_inno > parent2.highest_inno) total = parent1.highest_inno+1;
        else total = parent2.highest_inno + 1;

        int matching = 0; // matching genes
        int disjoint = 0; // disjoint genes
        int excess = 0;   // excess genes

        // Adding genes according to innovation (a little crude, but works)
        for(int i = 0; i < parent2.highest_inno+1; i++){
            Connection p1e = parent1.exists(i);
            Connection p2e = parent2.exists(i);

            if(p1e != null && p2e != null){
                matching++;
                if(Math.random() < 0.5f) child.genes.add(p1e.clone());
                else child.genes.add(p2e.clone());
            }
            else{
                if(p1e != null){
                    disjoint++;
                    child.genes.add(p1e.clone());
                }
                else if(p2e != null){
                    disjoint++;
                    child.genes.add(p2e.clone());
                }
            }
        }

        // Debugging statements------------------------------
        System.out.println("Cross over results : ");
        System.out.println("Total genes " + total);
        System.out.println("Matching genes " + matching);
        System.out.println("Disjoint genes " + disjoint);
        excess = total - disjoint - matching;
        if(excess < 1) excess = 0;
        System.out.println("Excess genes " + excess);
        // --------------------------------------------------

        // Give most nodes to child--------------------------
        if(parent2.total_nodes > parent1.total_nodes){
            for(int i = 0; i < parent2.nodes.size(); i++){
                child.nodes.add(parent2.nodes.get(i).clone());
                child.total_nodes++;
            }
        }
        else{
            for(int i = 0; i < parent1.nodes.size(); i++){
                child.nodes.add(parent1.nodes.get(i).clone());
                child.total_nodes++;
            }
        }
        // --------------------------------------------------
        // Connect the childs genes to its own nodes ( Very freakin important !!!)
        child.connectNodes();
        return child;
    }

    // Printing all Information about the Genome (Debugging)
    public void printGenome(){
        System.out.println("Genome");
        System.out.println("--------------------------------------------");
        for(Connection c : genes){
            c.printGene();
        }
        System.out.println("Fitness : " + fitness);
        System.out.println("Highest Inno : " + highest_inno);
        System.out.println("Total Nodes : " + total_nodes);
        System.out.println("--------------------------------------------");
    }
}
