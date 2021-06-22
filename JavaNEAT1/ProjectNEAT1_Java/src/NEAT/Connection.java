package NEAT;

// Connection Gene class
public class Connection {
    public Node in_node, out_node; // Input and output node
    public float weight = (float)Math.random() * 2 - 1; // Random weight between -1 and 1
    public int inno = -1; // Innovation number (very important)
    public boolean enabled = true; // enabled or not

    // Constructor
    public Connection(Node i, Node o){
        in_node = i;
        out_node = o;
    }

    // Print For debugging
    public void printGene(){
        System.out.println(inno + "] " + in_node.number + " -> " + out_node.number + " " + weight + " " + enabled);
    }

    // Mutate weight
    public void mutateWeight(){
        float rand2 = (float)Math.random();
        // total randomization
        if (rand2 < 0.1) {
            weight = (float)Math.random() * 2 - 1;
        } 
        // Partial random shifting
        else {
            weight += (float)(Math.random() * 2 - 1)/5;
            if(weight > 1){
                weight = 1;
            }
            if(weight < -1){
                weight = -1;
            }
        }
    }

    // Cloning
    public Connection clone(){
        Connection c = new Connection(in_node.clone(), out_node.clone());
        c.enabled = enabled;
        c.inno = inno;
        c.weight = weight;
        return c;
    }
}
