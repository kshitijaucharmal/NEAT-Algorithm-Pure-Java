package NEAT;
import java.util.ArrayList;

// Node class for NEAT
public class Node{
    public int number, layer;
    public float sum, outputValue; // sum is the total sum, outputValue is activated
    public ArrayList<Connection> inConnections = new ArrayList<Connection>(); // All connections coming to this node

    // Constructor
    public Node(int n, int l){
        number = n;
        layer = l;
    }

    // Clone the node
    public Node clone(){
        Node n = new Node(number, layer);
        n.sum = sum;
        n.outputValue = outputValue;
        return n;
    }

    // Sigmoid activation
    private float activate(float x){
        return 1 / (float)(1 + Math.exp(-x));
    }

    // Calculate the outputValue of Node
    public void calculate(){
        // No need if input layer
        if(layer == 0){
            return;
        }

        // For all Connections 
        for(Connection c : inConnections){
            if(c.enabled){
                sum += c.in_node.outputValue * c.weight;
            }
        }
        
        // Activate the sum
        outputValue = activate(sum);
    }
}